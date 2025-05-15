package com.luckyseven.backend.domain.budget.mapper;

import com.luckyseven.backend.domain.budget.dto.BudgetCreateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateResponse;
import com.luckyseven.backend.domain.budget.dto.BudgetReadResponse;
import com.luckyseven.backend.domain.budget.dto.BudgetUpdateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetUpdateResponse;
import com.luckyseven.backend.domain.budget.entity.Budget;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class BudgetMapper {

  public BudgetCreateResponse toCreateResponse(Budget budget) {
    return BudgetCreateResponse.builder()
        .id(budget.getId())
        .setBy(budget.getSetBy())
        .balance(budget.getBalance())
        .foreignBalance(budget.getForeignBalance())
        .avgExchangeRate(budget.getAvgExchangeRate())
        .createdAt(budget.getCreatedAt())
        .build();
  }

  public BudgetReadResponse toReadResponse(Budget budget) {
    return BudgetReadResponse.builder()
        .id(budget.getId())
        .setBy(budget.getSetBy())
        .totalAmount(budget.getTotalAmount())
        .setBy(budget.getSetBy())
        .balance(budget.getBalance())
        .foreignCurrency(budget.getForeignCurrency())
        .foreignBalance(budget.getForeignBalance())
        .avgExchangeRate(budget.getAvgExchangeRate())
        .updatedAt(budget.getUpdatedAt())
        .build();
  }

  public BudgetUpdateResponse toUpdateResponse(Budget budget) {
    return BudgetUpdateResponse.builder()
        .id(budget.getId())
        .setBy(budget.getSetBy())
        .balance(budget.getBalance())
        .foreignCurrency(budget.getForeignCurrency())
        .foreignBalance(budget.getForeignBalance())
        .avgExchangeRate(budget.getAvgExchangeRate())
        .updatedAt(budget.getUpdatedAt())
        .build();
  }

}
