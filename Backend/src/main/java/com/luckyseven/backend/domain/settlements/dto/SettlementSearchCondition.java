package com.luckyseven.backend.domain.settlements.dto;

import lombok.Data;

@Data
public class SettlementSearchCondition {

  private Long expenseId;
  private Long settlerId;
  private Long payerId;
  private Boolean isSettled;

}
