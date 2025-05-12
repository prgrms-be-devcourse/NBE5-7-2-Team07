package com.luckyseven.backend.domain.budget.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BudgetCreateRequest extends BudgetBaseRequest {

  private String foreignCurrency;

}
