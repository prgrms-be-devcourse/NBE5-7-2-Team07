package com.luckyseven.backend.domain.settlements.dto;

import com.luckyseven.backend.domain.settlements.entity.Settlement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for {@link Settlement}
 */
@Data
@Builder
public class SettlementResponse {

  private Long id;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private BigDecimal amount;
  private Boolean isSettled;
  private Long settlerId;
  private Long payerId;
  private Long expenseId;
}