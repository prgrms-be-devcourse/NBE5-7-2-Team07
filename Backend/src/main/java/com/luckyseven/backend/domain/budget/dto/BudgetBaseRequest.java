package com.luckyseven.backend.domain.budget.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BudgetBaseRequest {

  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal totalAmount;

  @NotNull
  private Long setBy;

  private Boolean isExchanged;
  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal exchangeRate;

}
