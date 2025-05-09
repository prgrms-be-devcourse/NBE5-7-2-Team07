package com.luckyseven.backend.domain.expense.dto;

import com.luckyseven.backend.domain.expense.enums.ExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ExpenseUpdateRequest {

    @NotBlank(message = "설명은 공백일 수 없습니다.")
    private String description;

    @NotNull(message = "금액은 필수 입력 항목입니다.")
    @DecimalMin(value = "0.00", message = "금액은 0 이상이어야 합니다.")
    private BigDecimal amount;

    @NotNull(message = "카테고리는 필수 선택 항목입니다.")
    private ExpenseCategory category;
}
