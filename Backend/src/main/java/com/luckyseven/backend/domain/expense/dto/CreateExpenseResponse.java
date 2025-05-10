package com.luckyseven.backend.domain.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CreateExpenseResponse {

  private Long expenseId;
  private BigDecimal amount;
  private BigDecimal foreignBalance;
  private BigDecimal balance;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
