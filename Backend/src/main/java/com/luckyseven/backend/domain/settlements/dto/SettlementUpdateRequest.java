package com.luckyseven.backend.domain.settlements.dto;

import com.luckyseven.backend.domain.settlements.entity.Settlement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import lombok.Data;

/**
 * DTO for {@link Settlement}
 */
@Data
public class SettlementUpdateRequest {

  @DecimalMin(value = "0.0", inclusive = false)
  @Digits(integer = 15, fraction = 2)
  private BigDecimal amount;

  private Boolean isSettled;

  private Long settlerId;

  private Long payerId;

  private Long expenseId;
}