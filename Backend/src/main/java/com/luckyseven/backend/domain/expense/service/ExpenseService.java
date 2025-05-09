package com.luckyseven.backend.domain.expense.service;

import com.luckyseven.backend.domain.expense.dto.ExpenseBalanceResponse;
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

        // TODO: 현재 로그인한 사용자가 해당 팀(teamId)의 팀원인지 검증 (팀원 로직 참고)
        // TODO: 로그인한 사용자 ID를 결제자(payerId)로 설정 (팀원 로직 참고)
        // TODO: 정산 대상자들이 모두 해당 팀 소속인지 검증 (팀원 로직 참고)
        // TODO: 예산 원화 잔고 차감

        Expense expense = ExpenseMapper.toExpense(request, teamId);
        Expense saved = expenseRepository.save(expense);
        return ExpenseMapper.toExpenseResponse(saved);
    }

    @Transactional
    public ExpenseResponse updateExpense(Long expenseId,
        ExpenseUpdateRequest request) {

        Expense expense = getExpenseById(expenseId);

        // TODO: 현재 로그인한 사용자의 ID를 SecurityContextHolder 또는 JWT 유틸에서 꺼냄
        // TODO: 해당 ID가 expense.payerId와 일치하는지 검증
        // TODO: 예산 원화 잔고 수정

        expense.update(request.getDescription(), request.getAmount(), request.getCategory());

        return ExpenseMapper.toExpenseResponse(expense);
    }

    @Transactional
    public ExpenseBalanceResponse deleteExpense(Long expenseId) {

        // TODO: 로그인 사용자 ID와 expense.payerId 일치하는지 검증

        Expense expense = getExpenseById(expenseId);

        Long teamId = expense.getTeamId();

        // TODO: 팀 조회
        // TODO: budgetId를 팀에서 추출
        // TODO: 예산 조회
        // TODO; 예산 원화 잔고 증가

        expenseRepository.delete(expense);

        // TODO: balance 와 foreginBalance 변환하여 ExpenseBalanceResponse 반환
        return null;
    }


    private Expense getExpenseById(Long expenseId) {
        return expenseRepository.findById(expenseId)
            .orElseThrow(() -> new CustomLogicException(ExceptionCode.EXPENSE_NOT_FOUND));
    }
}
