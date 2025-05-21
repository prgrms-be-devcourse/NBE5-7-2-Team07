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
import com.luckyseven.backend.domain.expense.enums.PaymentMethod;
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
    Member payer = memberService.findMemberOrThrow(request.payerId());
    Budget budget = team.getBudget();

    if (request.paymentMethod() == PaymentMethod.CASH) {
      // 외화 결제: 외화 및 원화 잔액 차감
      budget.debitForeign(request.amount());
    } else {
      // 카드 결제: 원화 잔액 차감
      budget.debitKrw(request.amount());
    }

    Expense expense = ExpenseMapper.fromExpenseRequest(request, team, payer);
    Expense saved = expenseRepository.save(expense);

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
    BigDecimal originalAmount = expense.getAmount();
    BigDecimal newAmount = request.amount();
    BigDecimal delta = newAmount.subtract(originalAmount);

    Budget budget = expense.getTeam().getBudget();
    if (delta.compareTo(BigDecimal.ZERO) > 0) {
      // 금액 증가 시 원화 잔액 차감
      budget.debitKrw(delta);
    } else if (delta.compareTo(BigDecimal.ZERO) < 0) {
      // 금액 감소 시 줄어든 만큼 원화 환불
      budget.creditKrw(delta.abs());
    }

    expense.update(request.description(), newAmount, request.category());
    evictRecentExpensesForTeam(expense.getTeam().getId());
    return ExpenseMapper.toCreateExpenseResponse(expense, budget);
  }

  @Transactional
  public ExpenseBalanceResponse deleteExpense(Long expenseId) {
    Expense expense = findExpenseOrThrow(expenseId);
    Long teamId = expense.getTeam().getId();
    Budget budget = expense.getTeam().getBudget();

    // 지출 삭제 시 원화 환불
    budget.creditKrw(expense.getAmount());
    expenseRepository.delete(expense);

    evictRecentExpensesForTeam(teamId);
    return ExpenseMapper.toExpenseBalanceResponse(budget);
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

}
