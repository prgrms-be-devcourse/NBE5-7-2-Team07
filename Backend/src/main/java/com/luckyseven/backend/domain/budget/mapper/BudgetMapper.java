package com.luckyseven.backend.domain.budget.mapper;

import com.luckyseven.backend.domain.budget.dto.BudgetCreateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateResponse;
import com.luckyseven.backend.domain.budget.dto.BudgetReadResponse;
import com.luckyseven.backend.domain.budget.entity.Budget;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BudgetMapper {
  public static Budget toEntity(Long teamId, BudgetCreateRequest request) {

    BigDecimal exchangeRate;
    BigDecimal foreignBalance;
    BigDecimal avgExchangeRate;
    if (request.getExchangeRate() != null) {
      exchangeRate = request.getExchangeRate();
      foreignBalance = request.getTotalAmount()
          .divide(exchangeRate, 2, RoundingMode.HALF_UP);
      avgExchangeRate = request.getExchangeRate();
    } else {
      exchangeRate = BigDecimal.ONE;
      foreignBalance = BigDecimal.ZERO;
      avgExchangeRate = BigDecimal.ONE;
    }

    return Budget.builder()
        .teamId(teamId)
        .totalAmount(request.getTotalAmount())
        .exchangeRate(exchangeRate)
        .setBy(request.getSetBy())
        .balance(request.getTotalAmount())
        .foreignBalance(foreignBalance)
        .foreignCurrency(request.getForeignCurrency())
        .avgExchangeRate(avgExchangeRate)
        .build();
  }

  public static BudgetCreateResponse toCreateResponse(Budget budget) {

    return BudgetCreateResponse.builder()
        .id(budget.getId())
        .balance(budget.getBalance())
        .foreignBalance(budget.getForeignBalance())
        .avgExchangeRate(budget.getAvgExchangeRate())
        .createdAt(budget.getCreatedAt())
        .build();
  }

  public static BudgetReadResponse toReadResponse(Budget budget) {
    return BudgetReadResponse.builder()
        .id(budget.getId())
        .totalAmount(budget.getTotalAmount())
        .setBy(budget.getSetBy())
        .balance(budget.getBalance())
        .foreignCurrency(budget.getForeignCurrency())
        .foreignBalance(budget.getForeignBalance())
        .avgExchangeRate(budget.getAvgExchangeRate())
        .updatedAt(budget.getUpdatedAt())
        .build();
  }

//  public static Budget toEntity(BudgetUpdateRequest request) {
//    return null;
//  }
//
//  public static BudgetUpdateResponse toUpdateResponse(Budget budget) {
//    return null;
//  }

}
