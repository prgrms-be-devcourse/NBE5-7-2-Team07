package com.luckyseven.backend.domain.budget.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luckyseven.backend.domain.budget.dao.BudgetRepository;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateResponse;
import com.luckyseven.backend.domain.budget.dto.BudgetReadResponse;
import com.luckyseven.backend.domain.budget.dto.BudgetUpdateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetUpdateResponse;
import com.luckyseven.backend.domain.budget.entity.Budget;
import com.luckyseven.backend.domain.budget.entity.CurrencyCode;
import com.luckyseven.backend.domain.budget.mapper.BudgetMapper;
import com.luckyseven.backend.domain.budget.validator.BudgetValidator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTests {

  @InjectMocks
  private BudgetService budgetService;
  @Mock
  private BudgetRepository budgetRepository;
  @Mock
  private BudgetMapper budgetMapper;
  @Mock
  private BudgetValidator budgetValidator;

  @Test
  @DisplayName("save는 환전한 Budget을 저장하고 CreateResponse를 반환한다")
  void save_create_exchanged_budget_and_return_create_response() throws Exception {

    // given
    Long teamId = 1L;
    Long loginMemberId = 1L;
    BudgetCreateRequest request = BudgetCreateRequest.builder()
        .totalAmount(BigDecimal.valueOf(100000))
        .isExchanged(true)
        .exchangeRate(BigDecimal.valueOf(1393.7))
        .foreignCurrency(CurrencyCode.USD)
        .build();
    Budget budget = Budget.builder()
        .teamId(teamId)
        .totalAmount(request.getTotalAmount())
        .setBy(loginMemberId)
        .balance(request.getTotalAmount())
        .foreignCurrency(request.getForeignCurrency())
        .build();
    budget.setAvgExchangeRate(request.getIsExchanged(), request.getExchangeRate());
    BudgetCreateResponse expectedResponse = BudgetCreateResponse.builder()
        .id(1L)
        .balance(BigDecimal.valueOf(100000))
        .foreignBalance(BigDecimal.valueOf(71.75))
        .avgExchangeRate(BigDecimal.valueOf(1393.7))
        .build();

    when(budgetMapper.toCreateResponse(any(Budget.class))).thenReturn(expectedResponse);

    // when
    BudgetCreateResponse response = budgetService.save(teamId, loginMemberId, request);

    // then
    verify(budgetValidator).validateBudgetNotExist(teamId);
    verify(budgetRepository).save(any(Budget.class));
    verify(budgetMapper).toCreateResponse(any(Budget.class));

    assertThat(response).isNotNull();
    assertThat(response.getBalance()).isEqualTo(expectedResponse.getBalance());

  }

  @Test
  @DisplayName("getByTeamId는 teamId를 통해 Budget을 조회하고 ReadResponse로 반환한다")
  void getByTeamId_read_budget_by_team_id_and_return_read_response() throws Exception {

    // given
    Long teamId = 1L;
    Budget budget = Budget.builder()
        .teamId(1L)
        .totalAmount(BigDecimal.valueOf(100000))
        .balance(BigDecimal.valueOf(100000))
        .foreignCurrency(CurrencyCode.USD)
        .foreignBalance(BigDecimal.valueOf(71.75))
        .avgExchangeRate(BigDecimal.valueOf(1393.7))
        .build();
    BudgetReadResponse expectedResponse = BudgetReadResponse.builder()
        .id(1L)
        .totalAmount(BigDecimal.valueOf(100000))
        .balance(BigDecimal.valueOf(100000))
        .foreignCurrency(CurrencyCode.USD)
        .foreignBalance(BigDecimal.valueOf(71.75))
        .avgExchangeRate(BigDecimal.valueOf(1393.70))
        .build();

    when(budgetValidator.validateBudgetExist(teamId)).thenReturn(budget);
    when(budgetMapper.toReadResponse(budget)).thenReturn(expectedResponse);

    // when
    BudgetReadResponse response = budgetService.getByTeamId(teamId);

    // then
    assertThat(response).isEqualTo(expectedResponse);

  }

  @Test
  @DisplayName("updateByTeamId는 Budget을 업데이트하고 UpdateResponse를 반환한다")
  void updateByTeamId_update_budget_and_return_update_response() throws Exception {

    // given
    Long teamId = 1L;
    Long loginMemberId = 1L;
    Budget budget = Budget.builder()
        .teamId(teamId)
        .totalAmount(BigDecimal.valueOf(100000))
        .balance(BigDecimal.valueOf(100000))
        .foreignCurrency(CurrencyCode.USD)
        .foreignBalance(BigDecimal.valueOf(71.75))
        .avgExchangeRate(BigDecimal.valueOf(1393.7))
        .build();
    BudgetUpdateRequest request = BudgetUpdateRequest.builder()
        .totalAmount(BigDecimal.valueOf(150000))
        .build();
    BudgetUpdateResponse expectedResponse = BudgetUpdateResponse.builder()
        .id(1L)
        .setBy(loginMemberId)
        .balance(BigDecimal.valueOf(150000))
        .foreignBalance(BigDecimal.valueOf(107.63))
        .foreignCurrency(CurrencyCode.USD)
        .avgExchangeRate(BigDecimal.valueOf(1393.7))
        .build();

    when(budgetValidator.validateBudgetExist(teamId)).thenReturn(budget);
    when(budgetMapper.toUpdateResponse(any(Budget.class))).thenReturn(expectedResponse);

    // when
    BudgetUpdateResponse response = budgetService.updateByTeamId(teamId, loginMemberId, request);

    // then
    assertThat(response).isNotNull();
    assertThat(response.getBalance()).isEqualTo(expectedResponse.getBalance());
    assertThat(response.getForeignBalance()).isEqualTo(expectedResponse.getForeignBalance());

    assertThat(budget.getTotalAmount()).isEqualTo(request.getTotalAmount());
    assertThat(budget.getBalance()).isEqualTo(request.getTotalAmount());
  }

  @Test
  @DisplayName("deleteByTeamId는 budget을 삭제한다")
  void deleteByTeamId_delete_budget() throws Exception {

    // given
    Long teamId = 1L;
    Budget budget = Budget.builder()
        .teamId(2L)
        .totalAmount(BigDecimal.valueOf(100000))
        .balance(BigDecimal.valueOf(100000))
        .foreignCurrency(CurrencyCode.USD)
        .foreignBalance(null)
        .avgExchangeRate(null)
        .build();

    when(budgetValidator.validateBudgetExist(teamId)).thenReturn(budget);

    // when
    budgetService.deleteByTeamId(teamId);

    // then
    verify(budgetRepository).delete(budget);

  }
}