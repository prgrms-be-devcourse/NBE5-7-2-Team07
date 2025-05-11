package com.luckyseven.backend.domain.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetCreateResponse {

  private Long id;
  private BigDecimal balance;
  private BigDecimal foreignBalance;
  private BigDecimal avgExchangeRate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Builder
  public BudgetCreateResponse(Long id, BigDecimal balance, BigDecimal foreignBalance,
      BigDecimal avgExchangeRate, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.balance = balance;
    this.foreignBalance = foreignBalance;
    this.avgExchangeRate = avgExchangeRate;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }
}
