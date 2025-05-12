package com.luckyseven.backend.domain.team.service;

import com.luckyseven.backend.domain.team.dto.TeamCreateRequest;
import com.luckyseven.backend.domain.team.dto.TeamCreateResponse;
import com.luckyseven.backend.domain.team.dto.TeamDashboardResponse;
import com.luckyseven.backend.domain.team.dto.TeamJoinResponse;
import com.luckyseven.backend.domain.team.entity.Budget;
import com.luckyseven.backend.domain.team.entity.Expense;
import com.luckyseven.backend.domain.team.entity.Member;
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.entity.TeamMember;
import com.luckyseven.backend.domain.team.repository.BudgetRepository;
import com.luckyseven.backend.domain.team.repository.ExpenseRepository;
import com.luckyseven.backend.domain.team.repository.TeamMemberRepository;
import com.luckyseven.backend.domain.team.repository.TeamRepository;
import com.luckyseven.backend.domain.team.util.TeamMapper;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamService {

  TeamRepository teamRepository;
  TeamMemberRepository teamMemberRepository;
  BudgetRepository budgetRepository;
  ExpenseRepository expenseRepository;

  TeamMapper teamMapper;

  /**
   * 팀을 생성한다. 생성한 회원을 팀 리더로 등록한다
   *
   * @param creator 팀 생성하는 회원
   * @param request 팀 생성 요청
   * @return 생성된 팀 정보
   */
  @Transactional
  public TeamCreateResponse createTeam(Member creator, TeamCreateRequest request) {
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
    return teamMapper.toCreateResponse(savedTeam);
  }

  /**
   * 멤버가 팀 코드와 팀 pwd를 입력하여 팀에 가입한다.
   *
   * @param member       가입할 멤버
   * @param teamCode     팀 코드
   * @param teamPassword 팀 pwd
   * @return 가입된 팀의 정보
   * @throws IllegalArgumentException
   */
  @Transactional
  public TeamJoinResponse joinTeam(Member member, String teamCode, String teamPassword) {
    Team team = teamRepository.findByTeamCode(teamCode)
        .orElseThrow(() -> new IllegalArgumentException("에러"));
    if (!team.getTeamPassword().equals(teamPassword)) {
      throw new IllegalArgumentException("비밀번호 일치 실패.");
    }

    boolean isAlreadyJoined = teamMemberRepository.existsByTeamAndMember(team, member);
    if (isAlreadyJoined) {
      throw new IllegalArgumentException("이미 팀에 가입되어 있다.");
    }

    TeamMember teamMember = TeamMember.builder()
        .team(team)
        .member(member)
        .build();

    teamMemberRepository.save(teamMember);
    team.addTeamMember(teamMember);
    return teamMapper.toJoinResponse(team);
  }

  /**
   * 팀 코드를 생성한다
   *
   * @return 생성된 팀 코드
   */
  private String generateTeamCode() {
    return UUID.randomUUID().toString().substring(0, 8);
  }


  /**
   * 대시보드를 가져온다.
   *
   * @param teamId
   * @return
   */
  @Transactional(readOnly = true)
  public TeamDashboardResponse getTeamDashboard(Long teamId) {
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

    Budget budget = budgetRepository.findByTeamId(teamId)
        .orElseThrow(() -> new IllegalArgumentException("팀의 예산 정보가 없습니다."));

    List<Expense> expenses = expenseRepository.findAllByTeamId(teamId);

    return teamMapper.toTeamDashboardResponse(team, budget, expenses);
  }

}
