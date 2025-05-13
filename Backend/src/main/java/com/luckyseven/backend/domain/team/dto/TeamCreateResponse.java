package com.luckyseven.backend.domain.team.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String teamPassword;
}
