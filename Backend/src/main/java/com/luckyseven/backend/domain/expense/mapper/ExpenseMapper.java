package com.luckyseven.backend.domain.expense.mapper;

import com.luckyseven.backend.domain.expense.dto.ExpenseRequest;
import com.luckyseven.backend.domain.expense.dto.ExpenseResponse;
import com.luckyseven.backend.domain.expense.entity.Expense;

public class ExpenseMapper {

    private ExpenseMapper() {
    }

    public static Expense toExpense(ExpenseRequest request, Long teamId) {
        return Expense.builder()
            .description(request.getDescription())
            .amount(request.getAmount())
            .category(request.getCategory())
            .payerId(request.getPayerId())
            .teamId(teamId)
            .build();
    }

    public static ExpenseResponse toExpenseResponse(Expense expense) {
        return ExpenseResponse.builder()
            .amount(expense.getAmount())
            .expenseId(expense.getId())
            // TODO: foreignBalance, balance 추가 작성
            .foreignBalance(null)
            .balance(null)
            .createdAt(expense.getCreatedAt())
            .updatedAt(expense.getUpdatedAt())
            .build();
    }
}
