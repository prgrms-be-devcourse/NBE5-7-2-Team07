package com.luckyseven.backend.domain.budget.validator;

import com.luckyseven.backend.domain.budget.dao.BudgetRepository;
import com.luckyseven.backend.domain.budget.dto.BudgetBaseRequest;
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
      // TODO: ExceptionCode에 BUDGET_CONFLICT 추가
      throw new CustomLogicException(ExceptionCode.BUDGET_CONFLICT,
          "budgetId: " + budgetOptional.get().getId());
    }
  }

  public Budget validateBudgetExist(Long teamId) {
    // TODO: ExceptionCode에 TEAM_NOT_FOUND 추가
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
  }
}
