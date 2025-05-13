package com.luckyseven.backend.domain.budget.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder
@Jacksonized
public class BudgetCreateRequest extends BudgetBaseRequest {

  @NotNull
  private BigDecimal totalAmount;

  @NotNull
  private Boolean isExchanged;

  @NotNull
  @NotBlank
  private String foreignCurrency;

}
