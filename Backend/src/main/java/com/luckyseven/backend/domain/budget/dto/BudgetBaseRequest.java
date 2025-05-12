package com.luckyseven.backend.domain.budget.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BudgetBaseRequest {

  private BigDecimal totalAmount;
  private Long setBy;
  private Boolean isExchanged;
  private BigDecimal exchangeRate;

}
