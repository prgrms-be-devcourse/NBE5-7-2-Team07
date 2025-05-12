package com.luckyseven.backend.domain.expense.dto;

import com.luckyseven.backend.domain.expense.enums.ExpenseCategory;
import com.luckyseven.backend.domain.expense.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseResponse {

  private Long id;
  private String description;
  private BigDecimal amount;
  private ExpenseCategory category;
  private Long payerId;
  private String payerNickname;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private PaymentMethod paymentMethod;
}
