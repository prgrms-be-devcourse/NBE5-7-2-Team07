package com.luckyseven.backend.domain.budget.entity;

import static com.luckyseven.backend.sharedkernel.exception.ExceptionCode.INSUFFICIENT_BALANCE;
import com.luckyseven.backend.domain.budget.dto.BudgetUpdateRequest;
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.sharedkernel.entity.BaseEntity;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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

  @Id
  @Column(name = "budget_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(mappedBy = "budget")
  private Team team;

  @Column(nullable = false)
  private BigDecimal totalAmount;

  @Setter
  @Column(nullable = false)
  private Long setBy;

  @Column(nullable = false)
  private BigDecimal balance;

  private BigDecimal foreignBalance;

  @Setter
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 3)
  private CurrencyCode foreignCurrency;

  private BigDecimal avgExchangeRate;

  @Builder
  public Budget(Team team, BigDecimal totalAmount, Long setBy,
      BigDecimal balance, BigDecimal foreignBalance, CurrencyCode foreignCurrency,
      BigDecimal avgExchangeRate) {
    this.team = team;
    this.totalAmount = totalAmount;
    this.setBy = setBy;
    this.balance = balance;
    this.foreignBalance = foreignBalance;
    this.foreignCurrency = foreignCurrency;
    this.avgExchangeRate = avgExchangeRate;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public void addBalance(BudgetUpdateRequest request) {
    if (request.additionalBudget() == null) {
      return;
    }
    this.balance = this.balance.add(request.additionalBudget());
  }

  public void setExchangeInfo(boolean isExchanged, BigDecimal amount, BigDecimal exchangeRate) {
    if (!isExchanged) {
      this.avgExchangeRate = null;
      this.foreignBalance = null;
      return;
    }
    updateForeignBalance(amount, exchangeRate);
  }

  public void updateExchangeInfo(boolean isExchanged, BigDecimal amount, BigDecimal exchangeRate) {
    if (!isExchanged) {
      return;
    }
    updateAvgExchangeRate(amount, exchangeRate);
    updateForeignBalance(amount, exchangeRate);
  }

  private void updateAvgExchangeRate(BigDecimal amount, BigDecimal exchangeRate) {
    if (this.avgExchangeRate == null || this.avgExchangeRate.compareTo(BigDecimal.ZERO) == 0) {
      avgExchangeRate = exchangeRate;
      return;
    }
    BigDecimal foreignAmount = amount.divide(exchangeRate, 10, RoundingMode.HALF_UP);
    BigDecimal totalCost = this.foreignBalance.multiply(this.avgExchangeRate).add(amount);
    BigDecimal totalForeign = this.foreignBalance.add(foreignAmount);
    this.avgExchangeRate = totalCost.divide(totalForeign, 2, RoundingMode.HALF_UP);
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

  public void setForeignBalance(BigDecimal amount) {
    this.foreignBalance = amount;
  }

  public Budget setTeam(Team team) {
    if (this.team != null) {
      this.team.setBudget(null);
    }
    this.team = team;
    if (team != null && team.getBudget() != this) {
      team.setBudget(this);
    }
    return this;
  }

  public void debit(BigDecimal amount) {
    if (amount == null || balance.compareTo(amount) < 0) {
      throw new CustomLogicException(INSUFFICIENT_BALANCE);
    }
    this.balance = this.balance.subtract(amount);
  }

  public void credit(BigDecimal amount) {
    if (amount != null) {
      this.balance = this.balance.add(amount);
    }
  }
}