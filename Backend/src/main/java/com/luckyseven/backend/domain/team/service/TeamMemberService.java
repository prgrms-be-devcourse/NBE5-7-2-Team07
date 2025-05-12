package com.luckyseven.backend.domain.team.service;

import com.luckyseven.backend.domain.team.dto.TeamMemberDto;
import com.luckyseven.backend.domain.team.entity.TeamMember;
import com.luckyseven.backend.domain.team.repository.TeamMemberRepository;
import com.luckyseven.backend.domain.team.repository.TeamRepository;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamMemberService {

  private final TeamRepository teamRepository;
  private final TeamMemberRepository teamMemberRepository;

  public TeamMemberService(TeamRepository teamRepository, TeamMemberRepository teamMemberRepository) {
    this.teamRepository = teamRepository;
    this.teamMemberRepository = teamMemberRepository;
  }

  @Transactional(readOnly = true)
  public List<TeamMemberDto> getTeamMemberByTeamId(Long id) {
    if(!teamRepository.existsById(id)){
      new NoSuchElementException("팀 없음");
    }
    return teamMemberRepository.findByTeamId(id).stream().map(TeamMemberDto::from).toList();
  }

  @Transactional
  public void removeTeamMember(Long teamId, Long teamMemberId) {
    if(!teamRepository.existsById(teamId)){
      throw new NoSuchElementException("팀을 찾을 수 없습니다");
    }
    // 팀 멤버 존재 여부 확인
    TeamMember teamMember = teamMemberRepository.findById(teamMemberId).orElseThrow(() -> new NoSuchElementException("팀 멤버 찾기 실패"));

    if(!teamMember.getTeam().getId().equals(teamId)){
      throw  new IllegalArgumentException("해당 팀에 속한 멤버가 아닙니다");
    }
    // 팀 멤버 삭제
    teamMemberRepository.deleteById(teamMemberId);
  }

}
