package com.luckyseven.backend.domain.team.util;

import com.luckyseven.backend.domain.team.dto.TeamMemberDto;
import com.luckyseven.backend.domain.team.entity.TeamMember;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class TeamMemberMapper {

  /**
   * TeamMember 엔티티를 TeamMemberDto로 변환합니다.
   *
   * @param teamMember 변환할 팀멤버 엔티티
   * @return 변환된 팀멤버 DTO
   */
  public TeamMemberDto toDto(TeamMember teamMember) {
    if (teamMember == null) {
      return null;
    }

    return TeamMemberDto.builder()
        .id(teamMember.getId())
        .teamId(teamMember.getTeam().getId())
        .teamName(teamMember.getTeam().getName())
        .memberId(teamMember.getMember().getId())
        .memberName(teamMember.getMember().getName())
        .memberEmail(teamMember.getMember().getEmail())
        .build();
  }

  /**
   * TeamMember 엔티티 리스트를 TeamMemberDto 리스트로 변환합니다.
   *
   * @param teamMembers 변환할 팀멤버 엔티티 리스트
   * @return 변환된 팀멤버 DTO 리스트
   */
  public List<TeamMemberDto> toDtoList(List<TeamMember> teamMembers) {
    if (teamMembers == null) {
      return List.of();
    }

    return teamMembers.stream()
        .map(this::toDto)
        .toList();
  }

}
