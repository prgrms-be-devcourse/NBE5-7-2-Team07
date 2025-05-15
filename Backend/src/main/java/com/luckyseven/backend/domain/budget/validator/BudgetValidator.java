package com.luckyseven.backend.domain.budget.validator;

import com.luckyseven.backend.domain.budget.dao.BudgetRepository;
import com.luckyseven.backend.domain.budget.dto.BudgetBaseRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetUpdateRequest;
import com.luckyseven.backend.domain.budget.entity.Budget;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BudgetValidator {

  private final BudgetRepository budgetRepository;

  public void validateBudgetNotExist(Long teamId) {
    Optional<Budget> budgetOptional = budgetRepository.findByTeamId(teamId);

    if (budgetOptional.isPresent()) {
      throw new CustomLogicException(ExceptionCode.BUDGET_CONFLICT,
          "budgetId: " + budgetOptional.get().getId());
    }
  }

  public Budget validateBudgetExist(Long teamId) {
    return budgetRepository.findByTeamId(teamId)
        .orElseThrow(() ->
            new CustomLogicException(ExceptionCode.TEAM_NOT_FOUND, "teamId: " + teamId)
        );
  }

  public void validateRequest(BudgetBaseRequest request) {
    if (Boolean.TRUE.equals(request.getIsExchanged())
        && request.getExchangeRate() == null) {
      throw new CustomLogicException(ExceptionCode.BAD_REQUEST, "환전 여부가 true인데 환율이 없습니다.");
    }
    if (Boolean.FALSE.equals(request.getIsExchanged()) && request.getExchangeRate() != null) {
      throw new CustomLogicException(ExceptionCode.BAD_REQUEST, "환전 여부가 false인데 환율이 있습니다.");
    }

    // BudgetUpdateRequest일 경우 추가 검증
    if (request instanceof BudgetUpdateRequest updateRequest) {
      // totalAmount와 additionalAmount는 함께 입력될 수 없음
      if (updateRequest.getTotalAmount() != null && updateRequest.getAdditionalBudget() != null) {
        throw new CustomLogicException(ExceptionCode.BAD_REQUEST,
            "예산 전체 수정과 추가는 함께 할 수 없습니다.");
      }
      // additionalAmount 입력 시 isExchanged 필수
      if (updateRequest.getAdditionalBudget() != null && updateRequest.getIsExchanged() == null) {
        throw new CustomLogicException(ExceptionCode.BAD_REQUEST,
            "예산 추가 시 환전 여부를 함께 입력해야 합니다.");
      }
    }
  }
}
