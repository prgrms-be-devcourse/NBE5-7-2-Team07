package com.luckyseven.backend.domain.budget.entity;

import com.luckyseven.backend.sharedkernel.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget extends BaseEntity {

  @Column(nullable = false)
  private Long teamId;

  @Column(nullable = false)
  private BigDecimal totalAmount;

  @Setter
  @Column(nullable = false)
  private Long setBy;

  @Setter
  @Column(nullable = false)
  private BigDecimal balance;
  @Setter
  private BigDecimal foreignBalance;

  @Column(nullable = false)
  private CurrencyCode foreignCurrency;

  @Setter
  private BigDecimal avgExchangeRate;

  @Builder
  public Budget(Long teamId, BigDecimal totalAmount, Long setBy,
      BigDecimal balance, BigDecimal foreignBalance, CurrencyCode foreignCurrency,
      BigDecimal avgExchangeRate) {
    this.teamId = teamId;
    this.totalAmount = totalAmount;
    this.setBy = setBy;
    this.balance = balance;
    this.foreignBalance = foreignBalance;
    this.foreignCurrency = foreignCurrency;
    this.avgExchangeRate = avgExchangeRate;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
    this.balance = totalAmount;
  }

  public void setExchangeInfo(boolean isExchanged, BigDecimal amount, BigDecimal exchangeRate) {
    if (!isExchanged) {
      this.avgExchangeRate = null;
      this.foreignBalance = null;
      return;
    }

    updateForeignBalance(amount, exchangeRate);
    this.avgExchangeRate = exchangeRate;

  }
  public void updateExchangeInfo(boolean isExchanged, BigDecimal amount, BigDecimal exchangeRate) {
    if (!isExchanged) {
      return;
    }

    updateForeignBalance(amount, exchangeRate);
    updateAvgExchangeRate(amount, exchangeRate);

  }

  // 예산 추가 후 외화잔고 및 평균환율 수정
  private void updateAvgExchangeRate(BigDecimal amount, BigDecimal exchangeRate) {
    if (this.avgExchangeRate == null) {
      avgExchangeRate = exchangeRate;
      return;
    }
    this.avgExchangeRate = (this.balance.multiply(this.avgExchangeRate)
        .add(amount.multiply(exchangeRate)))
        .divide(this.balance.add(amount), 2, RoundingMode.HALF_UP);
  }

  private void updateForeignBalance(BigDecimal amount, BigDecimal exchangeRate) {
    BigDecimal additionalBudget = amount.divide(exchangeRate, 2, RoundingMode.HALF_UP);
    if (this.foreignBalance == null) {
      foreignBalance = BigDecimal.ZERO;
    }
    this.foreignBalance = this.foreignBalance.add(additionalBudget);
  }

  public void setForeignBalance() {
    if (avgExchangeRate != null) {
      this.foreignBalance = totalAmount.divide(avgExchangeRate, 2, RoundingMode.HALF_UP);
    }
  }

}