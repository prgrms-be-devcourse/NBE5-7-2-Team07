package com.luckyseven.backend.domain.team.util;

import com.luckyseven.backend.domain.team.dto.TeamCreateRequest;
import com.luckyseven.backend.domain.team.dto.TeamCreateResponse;
import com.luckyseven.backend.domain.team.dto.TeamDashboardResponse;
import com.luckyseven.backend.domain.team.dto.TeamDashboardResponse.ExpenseDto;
import com.luckyseven.backend.domain.team.dto.TeamJoinResponse;
import com.luckyseven.backend.domain.team.entity.Budget;
import com.luckyseven.backend.domain.team.entity.Expense;
import com.luckyseven.backend.domain.team.entity.Member;
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.entity.TeamMember;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

  /**
   *
   * TeamCreateRequest와 멤버 정보를 Team 엔티티로 변환한다.
   * @param request 요청
   * @param leader 리더가 될 멤버
   * @param teamCode 팀 join 시 필요한 팀 코드
   * @return Team 엔티티
   */
  public Team toTeamEntity(TeamCreateRequest request, Member leader, String teamCode) {
    return Team.builder()
        .name(request.getName())
        .teamCode(teamCode)
        .teamPassword(request.getTeamPassword())
        .leader(leader)
        .build();
  }

  /**
   * Member 와 Team 정보를 TeamMember 엔티티로 변환환다
   * @param member 연결할 멤버
   * @param team 연결할 팀
   * @return TeamMember 엔티티
   */
  public TeamMember toTeamMemberEntity(Member member, Team team) {
    return TeamMember.builder()
        .team(team)
        .member(member)
        .build();
  }


  /**
   * Team 엔티티를 TeamCreateResponse로 변환합니다.
   *
   * @param team 변환할 팀 엔티티
   * @return 변환된 팀 생성 응답 DTO
   */
  public TeamCreateResponse toTeamCreateResponse(Team team) {
    if (team == null) {
      return null;
    }
    return TeamCreateResponse.builder()
        .id(team.getId())
        .name(team.getName())
        .teamCode(team.getTeamCode())
        .leaderId(team.getLeader().getId())
        .build();
  }

  /**
   * Team 엔티티를 TeamJoinResponse로 변환합니다.
   *
   * @param team 변환할 팀 엔티티
   * @return 변환된 팀 참가 응답 DTO
   */
  public TeamJoinResponse toTeamJoinResponse(Team team) {
    if (team == null) {
      return null;
    }
    return TeamJoinResponse.builder()
        .id(team.getId())
        .teamName(team.getName())
        .teamCode(team.getTeamCode())
        .leaderId(team.getLeader().getId())
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
            .payer(expense.getPayer().getId())
            .build());
      }
    }

    return TeamDashboardResponse.builder()
        .team_id(team.getId())
        .currency(budget != null ? budget.getCurrency() : BigDecimal.ZERO)
        .balance(budget != null ? budget.getBalance() : BigDecimal.ZERO)
        .foreignBalance(budget != null ? budget.getForeignBalance() : BigDecimal.ZERO)
        .totalAmount(budget != null ? budget.getTotalAmount() : BigDecimal.ZERO)
        .avgExchangeRate(budget != null ? budget.getAvgExchangeRate() : BigDecimal.ZERO)
        .expenseList(expenseDtos)
        .build();
  }
}
