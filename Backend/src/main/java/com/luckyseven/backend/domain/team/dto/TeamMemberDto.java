package com.luckyseven.backend.domain.team.dto;

import com.luckyseven.backend.domain.team.entity.TeamMember;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TeamMemberDto {

  private Long id;
  private Long teamId;
  private String teamName;
  private Long memberId;
  private String memberName;
  private String memberEmail;
}