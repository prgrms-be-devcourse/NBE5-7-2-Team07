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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetService {

  private final BudgetRepository budgetRepository;
  private final BudgetMapper budgetMapper;
  private final BudgetValidator budgetValidator;

  @Transactional
  public BudgetCreateResponse save(Long teamId, Long loginMemberId, BudgetCreateRequest request) {
    budgetValidator.validateBudgetNotExist(teamId);

    Budget budget = Budget.builder()
        .teamId(teamId)
        .totalAmount(request.getTotalAmount())
        .setBy(loginMemberId)
        .balance(request.getTotalAmount())
        .foreignCurrency(request.getForeignCurrency())
        .build();

    budget.setAvgExchangeRate(request.getIsExchanged(), request.getExchangeRate());

    budgetRepository.save(budget);

    return budgetMapper.toCreateResponse(budget);
  }

  @Transactional
  public BudgetReadResponse getByTeamId(Long teamId) {
    Budget budget = budgetValidator.validateBudgetExist(teamId);

    return budgetMapper.toReadResponse(budget);
  }

  @Transactional
  public BudgetUpdateResponse updateByTeamId(Long teamId, Long loginMemberId,
      BudgetUpdateRequest request) {
    Budget budget = budgetValidator.validateBudgetExist(teamId);

    budget.setSetBy(loginMemberId);
    // totalAmount, Balance update
    if (request.getTotalAmount() != null) {
      budget.setTotalAmount(request.getTotalAmount());
    }
    // avgExchange, foreignBalance update
    if (request.getIsExchanged() != null) {
      budget.setAvgExchangeRate(request.getIsExchanged(), request.getExchangeRate());
    }
    // totalAmount만 수정을 원할 경우, foreignBalance update
    budget.setForeignBalance();

    return budgetMapper.toUpdateResponse(budget);
  }

  @Transactional
  public void deleteByTeamId(Long teamId) {
    Budget budget = budgetValidator.validateBudgetExist(teamId);
    budgetRepository.delete(budget);
  }
}
