package com.luckyseven.backend.domain.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TeamJoinRequest {

  private String teamCode;
  private String teamPassword;
}
