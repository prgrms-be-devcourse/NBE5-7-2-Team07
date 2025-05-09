package com.luckyseven.backend.domain.settlements.dto;

import com.luckyseven.backend.domain.settlements.entity.Settlement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

/**
 * DTO for {@link Settlement}
 */
@Data
public class SettlementCreateRequest {

  @NotNull
  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer = 15, fraction = 2)
  private BigDecimal amount;

  @NotNull
  private Long settlerId;

  @NotNull
  private Long payerId;

  @NotNull
  private Long expenseId;
}