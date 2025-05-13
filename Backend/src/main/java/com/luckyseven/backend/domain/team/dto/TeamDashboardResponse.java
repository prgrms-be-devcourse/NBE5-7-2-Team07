package com.luckyseven.backend.domain.team.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDashboardResponse {

  @NotNull(message = "팀 ID는 필수입니다")
  private Long team_id;

  @NotNull(message = "잔액 정보는 필수입니다")
  @DecimalMin(value = "0.0", message = "잔액은 0 이상이어야 합니다")
  private BigDecimal currency;

  private BigDecimal balance;

  private BigDecimal foreignBalance;

  private BigDecimal totalAmount;

  private BigDecimal exchangeRate;

  private BigDecimal avgExchangeRate;

  @Valid
  private List<ExpenseDto> expenseList = new ArrayList<>();

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ExpenseDto {

    private String description;
    private Integer amount;
    private String category;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private Long payer;
  }
}