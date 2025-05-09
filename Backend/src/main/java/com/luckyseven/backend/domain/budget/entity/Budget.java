package com.luckyseven.backend.domain.budget.entity;

import com.luckyseven.backend.sharedkernel.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Budget extends BaseEntity {

  @Id
  @Column(name = "budget_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long teamId;

  @Column(nullable = false)
  private BigDecimal totalAmount;
  private BigDecimal exchangeRate;

  @Column(nullable = false)
  private Long setBy;

  @Column(nullable = false)
  private BigDecimal balance;
  @Column(nullable = false)
  private BigDecimal foreignBalance;

  @Column(nullable = false)
  private String foreignCurrency;

  @Column(nullable = false)
  private BigDecimal avgExchangeRate;

  @Builder
  public Budget(Long teamId, BigDecimal totalAmount, BigDecimal exchangeRate, Long setBy,
      BigDecimal balance, BigDecimal foreignBalance, String foreignCurrency,
      BigDecimal avgExchangeRate) {
    this.teamId = teamId;
    this.totalAmount = totalAmount;
    this.exchangeRate = exchangeRate;
    this.setBy = setBy;
    this.balance = balance;
    this.foreignBalance = foreignBalance;
    this.foreignCurrency = foreignCurrency;
    this.avgExchangeRate = avgExchangeRate;
  }
}