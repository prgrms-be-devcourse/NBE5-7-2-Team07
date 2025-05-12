package com.luckyseven.backend.domain.team.util;

import com.luckyseven.backend.domain.team.dto.TeamCreateResponse;
import com.luckyseven.backend.domain.team.dto.TeamJoinRequest;
import com.luckyseven.backend.domain.team.dto.TeamJoinResponse;
import com.luckyseven.backend.domain.team.entity.Team;
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
}
