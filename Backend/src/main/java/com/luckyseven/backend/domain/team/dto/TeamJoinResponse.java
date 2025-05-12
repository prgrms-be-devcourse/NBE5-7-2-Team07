package com.luckyseven.backend.domain.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TeamJoinResponse {

  private Long id;
  private String teamName;
  private String teamCode;
  private Long leaderId;
}
