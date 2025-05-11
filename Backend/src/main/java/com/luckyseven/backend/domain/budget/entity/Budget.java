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
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget extends BaseEntity {

  @Id
  @Column(name = "budget_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long teamId;

  @Setter
  @Column(nullable = false)
  private BigDecimal totalAmount;
  @Setter
  private BigDecimal exchangeRate;

  @Setter
  @Column(nullable = false)
  private Long setBy;

  @Setter
  @Column(nullable = false)
  private BigDecimal balance;
  @Setter
  private BigDecimal foreignBalance;

  @Setter
  @Column(nullable = false)
  private String foreignCurrency;

  @Setter
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