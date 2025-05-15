package com.luckyseven.backend.domain.expense.util;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TempBudget {

  @Id
  private Long id;

  private BigDecimal balance;
  private BigDecimal foreignBalance;

  // Todo: 나중에 실제 엔티티로 옮기기
  public void updateBalance(BigDecimal balance) {
    if (balance != null) {
      this.balance = balance;
    }
  }
}
