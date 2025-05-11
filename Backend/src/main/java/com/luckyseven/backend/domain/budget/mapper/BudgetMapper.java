package com.luckyseven.backend.domain.budget.mapper;

import com.luckyseven.backend.domain.budget.dto.BudgetCreateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateResponse;
import com.luckyseven.backend.domain.budget.entity.Budget;
import java.math.BigDecimal;

public class BudgetMapper {
  public static Budget toEntity(Long teamId, BudgetCreateRequest request) {

    BigDecimal exchangeRate;
    BigDecimal foreignBalance;
    BigDecimal avgExchangeRate;
    if (request.getExchangeRate() != null) {
      exchangeRate = request.getExchangeRate();
      foreignBalance = request.getTotalAmount()
          .divide(exchangeRate, 2, BigDecimal.ROUND_HALF_UP);
      avgExchangeRate = request.getExchangeRate();
    } else {
      exchangeRate = BigDecimal.ONE;
      foreignBalance = BigDecimal.ZERO;
      // TODO: 팀원들과 상의 필요
      // 상황: 예산 입력 시 환전 여부 = false를 선택 -> 환율을 입력 받지 않음
      // 해결1: false를 선택하더라도 환율을 입력 받아 저장
      // 해결2: API를 사용하여 현재 환율 정보 가져오기(한국수출입은행 사용)
      // 1. 호출 시, 최근 호출 시간 업데이트
      // 2. 일정 범위 안이면 기존에 저장된 것 호출(일일 호출 가능 횟수를 1000회 제한)
      // 3. 범위 벗어나면 새로 호출
      // 비영업일의 데이터, 혹은 영업당일 11시 이전에 해당일의 데이터를 요청할 경우 null 값이 반환
      avgExchangeRate = BigDecimal.ZERO;
    }

    return Budget.builder()
        .teamId(teamId)
        .totalAmount(request.getTotalAmount())
        .exchangeRate(exchangeRate)
        .setBy(request.getSetBy())
        .balance(request.getTotalAmount())
        .foreignBalance(foreignBalance)
        .foreignCurrency(request.getForeignCurrency())
        .avgExchangeRate(avgExchangeRate)
        .build();
  }

  public static BudgetCreateResponse toCreateResponse(Budget budget) {

    return BudgetCreateResponse.builder()
        .id(budget.getId())
        .balance(budget.getBalance())
        .foreignBalance(budget.getForeignBalance())
        .avgExchangeRate(budget.getAvgExchangeRate())
        .createdAt(budget.getCreatedAt())
        .updatedAt(budget.getUpdatedAt())
        .build();
  }

//  public static BudgetReadResponse toReadResponse(Budget budget) {
//    return null;
//  }
//
//  public static Budget toEntity(BudgetUpdateRequest request) {
//    return null;
//  }
//
//  public static BudgetUpdateResponse toUpdateResponse(Budget budget) {
//    return null;
//  }

}
