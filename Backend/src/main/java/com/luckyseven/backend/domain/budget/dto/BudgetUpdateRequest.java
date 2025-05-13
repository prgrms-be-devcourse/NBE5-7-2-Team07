package com.luckyseven.backend.domain.budget.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder
@Jacksonized
public class BudgetUpdateRequest extends BudgetBaseRequest {
}
