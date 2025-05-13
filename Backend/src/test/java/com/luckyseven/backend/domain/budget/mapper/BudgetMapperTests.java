package com.luckyseven.backend.domain.budget.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.luckyseven.backend.domain.budget.dto.BudgetCreateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetUpdateRequest;
import com.luckyseven.backend.domain.budget.entity.Budget;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class BudgetMapperTests {

  private final BudgetMapper budgetMapper = new BudgetMapper();

  @Test
  @DisplayName("생성을 위한 toEntity는 request의 환전여부가 false일 때 외화잔고와 환율을 null로 반환한다")
  void toEntity_return_foreignBalance_and_aveExchange_when_isExchanged_is_null() throws Exception {

    // given
    Long teamId = 1L;
    BudgetCreateRequest request = BudgetCreateRequest.builder()
        .totalAmount(BigDecimal.valueOf(100000))
        .setBy(1L)
        .foreignCurrency("USD")
        .isExchanged(false)
        .build();

    // when
    Budget budget = budgetMapper.toEntity(teamId, request);

    // then
    assertThat(budget.getTeamId()).isEqualTo(teamId);
    assertThat(budget.getTotalAmount()).isEqualTo(BigDecimal.valueOf(100000));
    assertThat(budget.getSetBy()).isEqualTo(1L);
    assertThat(budget.getBalance()).isEqualTo(BigDecimal.valueOf(100000));
    assertThat(budget.getForeignCurrency()).isEqualTo("USD");
    assertThat(budget.getForeignBalance()).isNull();
    assertThat(budget.getAvgExchangeRate()).isNull();

  }

  @Test
  @DisplayName("생성을 위한 toEntity는 request 속 환율을 저장하고 외화잔고를 계산하여 저장하고 반환한다")
  void toEntity_return_calculated_foreignBalance_and_avgExchangeRate() throws Exception {

    // given
    Long teamId = 1L;
    BudgetCreateRequest request = BudgetCreateRequest.builder()
        .totalAmount(BigDecimal.valueOf(100000))
        .setBy(1L)
        .foreignCurrency("USD")
        .isExchanged(true)
        .exchangeRate(BigDecimal.valueOf(1393.7))
        .build();

    // when
    Budget budget = budgetMapper.toEntity(teamId, request);

    // then
    assertThat(budget.getTeamId()).isEqualTo(teamId);
    assertThat(budget.getTotalAmount()).isEqualTo(BigDecimal.valueOf(100000));
    assertThat(budget.getSetBy()).isEqualTo(1L);
    assertThat(budget.getBalance()).isEqualTo(BigDecimal.valueOf(100000));
    assertThat(budget.getForeignCurrency()).isEqualTo("USD");
    assertThat(budget.getForeignBalance()).isEqualTo(BigDecimal.valueOf(71.75));
    assertThat(budget.getAvgExchangeRate()).isEqualTo(BigDecimal.valueOf(1393.7));
  }

  @Test
  @DisplayName("수정을 위한 toEntity는 request 속 총 예산, 환전 여부/환율 입력 여부에 따라 선택 수정하고 반환한다")
  void toEntity_update_properties_selectively_depend_on_request() throws Exception {

    // given
    BudgetUpdateRequest request = BudgetUpdateRequest.builder()
        .totalAmount(BigDecimal.valueOf(150000))
        .build();
    Budget budget = Budget.builder()
        .teamId(1L)
        .totalAmount(BigDecimal.valueOf(100000))
        .setBy(1L)
        .balance(BigDecimal.valueOf(100000))
        .foreignCurrency("USD")
        .foreignBalance(BigDecimal.valueOf(71.75))
        .avgExchangeRate(BigDecimal.valueOf(1393.7))
        .build();

    // when
    Budget updatedBudget = budgetMapper.toEntity(request, budget);

    // then
    assertThat(updatedBudget.getTotalAmount()).isEqualTo(BigDecimal.valueOf(150000));
    assertThat(updatedBudget.getBalance()).isEqualTo(BigDecimal.valueOf(150000));
    assertThat(updatedBudget.getAvgExchangeRate()).isEqualTo(BigDecimal.valueOf(1393.7));
    assertThat(updatedBudget.getForeignBalance()).isEqualTo(BigDecimal.valueOf(107.63));

  }

}