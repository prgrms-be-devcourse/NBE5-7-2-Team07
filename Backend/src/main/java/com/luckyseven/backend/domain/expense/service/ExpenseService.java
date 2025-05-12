package com.luckyseven.backend.domain.expense.service;

import static com.luckyseven.backend.sharedkernel.exception.ExceptionCode.EXPENSE_NOT_FOUND;
import static com.luckyseven.backend.sharedkernel.exception.ExceptionCode.EXPENSE_PAYER_NOT_FOUND;
import static com.luckyseven.backend.sharedkernel.exception.ExceptionCode.INSUFFICIENT_BALANCE;
import static com.luckyseven.backend.sharedkernel.exception.ExceptionCode.TEAM_NOT_FOUND;

import com.luckyseven.backend.domain.expense.dto.CreateExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseBalanceResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseListResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseRequest;
import com.luckyseven.backend.domain.expense.dto.ExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseUpdateRequest;
import com.luckyseven.backend.domain.expense.entity.Expense;
import com.luckyseven.backend.domain.expense.mapper.ExpenseMapper;
import com.luckyseven.backend.domain.expense.repository.ExpenseRepository;
import com.luckyseven.backend.domain.expense.util.TempBudget;
import com.luckyseven.backend.domain.expense.util.TempMember;
import com.luckyseven.backend.domain.expense.util.TempMemberRepository;
import com.luckyseven.backend.domain.expense.util.TempTeam;
import com.luckyseven.backend.domain.expense.util.TempTeamRepository;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

  private final ExpenseRepository expenseRepository;

  // TODO: TEMP 엔티티 수정
  private final TempTeamRepository teamRepository;
  private final TempMemberRepository memberRepository;

  @Transactional
  public CreateExpenseResponse saveExpense(Long teamId, ExpenseRequest request) {
    TempTeam team = findTeamWithBudgetOrThrow(teamId);
    TempMember payer = findPayerOrThrow(request.getPayerId());

    TempBudget budget = team.getBudget();
    validateSufficientBudget(request.getAmount(), budget.getBalance());

    Expense expense = ExpenseMapper.toExpense(request, team, payer);
    Expense saved = expenseRepository.save(expense);

    budget.updateBalance(budget.getBalance().subtract(request.getAmount()));

    // TODO: 정산 저장 로직 추가
    return ExpenseMapper.toCreateExpenseResponse(saved, budget);
  }

  @Transactional(readOnly = true)
  public ExpenseResponse getExpense(Long expenseId) {
    Expense expense = findExpenseOrThrow(expenseId);
    return ExpenseMapper.toExpenseResponse(expense);
  }

  @Transactional(readOnly = true)
  public ExpenseListResponse getListExpense(Long teamId, Pageable pageable) {
    TempTeam team = findTeamOrThrow(teamId);
    Page<Expense> page = expenseRepository.findByTeamId(team.getId(), pageable);

    List<ExpenseResponse> content = page.getContent().stream()
        .map(ExpenseMapper::toExpenseResponse)
        .toList();

    return ExpenseMapper.toExpenseListResponse(content, page);
  }

  @Transactional
  public CreateExpenseResponse updateExpense(Long expenseId, ExpenseUpdateRequest request) {
    Expense expense = findExpenseOrThrow(expenseId);
    BigDecimal originalAmount = expense.getAmount();
    BigDecimal newAmount = request.getAmount();

    TempBudget budget = expense.getTeam().getBudget();
    BigDecimal delta = newAmount.subtract(originalAmount);
    if (delta.compareTo(BigDecimal.ZERO) > 0) {
      validateSufficientBudget(delta, budget.getBalance());
    }

    expense.update(request.getDescription(), newAmount, request.getCategory());
    budget.updateBalance(budget.getBalance().subtract(delta));

    return ExpenseMapper.toCreateExpenseResponse(expense, budget);
  }

  @Transactional
  public ExpenseBalanceResponse deleteExpense(Long expenseId) {
    Expense expense = findExpenseOrThrow(expenseId);
    TempBudget budget = expense.getTeam().getBudget();

    budget.updateBalance(budget.getBalance().add(expense.getAmount()));
    expenseRepository.delete(expense);

    return ExpenseMapper.toExpenseBalanceResponse(budget);
  }

  private TempTeam findTeamOrThrow(Long teamId) {
    return teamRepository.findById(teamId)
        .orElseThrow(() -> new CustomLogicException(TEAM_NOT_FOUND));
  }

  private TempTeam findTeamWithBudgetOrThrow(Long teamId) {
    return teamRepository.findTeamWithBudget(teamId)
        .orElseThrow(() -> new CustomLogicException(TEAM_NOT_FOUND));
  }

  private TempMember findPayerOrThrow(Long memberId) {
    return memberRepository.findById(memberId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_PAYER_NOT_FOUND));
  }

  private Expense findExpenseOrThrow(Long expenseId) {
    return expenseRepository.findById(expenseId)
        .orElseThrow(() -> new CustomLogicException(EXPENSE_NOT_FOUND));
  }

  private void validateSufficientBudget(BigDecimal amount, BigDecimal balance) {
    if (balance.compareTo(amount) < 0) {
      throw new CustomLogicException(INSUFFICIENT_BALANCE);
    }
  }
}
