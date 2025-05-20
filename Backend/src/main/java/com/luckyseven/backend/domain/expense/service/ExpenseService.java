package com.luckyseven.backend.domain.expense.service;

import static com.luckyseven.backend.sharedkernel.exception.ExceptionCode.EXPENSE_NOT_FOUND;
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
import com.luckyseven.backend.sharedkernel.cache.CacheEvictService;
import com.luckyseven.backend.sharedkernel.dto.PageResponse;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "recentExpenses")
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final TeamRepository teamRepository;
  private final MemberService memberService;
  private final SettlementService settlementService;
  private final CacheEvictService cacheEvictService;

  @Transactional
  public CreateExpenseResponse saveExpense(Long teamId, ExpenseRequest request) {
    Team team = findTeamOrThrow(teamId);
    Member payer = findMemberOrThrow(request);
    Budget budget = team.getBudget();

    budget.debit(request.amount());
    Expense saved = expenseRepository.save(
        ExpenseMapper.fromExpenseRequest(request, team, payer)
    );
    settlementService.createAllSettlements(request, payer, saved);

    evictRecentExpensesForTeam(teamId);
    return ExpenseMapper.toCreateExpenseResponse(saved, budget);
  }

  @Transactional(readOnly = true)
  public ExpenseResponse getExpense(Long expenseId) {
    Expense expense = findExpenseOrThrowWithPayer(expenseId);
    return ExpenseMapper.toExpenseResponse(expense);
  }

  @Transactional(readOnly = true)
  @Cacheable(key = "'team:' + #teamId + ':page:' + #pageable.pageNumber + ':size:' + #pageable.pageSize")
  public PageResponse<ExpenseResponse> getListExpense(Long teamId, Pageable pageable) {
    validateTeamExists(teamId);
    Page<ExpenseResponse> page = expenseRepository.findResponsesByTeamId(teamId, pageable);
    return ExpenseMapper.toPageResponse(page);
  }

  @Transactional
  public CreateExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest request) {
    Expense expense = findExpenseOrThrow(expenseId);
    BigDecimal delta = request.amount().subtract(expense.getAmount());
    Budget budget = expense.getTeam().getBudget();

    adjustBudget(delta, budget);
    expense.update(request.description(), request.amount(), request.category());

    evictRecentExpensesForTeam(expense.getTeam().getId());
    return ExpenseMapper.toCreateExpenseResponse(expense, budget);
  }

  @Transactional
  public ExpenseBalanceResponse deleteExpense(Long expenseId) {
    Expense expense = findExpenseOrThrow(expenseId);
    Long teamId = expense.getTeam().getId();

    Budget budget = expense.getTeam().getBudget();
    budget.credit(expense.getAmount());
    expenseRepository.delete(expense);

    evictRecentExpensesForTeam(teamId);
    return ExpenseMapper.toExpenseBalanceResponse(budget);
  }

  private void adjustBudget(BigDecimal delta, Budget budget) {
    if (delta == null || delta.compareTo(BigDecimal.ZERO) == 0) {
      return;
    }
    if (delta.compareTo(BigDecimal.ZERO) > 0) {
      budget.debit(delta);
    } else {
      budget.credit(delta.abs());
    }
  }

  private void evictRecentExpensesForTeam(Long teamId) {
    cacheEvictService.evictByPrefix("recentExpenses", "team:" + teamId + ":");
  }

  private Expense findExpenseOrThrowWithPayer(Long expenseId) {
    return expenseRepository.findByIdWithPayer(expenseId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_NOT_FOUND));
  }

  private Expense findExpenseOrThrow(Long expenseId) {
    return expenseRepository.findWithTeamAndBudgetById(expenseId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_NOT_FOUND));
  }

  private void validateTeamExists(Long teamId) {
    if (!teamRepository.existsById(teamId)) {
      throw new CustomLogicException(TEAM_NOT_FOUND);
    }
  }

  private Team findTeamOrThrow(Long teamId) {
    return teamRepository.findTeamWithBudget(teamId)
        .orElseThrow(() -> new CustomLogicException(TEAM_NOT_FOUND));
  }

  private Member findMemberOrThrow(ExpenseRequest request) {
    return memberService.findMemberOrThrow(request.payerId());
  }
}