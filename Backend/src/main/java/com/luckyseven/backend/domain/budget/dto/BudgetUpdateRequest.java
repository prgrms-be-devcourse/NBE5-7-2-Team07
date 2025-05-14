package com.luckyseven.backend.domain.budget.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder
@Jacksonized
public class BudgetUpdateRequest extends BudgetBaseRequest {

  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal additionalBudget;

}
