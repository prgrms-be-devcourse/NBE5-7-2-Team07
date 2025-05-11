package com.luckyseven.backend.domain.team.service;

import com.luckyseven.backend.domain.team.dto.TeamCreateRequest;
import com.luckyseven.backend.domain.team.entity.Member;
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.entity.TeamMember;
import com.luckyseven.backend.domain.team.repository.TeamMemberRepository;
import com.luckyseven.backend.domain.team.repository.TeamRepository;
import java.util.UUID;

public class TeamService {

  TeamRepository teamRepository;
  TeamMemberRepository teamMemberRepository;

  /**
   * 팀을 생성한다. 생성한 회원을 팀 리더로 등록한다
   *
   * @param creator 팀 생성하는 회원
   * @param request 팀 생성 요청
   * @return 생성된 팀 정보
   */
  public Team createTeam(Member creator, TeamCreateRequest request) {
    String teamCode = generateTeamCode();
    Team team = Team.builder()
        .name(request.getName())
        .teamCode(teamCode)
        .teamPassword(request.getTeamPassword())
        .leaderId(creator.getId())
        .build();

    Team savedTeam = teamRepository.save(team);

    TeamMember teamMember = TeamMember.builder()
        .team(savedTeam)
        .member(creator)
        .build();

    // 리더를 TeamMember 에 추가
    teamMemberRepository.save(teamMember);

    savedTeam.addTeamMember(teamMember);
    return savedTeam;
  }

  /**
   * 팀 코드를 생성한다
   * @return 생성된 팀 코드
   */
  private String generateTeamCode() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

}
