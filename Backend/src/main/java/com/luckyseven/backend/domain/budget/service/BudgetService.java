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
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BudgetService {

  private final TeamRepository teamRepository;
  private final BudgetRepository budgetRepository;
  private final BudgetMapper budgetMapper;
  private final BudgetValidator budgetValidator;

  @Transactional
  public BudgetCreateResponse save(Long teamId, Long loginMemberId, BudgetCreateRequest request) {
    budgetValidator.validateBudgetNotExist(teamId);
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new EntityNotFoundException("팀을 찾을 수 없습니다: " + teamId));

    Budget budget = Budget.builder()
        .team(team)
        .totalAmount(request.totalAmount())
        .setBy(loginMemberId)
        .balance(request.totalAmount())
        .foreignCurrency(request.foreignCurrency())
        .build();

    budget.setExchangeInfo(request.isExchanged(),
        budget.getTotalAmount(),
        request.exchangeRate());

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

    if (request.additionalBudget() != null) {
      addBudget(request, budget);
      return budgetMapper.toUpdateResponse(budget);
    }

    updateTotalAmountOrExchangeRate(request, budget);

    return budgetMapper.toUpdateResponse(budget);
  }

  @Transactional
  public void deleteByTeamId(Long teamId) {
    Budget budget = budgetValidator.validateBudgetExist(teamId);
    budgetRepository.delete(budget);
  }

  private static void addBudget(BudgetUpdateRequest request, Budget budget) {
    // totalAmount, Balance += additionalBudget
    if (request.additionalBudget() != null) {
      budget.updateExchangeInfo(request.isExchanged(),
          request.additionalBudget(),
          request.exchangeRate());
      budget.setTotalAmount(budget.getTotalAmount().add(request.additionalBudget()));
    }
  }

  private static void updateTotalAmountOrExchangeRate(BudgetUpdateRequest request, Budget budget) {
    // totalAmount, Balance update
    if (request.totalAmount() != null) {
      budget.setTotalAmount(request.totalAmount());
    }

    // avgExchange, foreignBalance update
    if (request.isExchanged() != null) {
      budget.setExchangeInfo(request.isExchanged(),
          budget.getTotalAmount(),
          request.exchangeRate());
    }
    // totalAmount만 수정을 원할 경우, foreignBalance update
    budget.setForeignBalance();
  }
}
