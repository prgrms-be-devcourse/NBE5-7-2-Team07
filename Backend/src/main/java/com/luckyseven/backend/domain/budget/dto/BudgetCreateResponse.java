package com.luckyseven.backend.domain.budget.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@NotNull
public class BudgetCreateResponse {

  private Long id;
  private LocalDateTime createdAt;

  private Long setBy;
  private BigDecimal balance;

  private BigDecimal avgExchangeRate;
  private BigDecimal foreignBalance;

}
