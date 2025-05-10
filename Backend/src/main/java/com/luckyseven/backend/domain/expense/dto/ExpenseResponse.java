package com.luckyseven.backend.domain.expense.dto;

import com.luckyseven.backend.domain.expense.enums.ExpenseCategory;
import com.luckyseven.backend.domain.expense.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ExpenseResponse {

  private String description;
  private BigDecimal amount;
  private ExpenseCategory category;
  private Long payerId;
  private String payerNickname;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private PaymentMethod paymentMethod;
  private List<SettlerResponse> settlers;
}
