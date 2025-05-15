package com.luckyseven.backend.domain.budget.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@SuperBuilder
@Jacksonized
public class BudgetBaseRequest {

  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal totalAmount;

  private Boolean isExchanged;
  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal exchangeRate;

}
