package com.luckyseven.backend.domain.expense.service;

import com.luckyseven.backend.domain.expense.dto.CreateExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseBalanceResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseRequest;
import com.luckyseven.backend.domain.expense.dto.ExpenseUpdateRequest;
import com.luckyseven.backend.domain.expense.entity.Expense;
import com.luckyseven.backend.domain.expense.mapper.ExpenseMapper;
import com.luckyseven.backend.domain.expense.repository.ExpenseRepository;
import com.luckyseven.backend.domain.expense.util.TempBudget;
import com.luckyseven.backend.domain.expense.util.TempMember;
import com.luckyseven.backend.domain.expense.util.TempTeam;
import com.luckyseven.backend.domain.expense.util.TempTeamRepository;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseRepository expenseRepository;

  // TODO: TEMP 엔티티 수정
  private final TempTeamRepository teamRepository;

  @Transactional
  public CreateExpenseResponse saveExpense(Long teamId, ExpenseRequest request) {
    TempTeam team = findTeamOrThrow(teamId);
    TempBudget budget = team.getBudget();

    validateSufficientBudget(request.getAmount(), budget.getBalance());

    TempMember payer = new TempMember();  // TODO: 시큐리티 설정 보고 수정
    Expense expense = ExpenseMapper.toExpense(request, team, payer);
    Expense saved = expenseRepository.save(expense);

    budget.updateBalance(budget.getBalance().subtract(request.getAmount()));

    return ExpenseMapper.toExpenseResponse(saved, budget);
  }

  @Transactional
  public CreateExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest request) {
    Expense expense = findExpenseOrThrow(expenseId);
    BigDecimal previous = expense.getAmount();
    BigDecimal updated = request.getAmount();

    TempBudget budget = expense.getTeam().getBudget();
    BigDecimal delta = updated.subtract(previous);
    if (delta.compareTo(BigDecimal.ZERO) > 0) {
      validateSufficientBudget(delta, budget.getBalance());
    }

    expense.update(request.getDescription(), updated, request.getCategory());
    budget.updateBalance(budget.getBalance().subtract(delta));

    return ExpenseMapper.toExpenseResponse(expense, budget);
  }

  @Transactional
  public ExpenseBalanceResponse deleteExpense(Long expenseId) {
    Expense expense = findExpenseOrThrow(expenseId);
    TempBudget budget = expense.getTeam().getBudget();

    budget.updateBalance(budget.getBalance().add(expense.getAmount()));
    expenseRepository.delete(expense);

    return ExpenseMapper.toExpenseBalanceResponce(budget);
  }


  private TempTeam findTeamOrThrow(Long teamId) {
    return teamRepository.findById(teamId)
        .orElseThrow(() -> new CustomLogicException(ExceptionCode.TEAM_NOT_FOUND));
  }

  private Expense findExpenseOrThrow(Long id) {
    return expenseRepository.findById(id)
        .orElseThrow(() -> new CustomLogicException(ExceptionCode.EXPENSE_NOT_FOUND));
  }

  private void validateSufficientBudget(BigDecimal amount, BigDecimal balance) {
    if (balance.compareTo(amount) < 0) {
      throw new CustomLogicException(ExceptionCode.INSUFFICIENT_BALANCE);
    }
  }
}
