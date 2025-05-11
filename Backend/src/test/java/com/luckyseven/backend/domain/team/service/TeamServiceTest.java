package com.luckyseven.backend.domain.team.service;

import com.luckyseven.backend.domain.team.dto.TeamCreateRequest;
import com.luckyseven.backend.domain.team.entity.Member;
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.entity.TeamMember;
import com.luckyseven.backend.domain.team.repository.TeamMemberRepository;
import com.luckyseven.backend.domain.team.repository.TeamRepository;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

  @Mock
  private TeamRepository teamRepository;

  @Mock
  private TeamMemberRepository teamMemberRepository;

  @InjectMocks
  private TeamService teamService;

  private Member creator;
  private TeamCreateRequest request;

  @BeforeEach
  void setUp() {
    creator = Member.builder()
        .id(1L)
        .name("테스터")
        .email("text@example.com")
        .nickname("test")
        .build();

    request = TeamCreateRequest.builder()
        .name("test_team")
        .teamPassword("1234")
        .build();
  }

  @Test
  void createTeam() {
    //given

    Team savedTeam = Team.builder()
        .id(1L)
        .name(request.getName())
        .teamPassword(request.getTeamPassword())
        .teamCode("1234")
        .leaderId(creator.getId())
        .build();

    given(teamRepository.save(any(Team.class))).willReturn(savedTeam);

    //when
    Team result = teamService.createTeam(creator, request);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo(request.getName());
    assertThat(result.getTeamPassword()).isEqualTo(request.getTeamPassword());
    assertThat(result.getLeaderId()).isEqualTo(creator.getId());

    //팀 저장
    ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);
    verify(teamRepository).save(teamCaptor.capture());
    assertThat(teamCaptor.getValue().getName()).isEqualTo(request.getName());
    assertThat(teamCaptor.getValue().getLeaderId()).isEqualTo(creator.getId());
    assertThat(teamCaptor.getValue().getTeamPassword()).isEqualTo(request.getTeamPassword());

    //팀 멤버 저장
    ArgumentCaptor<TeamMember> teamMemberCaptor = ArgumentCaptor.forClass(TeamMember.class);
    verify(teamMemberRepository).save(teamMemberCaptor.capture());
    TeamMember capturedTeamMember = teamMemberCaptor.getValue();
    assertThat(capturedTeamMember.getMember()).isEqualTo(creator);
    assertThat(capturedTeamMember.getTeam()).isEqualTo(savedTeam);
  }
}