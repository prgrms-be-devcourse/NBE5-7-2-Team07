package com.luckyseven.backend.domain.expense.service;

import static com.luckyseven.backend.sharedkernel.exception.ExceptionCode.EXPENSE_NOT_FOUND;
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
import com.luckyseven.backend.domain.member.service.MemberService;
import com.luckyseven.backend.domain.settlements.app.SettlementService;
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.repository.TeamRepository;
import com.luckyseven.backend.sharedkernel.dto.PageResponse;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final TeamRepository teamRepository;
  private final MemberService memberService;
  private final SettlementService settlementService;


  @Transactional
  @CacheEvict(value = "recentExpenses", allEntries = true)
  public CreateExpenseResponse saveExpense(Long teamId, ExpenseRequest request) {
    Team team = findTeamOrThrow(teamId);

    Member payer = memberService.findMemberOrThrow(request.payerId());
    Budget budget = team.getBudget();
    validateSufficientBudget(request.amount(), budget.getBalance());

    Expense expense = ExpenseMapper.fromExpenseRequest(request, team, payer);
    Expense saved = expenseRepository.save(expense);

    // TODO: 낙관적(Lock) 고려
    budget.updateBalance(budget.getBalance().subtract(request.amount()));
    settlementService.createAllSettlements(request, payer, saved);

    return ExpenseMapper.toCreateExpenseResponse(saved, budget);
  }

  @Transactional(readOnly = true)
  public ExpenseResponse getExpense(Long expenseId) {
    Expense expense = expenseRepository.findByIdWithPayer(expenseId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_NOT_FOUND));
    return ExpenseMapper.toExpenseResponse(expense);
  }

  // TODO: 캐시 튜닝 필요

  @Transactional(readOnly = true)
  @Cacheable(
      value = "recentExpenses",
      key = "'team:' + #teamId + ':page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize"
  )
  public PageResponse<ExpenseResponse> getListExpense(Long teamId, Pageable pageable) {

    validateTeamExists(teamId);

    Page<ExpenseResponse> page = expenseRepository.findResponsesByTeamId(teamId, pageable);
    return ExpenseMapper.toPageResponse(page);
  }

  @Transactional
  @CacheEvict(value = "recentExpenses", allEntries = true)
  public CreateExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest request) {
    Expense expense = expenseRepository.findWithTeamAndBudgetById(expenseId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_NOT_FOUND));

    BigDecimal original = expense.getAmount();
    BigDecimal updated = request.amount();
    Budget budget = expense.getTeam().getBudget();
    BigDecimal delta = updated.subtract(original);
    if (delta.compareTo(BigDecimal.ZERO) > 0) {
      validateSufficientBudget(delta, budget.getBalance());
    }

    expense.update(request.description(), updated, request.category());
    budget.updateBalance(budget.getBalance().subtract(delta));

    return ExpenseMapper.toCreateExpenseResponse(expense, budget);
  }

  @Transactional
  @CacheEvict(value = "recentExpenses", allEntries = true)
  public ExpenseBalanceResponse deleteExpense(Long expenseId) {
    Expense expense = expenseRepository.findWithTeamAndBudgetById(expenseId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_NOT_FOUND));

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

  private void validateSufficientBudget(BigDecimal amount, BigDecimal balance) {
    if (balance.compareTo(amount) < 0) {
      throw new CustomLogicException(INSUFFICIENT_BALANCE);
    }
  }

  private Team findTeamOrThrow(Long teamId) {
    return teamRepository.findTeamWithBudget(teamId)
        .orElseThrow(() -> new CustomLogicException(TEAM_NOT_FOUND));
  }
}
