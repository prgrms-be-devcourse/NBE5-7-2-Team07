package com.luckyseven.backend.domain.budget.dto;

import com.luckyseven.backend.domain.budget.entity.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@NotNull
public class BudgetUpdateResponse {

  private Long id;
  private BigDecimal balance;
  private CurrencyCode foreignCurrency;
  private BigDecimal foreignBalance;
  private BigDecimal avgExchangeRate;
  private LocalDateTime updatedAt;

}
