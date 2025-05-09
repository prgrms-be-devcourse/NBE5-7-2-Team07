package com.luckyseven.backend.domain.settlements.util;

import com.luckyseven.backend.domain.settlements.TempExpense;
import com.luckyseven.backend.domain.settlements.TempMember;
import com.luckyseven.backend.domain.settlements.dto.SettlementCreateRequest;
import com.luckyseven.backend.domain.settlements.dto.SettlementResponse;
import com.luckyseven.backend.domain.settlements.entity.Settlement;

public class SettlementMapper {

  private SettlementMapper() {
  }

  public static SettlementResponse toSettlementResponse(Settlement settlement) {
    return SettlementResponse.builder()
        .id(settlement.getId())
        .amount(settlement.getAmount())
        .createdAt(settlement.getCreatedAt())
        .updatedAt(settlement.getUpdatedAt())
        .isSettled(settlement.getIsSettled())
        .settlerId(settlement.getSettler().getId())
        .payerId(settlement.getPayer().getId())
        .expenseId(settlement.getExpense().getId())
        .build();
  }

  // TODO: TEMP 엔티티 제거
  public static Settlement fromSettlementCreateRequest(SettlementCreateRequest request,
      TempMember settler, TempMember payer, TempExpense expense) {
    return new Settlement(request.getAmount(), settler, payer, expense);
  }
}
