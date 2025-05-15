package com.luckyseven.backend.domain.team.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamMemberDto {

  private Long id;
  private Long teamId;

  @Size(min = 2, max = 30, message = "팀 이름은 2자 이상 30자 이하여야 합니다")
  private String teamName;
  private Long memberId;

  @Size(min = 2, max = 30, message = "회원 이름은 2자 이상 30자 이하여야 합니다")
  private String memberNickName;

  @Email(message = "올바른 이메일 형식이 아닙니다")
  private String memberEmail;
}