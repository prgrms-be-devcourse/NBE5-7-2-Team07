package com.luckyseven.backend.domain.budget.service;

import com.luckyseven.backend.domain.budget.dao.BudgetRepository;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateResponse;
import com.luckyseven.backend.domain.budget.entity.Budget;
import com.luckyseven.backend.domain.budget.mapper.BudgetMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BudgetService {

  private final BudgetRepository budgetRepository;

  public BudgetCreateResponse save(Long teamId, BudgetCreateRequest request) {
    Budget budget = BudgetMapper.toEntity(teamId, request);

    budgetRepository.save(budget);

    return BudgetMapper.toCreateResponse(budget);
  }
}
