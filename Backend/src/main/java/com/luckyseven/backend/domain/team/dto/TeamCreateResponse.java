package com.luckyseven.backend.domain.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TeamCreateResponse {

  private Long id;
  private String name;
  private String teamCode;
  private Long leaderId;
  private String teamPassword;
}
