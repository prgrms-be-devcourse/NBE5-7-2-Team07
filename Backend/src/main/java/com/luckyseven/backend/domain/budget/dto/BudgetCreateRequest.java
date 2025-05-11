package com.luckyseven.backend.domain.budget.dto;

import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetCreateRequest {

  private BigDecimal totalAmount;
  private Long setBy;
  private String foreignCurrency;
  private Boolean isExchanged;
  private BigDecimal exchangeRate;

  public void validate() {
    if (Boolean.TRUE.equals(isExchanged) && exchangeRate == null) {
      throw new CustomLogicException(ExceptionCode.BAD_REQUEST, "환전 여부가 true인데 환율이 없습니다.");
    }

    if (Boolean.FALSE.equals(isExchanged)) {
      exchangeRate = null;
    }

  }
}
