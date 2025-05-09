package com.luckyseven.backend.domain.expense.dto;

import com.luckyseven.backend.domain.expense.enums.ExpenseCategory;
import com.luckyseven.backend.domain.expense.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
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
public class ExpenseRequest {

    @NotBlank(message = "설명은 공백일 수 없습니다.")
    private String description;

    @NotNull(message = "금액은 필수 입력 항목입니다.")
    @DecimalMin(value = "0.00", message = "금액은 0 이상이어야 합니다.")
    private BigDecimal amount;

    @NotNull(message = "카테고리는 필수 선택 항목입니다.")
    private ExpenseCategory category;

    @NotEmpty(message = "정산 대상자가 최소 1명 이상 필요합니다.")
    private List<@NotNull(message = "정산 대상자 ID는 null일 수 없습니다.") Long> settlerId;

    @NotNull(message = "결제 수단은 필수 선택 항목입니다.")
    private PaymentMethod paymentMethod;
}
