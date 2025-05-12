package com.luckyseven.backend.domain.team.util;

import com.luckyseven.backend.domain.team.dto.TeamCreateResponse;
import com.luckyseven.backend.domain.team.dto.TeamDashboardResponse;
import com.luckyseven.backend.domain.team.dto.TeamDashboardResponse.ExpenseDto;
import com.luckyseven.backend.domain.team.dto.TeamJoinRequest;
import com.luckyseven.backend.domain.team.dto.TeamJoinResponse;
import com.luckyseven.backend.domain.team.entity.Budget;
import com.luckyseven.backend.domain.team.entity.Expense;
import com.luckyseven.backend.domain.team.entity.Team;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

  /**
   * Team 엔티티를 TeamCreateResponse로 변환합니다.
   *
   * @param team 변환할 팀 엔티티
   * @return 변환된 팀 생성 응답 DTO
   */
  public TeamCreateResponse toCreateResponse(Team team) {
    if (team == null) {
      return null;
    }
    return TeamCreateResponse.builder()
        .id(team.getId())
        .name(team.getName())
        .teamCode(team.getTeamCode())
        .leaderId(team.getLeaderId())
        .teamPassword(team.getTeamPassword())
        .build();
  }

  /**
   * Team 엔티티를 TeamJoinResponse로 변환합니다.
   *
   * @param team 변환할 팀 엔티티
   * @return 변환된 팀 참가 응답 DTO
   */
  public TeamJoinResponse toJoinResponse(Team team) {
    if (team == null) {
      return null;
    }
    return TeamJoinResponse.builder()
        .id(team.getId())
        .teamName(team.getName())
        .teamCode(team.getTeamCode())
        .leaderId(team.getLeaderId())
        .build();
  }

  /**
   * Team, Budget과 Expense 목록을 TeamDashboardResponse로 변환합니다.
   *
   * @param team 변환할 팀 엔티티
   * @param budget 팀의 예산 정보
   * @param expenses 팀의 지출 목록
   * @return 변환된 팀 대시보드 응답 DTO
   */
  public TeamDashboardResponse toTeamDashboardResponse(Team team, Budget budget, List<Expense> expenses) {
    if (team == null) {
      return null;
    }

    List<ExpenseDto> expenseDtos = new ArrayList<>();
    if (expenses != null) {
      for (Expense expense : expenses) {
        expenseDtos.add(TeamDashboardResponse.ExpenseDto.builder()
            .description("") // Expense 엔티티에 description 필드가 없음 - 필요시 추가
            .amount(expense.getAmount())
            .category(expense.getCategory())
            .date(expense.getCreatedAt()) // BaseEntity에서 상속받은 createdAt을 사용
            .payer(expense.getPayerId())
            .build());
      }
    }

    return TeamDashboardResponse.builder()
        .team_id(team.getId())
        .currency(budget != null ? budget.getCurrency() : BigDecimal.ZERO)
        .balance(budget != null ? budget.getBalance() : BigDecimal.ZERO)
        .foreignBalance(budget != null ? budget.getForeignBalance() : BigDecimal.ZERO)
        .totalAmount(budget != null ? budget.getTotalAmount() : BigDecimal.ZERO)
        .exchangeRate(budget != null ? budget.getExchangeRate() : BigDecimal.ZERO)
        .avgExchangeRate(budget != null ? budget.getAvgExchangeRate() : BigDecimal.ZERO)
        .expenseList(expenseDtos)
        .build();
  }
}
