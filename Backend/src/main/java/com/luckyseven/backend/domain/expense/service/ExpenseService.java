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
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.repository.TeamRepository;
import com.luckyseven.backend.sharedkernel.dto.PageResponse;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseRepository expenseRepository;

  // TODO: TEMP 엔티티 수정
  private final TeamRepository teamRepository;
  private final MemberRepository memberRepository;

  @Transactional
  public CreateExpenseResponse saveExpense(Long teamId, ExpenseRequest request) {
    Team team = findTeamWithBudgetOrThrow(teamId);
    Member payer = findPayerOrThrow(request.payerId());

    Budget budget = team.getBudget();
    validateSufficientBudget(request.amount(), budget.getBalance());

    Expense expense = ExpenseMapper.fromExpenseRequest(request, team, payer);
    Expense saved = expenseRepository.save(expense);

    budget.updateBalance(budget.getBalance().subtract(request.amount()));

    // TODO: 정산 저장 로직 추가
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
    Expense expense = findExpenseOrThrow(expenseId);
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
    Expense expense = findExpenseOrThrow(expenseId);
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

  // TODO: 논의 후 삭제 예정
  public Team findTeamOrThrow(Long teamId) {
    return teamRepository.findById(teamId)
        .orElseThrow(() -> new CustomLogicException(TEAM_NOT_FOUND));
  }

  private Team findTeamWithBudgetOrThrow(Long teamId) {
    return teamRepository.findTeamWithBudget(teamId)
        .orElseThrow(() -> new CustomLogicException(TEAM_NOT_FOUND));
  }

  private Member findPayerOrThrow(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_PAYER_NOT_FOUND));
  }

  private Expense findExpenseOrThrow(Long expenseId) {
    return expenseRepository.findByIdWithTeamAndBudget(expenseId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_NOT_FOUND));
  }

  private void validateSufficientBudget(BigDecimal amount, BigDecimal balance) {
    if (balance.compareTo(amount) < 0) {
      throw new CustomLogicException(INSUFFICIENT_BALANCE);
    }
  }
}
