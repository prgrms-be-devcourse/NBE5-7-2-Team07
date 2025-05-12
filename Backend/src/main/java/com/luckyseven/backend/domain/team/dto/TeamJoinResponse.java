package com.luckyseven.backend.domain.team.dto;

import com.luckyseven.backend.domain.team.entity.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class TeamJoinResponse {

  private Long id;
  private String teamName;
  private String teamCode;
  private Long leaderId;
}
