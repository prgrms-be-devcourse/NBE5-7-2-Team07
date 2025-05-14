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

  private Long team_id;

  private BigDecimal currency;

  private BigDecimal balance;

  private BigDecimal foreignBalance;

  private BigDecimal totalAmount;

  private BigDecimal avgExchangeRate;

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