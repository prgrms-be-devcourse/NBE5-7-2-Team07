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
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService {

  private final TeamRepository teamRepository;
  private final TeamMemberRepository teamMemberRepository;
  private final BudgetRepository budgetRepository;
  private final ExpenseRepository expenseRepository;

  private final TeamMapper teamMapper;

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
    Team team = teamMapper.toTeamEntity(request, creator, teamCode);
    creator.addLeadingTeam(team);
    Team savedTeam = teamRepository.save(team);
    TeamMember teamMember = teamMapper.toTeamMemberEntity(creator, savedTeam);

    // 리더를 TeamMember 에 추가
    teamMemberRepository.save(teamMember);

    // <TODO> 예산 생성(임시로 구현)
    Budget budget = Budget.builder()
        .currency(BigDecimal.ZERO)
        .balance(BigDecimal.ZERO)
        .foreignBalance(BigDecimal.ZERO)
        .totalAmount(BigDecimal.ZERO)
        .exchangeRate(BigDecimal.ONE)
        .avgExchangeRate(BigDecimal.ONE)
        .build();

    // Team이 Budget의 주인이므로, Team 에서 Budget set
    Budget savedBudget = budgetRepository.save(budget);
    savedTeam.setBudget(savedBudget);
    savedBudget.setTeam(savedTeam);

    savedTeam.addTeamMember(teamMember);
    return teamMapper.toTeamCreateResponse(savedTeam);
  }

  /**
   * 멤버가 팀 코드와 팀 pwd를 입력하여 팀에 가입한다.
   *
   * @param member       가입할 멤버
   * @param teamCode     팀 코드
   * @param teamPassword 팀 pwd
   * @return 가입된 팀의 정보
   * @throws IllegalArgumentException 비밀번호 일치 실패 에러.
   */
  @Transactional
  public TeamJoinResponse joinTeam(Member member, String teamCode, String teamPassword) {
    Team team = teamRepository.findByTeamCode(teamCode)
        .orElseThrow(() -> new CustomLogicException(ExceptionCode.TEAM_NOT_FOUND,
            "팀 코드가 [%s]인 팀을 찾을 수 없습니다", teamCode));

    if (!team.getTeamPassword().equals(teamPassword)) {
      throw new IllegalArgumentException("비밀번호 일치 실패.");
    }

    boolean isAlreadyJoined = teamMemberRepository.existsByTeamAndMember(team, member);
    if (isAlreadyJoined) {
      throw new CustomLogicException(ExceptionCode.ALREADY_TEAM_MEMBER,
          "회원 ID [%d]는 이미 팀 ID [%d]에 가입되어 있습니다", member.getId(), team.getId());
    }

    TeamMember teamMember = teamMapper.toTeamMemberEntity(member, team);
    TeamMember savedTeamMember = teamMemberRepository.save(teamMember);

    team.addTeamMember(savedTeamMember);
    member.addTeamMember(savedTeamMember);

    if (!savedTeamMember.getTeam().getId().equals(team.getId()) ||
        !savedTeamMember.getMember().getId().equals(member.getId())) {
      throw new CustomLogicException(ExceptionCode.INTERNAL_SERVER_ERROR,
          "팀 멤버 관계 설정에 실패했습니다");
    }

    return teamMapper.toTeamJoinResponse(team);
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
   * @param teamId 팀의 ID
   * @return 팀 대시보드
   */
  @Transactional(readOnly = true)
  public TeamDashboardResponse getTeamDashboard(Long teamId) {
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new CustomLogicException(ExceptionCode.TEAM_NOT_FOUND,
            "ID가 [%d]인 팀을 찾을 수 없습니다", teamId));

    Budget budget = budgetRepository.findByTeamId(teamId)
        .orElseThrow(() -> new CustomLogicException(ExceptionCode.BUDGET_NOT_FOUND,
            "팀 ID [%d]의 예산 정보가 없습니다", teamId));

    List<Expense> expenses = expenseRepository.findAllByTeamId(teamId);

    return teamMapper.toTeamDashboardResponse(team, budget, expenses);
  }

}
