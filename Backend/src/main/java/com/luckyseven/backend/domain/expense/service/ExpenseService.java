package com.luckyseven.backend.domain.expense.service;

import static com.luckyseven.backend.sharedkernel.exception.ExceptionCode.EXPENSE_NOT_FOUND;
import static com.luckyseven.backend.sharedkernel.exception.ExceptionCode.EXPENSE_PAYER_NOT_FOUND;
import static com.luckyseven.backend.sharedkernel.exception.ExceptionCode.INSUFFICIENT_BALANCE;
import static com.luckyseven.backend.sharedkernel.exception.ExceptionCode.TEAM_NOT_FOUND;

import com.luckyseven.backend.domain.budget.entity.Budget;
import com.luckyseven.backend.domain.expense.dto.CreateExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseBalanceResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseRequest;
import com.luckyseven.backend.domain.expense.dto.ExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseUpdateRequest;
import com.luckyseven.backend.domain.expense.entity.Expense;
import com.luckyseven.backend.domain.expense.mapper.ExpenseMapper;
import com.luckyseven.backend.domain.expense.repository.ExpenseRepository;
import com.luckyseven.backend.domain.member.entity.Member;
import com.luckyseven.backend.domain.member.repository.MemberRepository;
import com.luckyseven.backend.domain.settlements.app.SettlementService;
import com.luckyseven.backend.domain.settlements.dto.SettlementCreateRequest;
import com.luckyseven.backend.domain.settlements.util.SettlementMapper;
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.repository.TeamRepository;
import com.luckyseven.backend.sharedkernel.dto.PageResponse;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final TeamRepository teamRepository;
  private final MemberRepository memberRepository;
  private final SettlementService settlementService;

  @Transactional
  public CreateExpenseResponse saveExpense(Long teamId, ExpenseRequest request) {
    StopWatch sw = new StopWatch("saveExpense");

    sw.start("findTeam");
    Team team = findTeamWithBudgetOrThrow(teamId);
    sw.stop();

    sw.start("findPayer");
    Member payer = findPayerOrThrow(request.payerId());
    sw.stop();

    Budget budget = team.getBudget();
    validateSufficientBudget(request.amount(), budget.getBalance());

    sw.start("saveExpenseEntity");
    Expense expense = ExpenseMapper.fromExpenseRequest(request, team, payer);
    Expense saved = expenseRepository.save(expense);
    sw.stop();

    sw.start("updateBudget");
    budget.updateBalance(budget.getBalance().subtract(request.amount()));
    sw.stop();

    sw.start("createSettlements");
    createSettlement(request, payer, saved);
    sw.stop();

    log.info(sw.prettyPrint());

    return ExpenseMapper.toCreateExpenseResponse(saved, budget);
  }


  @Transactional(readOnly = true)
  public ExpenseResponse getExpense(Long expenseId) {
    Expense expense = findExpenseOrThrow(expenseId);
    return ExpenseMapper.toExpenseResponse(expense);
  }

  @Transactional(readOnly = true)
  public PageResponse<ExpenseResponse> getListExpense(Long teamId, Pageable pageable) {

    validateTeamExists(teamId);

    Page<Expense> expensePage = expenseRepository.findByTeamId(teamId, pageable);

    return ExpenseMapper.toPageResponse(expensePage);
  }


  @Transactional
  public CreateExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest request) {
    Expense expense = findExpenseWithTeamBudget(expenseId);
    BigDecimal originalAmount = expense.getAmount();
    BigDecimal newAmount = request.amount();

    Budget budget = expense.getTeam().getBudget();
    BigDecimal delta = newAmount.subtract(originalAmount);
    if (delta.compareTo(BigDecimal.ZERO) > 0) {
      validateSufficientBudget(delta, budget.getBalance());
    }

    expense.update(request.description(), newAmount, request.category());
    budget.updateBalance(budget.getBalance().subtract(delta));

    return ExpenseMapper.toCreateExpenseResponse(expense, budget);
  }

  @Transactional
  public ExpenseBalanceResponse deleteExpense(Long expenseId) {
    Expense expense = findExpenseWithTeamBudget(expenseId);
    Budget budget = expense.getTeam().getBudget();

    budget.updateBalance(budget.getBalance().add(expense.getAmount()));
    expenseRepository.delete(expense);

    return ExpenseMapper.toExpenseBalanceResponse(budget);
  }

  private void validateTeamExists(Long teamId) {
    if (!teamRepository.existsById(teamId)) {
      throw new CustomLogicException(TEAM_NOT_FOUND);
    }
  }

  private Expense findExpenseWithTeamBudget(Long expenseId) {
    return expenseRepository.findWithTeamAndBudgetById(expenseId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  Team findTeamWithBudgetOrThrow(Long teamId) {
    return teamRepository.findTeamWithBudget(teamId)
        .orElseThrow(() -> new CustomLogicException(TEAM_NOT_FOUND));
  }

  private Member findPayerOrThrow(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_PAYER_NOT_FOUND));
  }

  private Expense findExpenseOrThrow(Long expenseId) {
    return expenseRepository.findByIdWithPayer(expenseId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_NOT_FOUND));
  }

  private void createSettlement(ExpenseRequest request, Member payer, Expense saved) {
    int totalMembers = request.settlerId().size();
    BigDecimal shareAmount = request.amount()
        .divide(BigDecimal.valueOf(totalMembers), RoundingMode.HALF_UP);

    for (Long settlerId : request.settlerId()) {
      if (settlerId.equals(request.payerId())) {
        continue;
      }
      SettlementCreateRequest settleReq = SettlementMapper.toSettlementCreateRequest(
          saved,
          request.payerId(),
          settlerId,
          shareAmount
      );
      settlementService.createSettlement(settleReq, payer, saved);
    }
  }

  private void validateSufficientBudget(BigDecimal amount, BigDecimal balance) {
    if (balance.compareTo(amount) < 0) {
      throw new CustomLogicException(INSUFFICIENT_BALANCE);
    }
  }
}
