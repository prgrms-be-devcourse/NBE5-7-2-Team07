package com.luckyseven.backend.domain.team.dto;

import com.luckyseven.backend.domain.team.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCreateResponse {
  private Long id;
  private String name;
  private String teamCode;
  private Long leaderId;

  public static TeamCreateResponse from(Team team) {
    return TeamCreateResponse.builder()
        .id(team.getId())
        .name(team.getName())
        .teamCode(team.getTeamCode())
        .leaderId(team.getLeaderId())
        .build();
  }
}
