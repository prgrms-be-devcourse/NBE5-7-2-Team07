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

  // 엔티티를 DTO로 변환
  public static TeamMemberDto from(TeamMember teamMember) {
    return TeamMemberDto.builder()
        .id(teamMember.getId())
        .teamId(teamMember.getTeam().getId())
        .teamName(teamMember.getTeam().getName())
        .memberId(teamMember.getMember().getId())
        .memberName(teamMember.getMember().getName())
        .memberEmail(teamMember.getMember().getEmail())
        .build();
  }
}