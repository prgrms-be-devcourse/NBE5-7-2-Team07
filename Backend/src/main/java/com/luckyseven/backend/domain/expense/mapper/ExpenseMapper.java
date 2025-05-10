package com.luckyseven.backend.domain.expense.mapper;

import com.luckyseven.backend.domain.expense.dto.CreateExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseBalanceResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseRequest;
import com.luckyseven.backend.domain.expense.entity.Expense;
import com.luckyseven.backend.domain.expense.util.TempBudget;
import com.luckyseven.backend.domain.expense.util.TempMember;
import com.luckyseven.backend.domain.expense.util.TempTeam;

public class ExpenseMapper {

  private ExpenseMapper() {
  }

  public static Expense toExpense(ExpenseRequest request, TempTeam team, TempMember payer) {
    return Expense.builder()
        .description(request.getDescription())
        .amount(request.getAmount())
        .category(request.getCategory())
        .payer(payer)
        .team(team)
        .build();
  }

  public static CreateExpenseResponse toExpenseResponse(Expense expense, TempBudget budget) {
    return CreateExpenseResponse.builder()
        .amount(expense.getAmount())
        .expenseId(expense.getId())
        .foreignBalance(budget.getForeignBalance())
        .balance(budget.getBalance())
        .createdAt(expense.getCreatedAt())
        .updatedAt(expense.getUpdatedAt())
        .build();
  }

  public static ExpenseBalanceResponse toExpenseBalanceResponce(TempBudget budget) {
    return ExpenseBalanceResponse
        .builder()
        .balance(budget.getBalance())
        .foreignBalance(budget.getForeignBalance())
        .build();
  }
}
