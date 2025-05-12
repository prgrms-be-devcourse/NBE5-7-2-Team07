package com.luckyseven.backend.domain.team.controller;

import com.luckyseven.backend.domain.team.dto.TeamCreateRequest;
import com.luckyseven.backend.domain.team.dto.TeamCreateResponse;
import com.luckyseven.backend.domain.team.dto.TeamJoinRequest;
import com.luckyseven.backend.domain.team.dto.TeamJoinResponse;
import com.luckyseven.backend.domain.team.entity.Member;
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.service.TeamService;
import com.luckyseven.backend.domain.team.service.TempMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "팀 관리", description = "팀 도메인 API")
public class TeamController {

  private final TeamService teamService;
  private final TempMemberService tempMemberService;

  @Operation(
      summary = "팀 생성",
      description = "새로운 팀을 생성합니다",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "팀 생성 성공",
              content = @Content(schema = @Schema(implementation = TeamCreateResponse.class))
          )
      }
  )

  @PostMapping("/api/teams")
  public ResponseEntity<TeamCreateResponse> createTeam(
      @Parameter(hidden = true) @AuthenticationPrincipal Member member,
      @Parameter(description = "팀 생성 요청 정보") @Valid @RequestBody
      TeamCreateRequest request) {
    TeamCreateResponse response = teamService.createTeam(member, request);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "팀 참가",
      description = "유저가 존재하는 팀에 참가합니다",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "팀 참가 성공",
              content = @Content(schema = @Schema(implementation = TeamJoinResponse.class))
          )
      }
  )
  @PostMapping("/api/teams/members")
  public ResponseEntity<TeamJoinResponse> joinTeam(
      @Parameter(description = "팀 참가 요청 정보") @RequestBody TeamJoinRequest request) {
    // 현재 로그인한 회원 정보 get
    Member currentMember = tempMemberService.getCurrentMember();

    // Service
    TeamJoinResponse response = teamService.joinTeam(currentMember, request.getTeamCode(),
        request.getTeamPassword());

    return ResponseEntity.ok(response);
  }


}
