package com.luckyseven.backend.domain.budget.service;

import com.luckyseven.backend.domain.budget.dao.BudgetRepository;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateResponse;
import com.luckyseven.backend.domain.budget.dto.BudgetReadResponse;
import com.luckyseven.backend.domain.budget.dto.BudgetUpdateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetUpdateResponse;
import com.luckyseven.backend.domain.budget.entity.Budget;
import com.luckyseven.backend.domain.budget.mapper.BudgetMapper;
import com.luckyseven.backend.domain.budget.validator.BudgetValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BudgetService {

  private final BudgetRepository budgetRepository;
  private final BudgetMapper budgetMapper;
  private final BudgetValidator budgetValidator;

  public BudgetCreateResponse save(Long teamId, BudgetCreateRequest request) {
    budgetValidator.validateBudgetNotExist(teamId);

    Budget budget = budgetMapper.toEntity(teamId, request);

    budgetRepository.save(budget);

    return budgetMapper.toCreateResponse(budget);
  }

  public BudgetReadResponse getByTeamId(Long teamId) {
    Optional<Budget> budgetOptional = budgetRepository.findByTeamId(teamId);

    Budget budget = budgetValidator.validateBudgetExist(teamId, budgetOptional);

    return budgetMapper.toReadResponse(budget);
  }


  public BudgetUpdateResponse updateByTeamId(Long teamId, @Valid BudgetUpdateRequest request) {
    Optional<Budget> budgetOptional = budgetRepository.findByTeamId(teamId);

    Budget budget = budgetValidator.validateBudgetExist(teamId, budgetOptional);

//    return budgetMapper.toUpdateResponse(budget);
    return null;
  }
}
