package com.luckyseven.backend.domain.team.service;

import com.luckyseven.backend.domain.team.dto.TeamMemberDto;
import com.luckyseven.backend.domain.team.entity.TeamMember;
import com.luckyseven.backend.domain.team.repository.TeamMemberRepository;
import com.luckyseven.backend.domain.team.repository.TeamRepository;
import com.luckyseven.backend.domain.team.util.TeamMemberMapper;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

  private final TeamRepository teamRepository;
  private final TeamMemberRepository teamMemberRepository;
  private final TeamMemberMapper teamMemberMapper;


  @Transactional(readOnly = true)
  public List<TeamMemberDto> getTeamMemberByTeamId(Long id) {
    if (!teamRepository.existsById(id)) {
      throw new CustomLogicException(ExceptionCode.TEAM_NOT_FOUND,
          "ID가 [%d]인 팀을 찾을 수 없습니다", id);
    }
    List<TeamMember> teamMembers = teamMemberRepository.findByTeamId(id);
    return teamMemberMapper.toDtoList(teamMembers);
  }

  @Transactional
  public void removeTeamMember(Long teamId, Long teamMemberId) {
    if (!teamRepository.existsById(teamId)) {
      throw new CustomLogicException(ExceptionCode.TEAM_NOT_FOUND,
          "ID가 [%d]인 팀을 찾을 수 없습니다", teamId);

    }
    // 팀 멤버 존재 여부 확인
    TeamMember teamMember = teamMemberRepository.findById(teamMemberId)
        .orElseThrow(() -> new CustomLogicException(ExceptionCode.TEAM_MEMBER_NOT_FOUND));

    if (!teamMember.getTeam().getId().equals(teamId)) {
      throw new CustomLogicException(ExceptionCode.NOT_TEAM_MEMBER,
          "팀 멤버 ID [%d]는 팀 ID [%d]에 속한 멤버가 아닙니다", teamMemberId, teamId);
    }
    // 팀 멤버 삭제
    teamMemberRepository.deleteById(teamMemberId);
  }

}
