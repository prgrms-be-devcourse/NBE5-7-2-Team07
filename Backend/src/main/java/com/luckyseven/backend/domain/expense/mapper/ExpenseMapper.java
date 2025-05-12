package com.luckyseven.backend.domain.expense.mapper;

import com.luckyseven.backend.domain.expense.dto.CreateExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseBalanceResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseListResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseRequest;
import com.luckyseven.backend.domain.expense.dto.ExpenseResponse;
import com.luckyseven.backend.domain.expense.entity.Expense;
import com.luckyseven.backend.domain.expense.util.TempBudget;
import com.luckyseven.backend.domain.expense.util.TempMember;
import com.luckyseven.backend.domain.expense.util.TempTeam;
import java.util.List;
import org.springframework.data.domain.Page;

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

  public static ExpenseBalanceResponse toExpenseBalanceResponse(TempBudget budget) {
    return ExpenseBalanceResponse
        .builder()
        .balance(budget.getBalance())
        .foreignBalance(budget.getForeignBalance())
        .build();
  }

  public static ExpenseResponse toExpenseResponse(Expense expense) {
    return ExpenseResponse.builder()
        .id(expense.getId())
        .description(expense.getDescription())
        .amount(expense.getAmount())
        .category(expense.getCategory())
        .payerId(expense.getPayer().getId())
        .payerNickname(expense.getPayer().getNickname())
        .createdAt(expense.getCreatedAt())
        .updatedAt(expense.getUpdatedAt())
        .paymentMethod(expense.getPaymentMethod())
        .build();
  }

  public static ExpenseListResponse toExpenseListResponse(
      List<ExpenseResponse> content,
      Page<Expense> page
  ) {
    return ExpenseListResponse.builder()
        .content(content)
        .page(page.getNumber())
        .size(page.getSize())
        .totalPages(page.getTotalPages())
        .totalElements(page.getTotalElements())
        .build();
  }
}
