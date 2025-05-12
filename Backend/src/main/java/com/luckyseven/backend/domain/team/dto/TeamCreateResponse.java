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
  private String teamPassword;
}
