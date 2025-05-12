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
  private BigDecimal balance;
  private BigDecimal foreignBalance;
  private BigDecimal avgExchangeRate;
  private LocalDateTime createdAt;

}
