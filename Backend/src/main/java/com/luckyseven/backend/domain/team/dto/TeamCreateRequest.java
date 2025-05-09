package com.luckyseven.backend.domain.team.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCreateRequest {

  @NotBlank(message = "팀 이름은 필수입니다")
  private String name;

  @NotBlank(message = "팀 비밀번호는 필수입니다")
  private String teamPassword;
}
