package com.luckyseven.backend.domain.budget.service;

import com.luckyseven.backend.domain.budget.dao.BudgetRepository;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateResponse;
import com.luckyseven.backend.domain.budget.dto.BudgetReadResponse;
import com.luckyseven.backend.domain.budget.entity.Budget;
import com.luckyseven.backend.domain.budget.mapper.BudgetMapper;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BudgetService {

  private final BudgetRepository budgetRepository;

  public BudgetCreateResponse save(Long teamId, BudgetCreateRequest request) {

    validateBudgetNotExist(teamId);

    Budget budget = BudgetMapper.toEntity(teamId, request);

    budgetRepository.save(budget);

    return BudgetMapper.toCreateResponse(budget);
  }

  public BudgetReadResponse getByTeamId(Long teamId) {
    Optional<Budget> budgetOptional = budgetRepository.findByTeamId(teamId);
    if (budgetOptional.isEmpty()) {
      // TODO: ExceptionCode에 TEAM_NOT_FOUND 추가
      throw new CustomLogicException(ExceptionCode.TEAM_NOT_FOUND, "teamId: " + teamId);
    }

    return BudgetMapper.toReadResponse(budgetOptional.get());
  }


  private void validateBudgetNotExist(Long teamId) {
    Optional<Budget> budgetOptional = budgetRepository.findByTeamId(teamId);

    if (budgetOptional.isPresent()) {
      // TODO: ExceptionCode에 BUDGET_CONFLICT 추가
      throw new CustomLogicException(ExceptionCode.BUDGET_CONFLICT,
          "budgetId: " + budgetOptional.get().getId());
    }
  }
}
