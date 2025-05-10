package com.luckyseven.backend.domain.expense.mapper;

import com.luckyseven.backend.domain.expense.dto.CreateExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseBalanceResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseRequest;
import com.luckyseven.backend.domain.expense.dto.ExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.SettlerResponse;
import com.luckyseven.backend.domain.expense.entity.Expense;
import com.luckyseven.backend.domain.expense.util.TempBudget;
import com.luckyseven.backend.domain.expense.util.TempMember;
import com.luckyseven.backend.domain.expense.util.TempSettlement;
import com.luckyseven.backend.domain.expense.util.TempTeam;
import java.util.List;

public class ExpenseMapper {

  private ExpenseMapper() {
  }

  public static Expense toExpense(ExpenseRequest request, TempTeam team, TempMember payer) {
    return Expense.builder()
        .description(request.getDescription())
        .amount(request.getAmount())
        .paymentMethod(request.getPaymentMethod())
        .category(request.getCategory())
        .payer(payer)
        .team(team)
        .build();
  }

  public static CreateExpenseResponse toCreateExpenseResponse(Expense expense, TempBudget budget) {
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

  public static ExpenseResponse toExpenseResponse(Expense expense,
      List<TempSettlement> settlements) {
    List<SettlerResponse> settlerResponses = settlements.stream()
        .map(ts -> SettlerResponse.builder()
            .id(ts.getSettler().getId())
            .nickname(ts.getSettler().getNickname())
            .build())
        .toList();

    return ExpenseResponse.builder()
        .description(expense.getDescription())
        .amount(expense.getAmount())
        .category(expense.getCategory())
        .payerId(expense.getPayer().getId())
        .payerNickname(expense.getPayer().getNickname())
        .createdAt(expense.getCreatedAt())
        .updatedAt(expense.getUpdatedAt())
        .paymentMethod(expense.getPaymentMethod())
        .settlers(settlerResponses)
        .build();
  }
}
