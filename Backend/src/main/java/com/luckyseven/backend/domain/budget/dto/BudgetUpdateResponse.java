package com.luckyseven.backend.domain.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BudgetUpdateResponse {

  private Long id;
  private BigDecimal balance;
  private BigDecimal foreignBalance;
  private BigDecimal avgExchangeRate;
  private LocalDateTime updatedAt;

  @Builder
  public BudgetUpdateResponse(Long id, BigDecimal balance, BigDecimal foreignBalance,
      BigDecimal avgExchangeRate, LocalDateTime updatedAt) {
    this.id = id;
    this.balance = balance;
    this.foreignBalance = foreignBalance;
    this.avgExchangeRate = avgExchangeRate;
    this.updatedAt = updatedAt;
  }
}
