package com.luckyseven.backend.domain.expense.controller;

import com.luckyseven.backend.domain.expense.dto.ExpenseRequest;
import com.luckyseven.backend.domain.expense.dto.ExpenseResponse;
import com.luckyseven.backend.domain.expense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Expense", description = "지출 내역 관리 API")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(summary = "지출 내역 등록")
    @PostMapping("/{teamId}/expense")
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createExpense(
        @PathVariable Long teamId,
        @RequestBody @Valid ExpenseRequest request
    ) {
        return expenseService.saveExpense(teamId, request);
    }

}
