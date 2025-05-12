package com.luckyseven.backend.domain.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class BudgetCreateResponse {

  private Long id;
  private BigDecimal balance;
  private BigDecimal foreignBalance;
  private BigDecimal avgExchangeRate;
  private LocalDateTime createdAt;

  @Builder
  public BudgetCreateResponse(Long id, BigDecimal balance, BigDecimal foreignBalance,
      BigDecimal avgExchangeRate, LocalDateTime createdAt) {
    this.id = id;
    this.balance = balance;
    this.foreignBalance = foreignBalance;
    this.avgExchangeRate = avgExchangeRate;
    this.createdAt = createdAt;
  }

}
