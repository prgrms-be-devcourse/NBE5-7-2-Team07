package com.luckyseven.backend.domain.expense.dto;

import com.luckyseven.backend.domain.expense.enums.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseUpdateRequest {

  private String description;

  @DecimalMin(value = "0.00", message = "금액은 0 이상이어야 합니다.")
  private BigDecimal amount;

  private ExpenseCategory category;
}
