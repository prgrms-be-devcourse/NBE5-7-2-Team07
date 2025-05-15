package com.luckyseven.backend.domain.team.service;

import com.luckyseven.backend.domain.team.dto.TeamCreateRequest;
import com.luckyseven.backend.domain.team.dto.TeamCreateResponse;
import com.luckyseven.backend.domain.team.dto.TeamDashboardResponse;
import com.luckyseven.backend.domain.team.dto.TeamJoinResponse;
import com.luckyseven.backend.domain.team.entity.Budget;
import com.luckyseven.backend.domain.team.entity.Expense;
import com.luckyseven.backend.domain.member.entity.Member;
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.entity.TeamMember;
import com.luckyseven.backend.domain.team.repository.BudgetRepository;
import com.luckyseven.backend.domain.team.repository.ExpenseRepository;
import com.luckyseven.backend.domain.team.repository.TeamMemberRepository;
import com.luckyseven.backend.domain.team.repository.TeamRepository;
import com.luckyseven.backend.domain.team.util.TeamMapper;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

  @Mock
  private TeamRepository teamRepository;

  @Mock
  private TeamMemberRepository teamMemberRepository;

  @Mock
  private BudgetRepository budgetRepository;

  @Mock
  private ExpenseRepository expenseRepository;

  @InjectMocks
  private TeamService teamService;

  @Mock
  private TeamMapper teamMapper;

  private Team team;
  private Member creator;
  private TeamCreateRequest request;

  @BeforeEach
  void setUp() {
    creator = Member.builder()
        .id(1L)
        .name("테스터")
        .email("text@example.com")
        .nickname("테스터")
        .build();

    request = TeamCreateRequest.builder()
        .name("test_team")
        .teamPassword("password")
        .build();

    team = Team.builder()
        .id(1L)
        .name("test_team")
        .teamCode("ABCDEF")
        .leader(creator)
        .teamPassword("password")
        .build();

  }

  @Test
  void createTeam() {
    //given
    given(teamRepository.save(any(Team.class))).willReturn(team);

    TeamCreateResponse expectedResponse = TeamCreateResponse.builder()
        .id(team.getId())
        .name(team.getName())
        .teamCode(team.getTeamCode())
        .leaderId(team.getLeader().getId())
        .teamPassword(team.getTeamPassword())
        .build();

    given(teamMapper.toTeamCreateResponse(any(Team.class))).willReturn(expectedResponse);

    //when
    TeamCreateResponse result = teamService.createTeam(creator, request);

    //then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(team.getId());
    assertThat(result.getName()).isEqualTo(request.getName());
    assertThat(result.getTeamPassword()).isEqualTo(request.getTeamPassword());
    assertThat(result.getLeaderId()).isEqualTo(creator.getId());

    //팀 저장
    ArgumentCaptor<Team> teamCaptor = ArgumentCaptor.forClass(Team.class);
    verify(teamRepository).save(teamCaptor.capture());
    assertThat(teamCaptor.getValue().getName()).isEqualTo(request.getName());
    assertThat(teamCaptor.getValue().getLeader().getId()).isEqualTo(creator.getId());
    assertThat(teamCaptor.getValue().getTeamPassword()).isEqualTo(request.getTeamPassword());

    //팀 멤버 저장
    ArgumentCaptor<TeamMember> teamMemberCaptor = ArgumentCaptor.forClass(TeamMember.class);
    verify(teamMemberRepository).save(teamMemberCaptor.capture());
    TeamMember capturedTeamMember = teamMemberCaptor.getValue();
    assertThat(capturedTeamMember.getMember()).isEqualTo(creator);
    assertThat(capturedTeamMember.getTeam()).isEqualTo(team);
  }

  @Test
  void joinTeam() {
    // given

    String teamCode = "ABCDEF";
    String teamPassword = "password";

    given(teamRepository.findByTeamCode(teamCode)).willReturn(Optional.of(team));

    // 새로운 멤버
    Member newMember = Member.builder()
        .id(2L)
        .name("new")
        .nickname("newMem")
        .build();

    // 새로운 TeamMember 객체 생성
    TeamMember teamMember = TeamMember.builder()
        .team(team)
        .member(newMember)
        .build();

    given(teamMemberRepository.save(any(TeamMember.class))).willReturn(teamMember);

    TeamJoinResponse expectedResponse = TeamJoinResponse.builder()
        .id(team.getId())
        .teamName(team.getName())
        .teamCode(team.getTeamCode())
        .leaderId(team.getLeader().getId())
        .build();

    given(teamMapper.toTeamJoinResponse(team)).willReturn(expectedResponse);

    // when
    TeamJoinResponse result = teamService.joinTeam(newMember, teamCode, teamPassword);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(team.getId());
    assertThat(result.getTeamName()).isEqualTo(team.getName());
    assertThat(result.getLeaderId()).isEqualTo(team.getLeader().getId());
    assertThat(result.getTeamCode()).isEqualTo(team.getTeamCode());

    // 팀 멤버 저장
    ArgumentCaptor<TeamMember> teamMemberCaptor = ArgumentCaptor.forClass(TeamMember.class);
    verify(teamMemberRepository).save(teamMemberCaptor.capture());
    TeamMember capturedTeamMember = teamMemberCaptor.getValue();
    assertThat(capturedTeamMember.getMember()).isEqualTo(newMember);
    assertThat(capturedTeamMember.getTeam()).isEqualTo(team);
  }

  @Test
  void 대시보드를get_대시보드Response_예상() {
    // Given
    Long teamId = 1L;
    Team team = mock(Team.class);

    Budget budget = mock(Budget.class);

    List<Expense> expenses = new ArrayList<>();

    TeamDashboardResponse expectedResponse = mock(TeamDashboardResponse.class);

    when(teamRepository.findById(teamId)).thenReturn(Optional.of(team));
    when(budgetRepository.findByTeamId(teamId)).thenReturn(Optional.of(budget));
    when(expenseRepository.findAllByTeamId(teamId)).thenReturn(expenses);
    when(teamMapper.toTeamDashboardResponse(team, budget, expenses)).thenReturn(expectedResponse);

    // When
    TeamDashboardResponse result = teamService.getTeamDashboard(teamId);

    // Then
    assertEquals(expectedResponse, result);
    verify(teamRepository).findById(teamId);
    verify(budgetRepository).findByTeamId(teamId);
    verify(expenseRepository).findAllByTeamId(teamId);
    verify(teamMapper).toTeamDashboardResponse(team, budget, expenses);
  }

  @Test
  void getTeamDashboard_WhenTeamNotFound_ShouldThrowException() {
    // Given
    Long teamId = 1L;
    when(teamRepository.findById(teamId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(CustomLogicException.class, () -> teamService.getTeamDashboard(teamId));
    verify(teamRepository).findById(teamId);
  }

}