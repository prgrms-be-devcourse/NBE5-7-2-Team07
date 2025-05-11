package com.luckyseven.backend.domain.team.controller;

import com.luckyseven.backend.domain.team.dto.TeamJoinRequest;
import com.luckyseven.backend.domain.team.dto.TeamJoinResponse;
import com.luckyseven.backend.domain.team.entity.Member;
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.service.TeamService;
import com.luckyseven.backend.domain.team.service.TempMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TeamController {

  private final TeamService teamService;
  private final TempMemberService tempMemberService;

  @PostMapping("/api/team")
  public ResponseEntity<TeamJoinResponse> joinTeam(@RequestBody TeamJoinRequest request) {
    // 현재 로그인한 회원 정보 get
    Member currentMember = tempMemberService.getCurrentMember();

    // Service
    Team joinedTeam = teamService.joinTeam(currentMember, request.getTeamCode(),
        request.getTeamPassword());

    // 응답
    TeamJoinResponse response = TeamJoinResponse.from(joinedTeam);

    return ResponseEntity.ok(response);
  }


}
