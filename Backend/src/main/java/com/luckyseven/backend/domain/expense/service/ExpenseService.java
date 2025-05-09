package com.luckyseven.backend.domain.expense.service;

import com.luckyseven.backend.domain.expense.dto.ExpenseRequest;
import com.luckyseven.backend.domain.expense.dto.ExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseUpdateRequest;
import com.luckyseven.backend.domain.expense.entity.Expense;
import com.luckyseven.backend.domain.expense.mapper.ExpenseMapper;
import com.luckyseven.backend.domain.expense.repository.ExpenseRepository;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;


    @Transactional
    public ExpenseResponse saveExpense(Long teamId, ExpenseRequest request) {
        // Todo: 팀/결제자/정산 대상자 검증(추후 팀원들 로직 참고)
        Expense expense = ExpenseMapper.toExpense(request, teamId);
        Expense saved = expenseRepository.save(expense);
        return ExpenseMapper.toExpenseResponse(saved);
    }

    @Transactional
    public ExpenseResponse updateExpense(Long expenseId,
        ExpenseUpdateRequest request) {

        Expense expense = getExpenseById(expenseId);

        expense.update(request.getDescription(), request.getAmount(), request.getCategory());

        return ExpenseMapper.toExpenseResponse(expense);
    }

    private Expense getExpenseById(Long expenseId) {
        return expenseRepository.findById(expenseId)
            .orElseThrow(() -> new CustomLogicException(ExceptionCode.EXPENSE_NOT_FOUND));
    }
}
