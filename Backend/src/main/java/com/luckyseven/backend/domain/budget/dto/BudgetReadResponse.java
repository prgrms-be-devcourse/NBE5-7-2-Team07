package com.luckyseven.backend.domain.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BudgetReadResponse {

  private Long id;
  private BigDecimal totalAmount;
  private Long setBy;
  private BigDecimal balance;
  private String foreignCurrency;
  private BigDecimal foreignBalance;
  private BigDecimal avgExchangeRate;
  private LocalDateTime updatedAt;

  @Builder
  public BudgetReadResponse(Long id, BigDecimal totalAmount, Long setBy, BigDecimal balance,
      String foreignCurrency, BigDecimal foreignBalance,
      BigDecimal avgExchangeRate, LocalDateTime updatedAt) {
    this.id = id;
    this.totalAmount = totalAmount;
    this.setBy = setBy;
    this.balance = balance;
    this.foreignCurrency = foreignCurrency;
    this.foreignBalance = foreignBalance;
    this.avgExchangeRate = avgExchangeRate;
    this.updatedAt = updatedAt;
  }
}
