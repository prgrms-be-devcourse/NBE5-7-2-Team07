package com.luckyseven.backend.domain.team.dto;

import com.luckyseven.backend.domain.team.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamJoinResponse {
  private Long id;
  private String teamName;
  private String teamCode;

  public static TeamJoinResponse from(Team team) {
    return TeamJoinResponse.builder()
        .id(team.getId())
        .teamName(team.getName())
        .teamCode(team.getTeamCode())
        .build();
  }
}
