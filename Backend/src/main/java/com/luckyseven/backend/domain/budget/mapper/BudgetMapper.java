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
  public Budget toEntity(Long teamId, Long loginMemberId, BudgetCreateRequest request) {
    BigDecimal foreignBalance;
    BigDecimal avgExchangeRate;

    if (request.getIsExchanged()) {
      avgExchangeRate = request.getExchangeRate();
      foreignBalance = request.getTotalAmount()
          .divide(avgExchangeRate, 2, RoundingMode.HALF_UP);
    } else {
      foreignBalance = null;
      avgExchangeRate = null;
    }

    return Budget.builder()
        .teamId(teamId)
        .totalAmount(request.getTotalAmount())
        .setBy(loginMemberId)
        .balance(request.getTotalAmount())
        .foreignBalance(foreignBalance)
        .foreignCurrency(request.getForeignCurrency())
        .avgExchangeRate(avgExchangeRate)
        .build();
  }

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

  public Budget toEntity(Long loginMemberId, BudgetUpdateRequest request, Budget budget) {
    budget.setSetBy(loginMemberId);

    // totalAmount, Balance update
    if (request.getTotalAmount() != null) {
      budget.setTotalAmount(request.getTotalAmount());
    }

    // avgExchange, foreignBalance update
    if (request.getIsExchanged() != null) {
      budget.setAvgExchangeRate(request.getIsExchanged(), request.getExchangeRate());
      return budget;
    }

    // totalAmount만 수정을 원할 경우, foreignBalance update
    budget.setForeignBalance();

    return budget;
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
