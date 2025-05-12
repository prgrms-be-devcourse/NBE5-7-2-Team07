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
  public Budget toEntity(Long teamId, BudgetCreateRequest request) {
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
        .setBy(request.getSetBy())
        .balance(request.getTotalAmount())
        .foreignBalance(foreignBalance)
        .foreignCurrency(request.getForeignCurrency())
        .avgExchangeRate(avgExchangeRate)
        .build();
  }

  public BudgetCreateResponse toCreateResponse(Budget budget) {

    return BudgetCreateResponse.builder()
        .id(budget.getId())
        .balance(budget.getBalance())
        .foreignBalance(budget.getForeignBalance())
        .avgExchangeRate(budget.getAvgExchangeRate())
        .createdAt(budget.getCreatedAt())
        .build();
  }

  public BudgetReadResponse toReadResponse(Budget budget) {
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

  public Budget toEntity(BudgetUpdateRequest request, Budget budget) {
    budget.setSetBy(request.getSetBy());

    // case 1: 총 예산(totalAmount) + 환전 여부/환율 수정(isExchanged + exchangeRate)
    // totalAmount, Balance update
    if (request.getTotalAmount() != null) {
      budget.setTotalAmount(request.getTotalAmount());
      budget.setBalance(request.getTotalAmount());
    }

    // 1-1: isExchanged = false
    if (request.getIsExchanged() != null && !request.getIsExchanged()) {
      budget.setForeignBalance(null);
      budget.setAvgExchangeRate(null);
      return budget;
    }

    // 1-2: isExchanged = true
    if (request.getIsExchanged() != null) {
      budget.setAvgExchangeRate(request.getExchangeRate());
      budget.setForeignBalance(budget.getTotalAmount()
          .divide(request.getExchangeRate(), 2, RoundingMode.HALF_UP));
      return budget;
    }

    // case 2: 총 예산(totalAmount)만 수정
    if (budget.getAvgExchangeRate() != null) {
      budget.setForeignBalance(budget.getTotalAmount()
          .divide(budget.getAvgExchangeRate(), 2, RoundingMode.HALF_UP));
    }

    return budget;
  }

  public BudgetUpdateResponse toUpdateResponse(Budget budget) {
    return BudgetUpdateResponse.builder()
        .id(budget.getId())
        .balance(budget.getBalance())
        .foreignCurrency(budget.getForeignCurrency())
        .foreignBalance(budget.getForeignBalance())
        .avgExchangeRate(budget.getAvgExchangeRate())
        .updatedAt(budget.getUpdatedAt())
        .build();
  }

}
