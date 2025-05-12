package com.luckyseven.backend.domain.expense.dto;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseBalanceResponse {

  private BigDecimal foreignBalance;
  private BigDecimal balance;
}
