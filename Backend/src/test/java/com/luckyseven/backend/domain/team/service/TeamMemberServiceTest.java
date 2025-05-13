package com.luckyseven.backend.domain.team.service;

import com.luckyseven.backend.domain.team.dto.TeamMemberDto;
import com.luckyseven.backend.domain.team.entity.Member;
import com.luckyseven.backend.domain.team.entity.Team;
import com.luckyseven.backend.domain.team.entity.TeamMember;
import com.luckyseven.backend.domain.team.repository.TeamMemberRepository;
import com.luckyseven.backend.domain.team.repository.TeamRepository;
import com.luckyseven.backend.domain.team.util.TeamMemberMapper;
import com.luckyseven.backend.domain.team.util.TestEntityBuilder;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

  @Mock
  private TeamRepository teamRepository;

  @Mock
  private TeamMemberRepository teamMemberRepository;

  @Mock
  private TeamMemberMapper teamMemberMapper;

  @InjectMocks
  private TeamMemberService teamMemberService;

  /**
   * 팀 ID로 팀 멤버 목록을 조회하는 기능 테스트 - 팀이 존재할 경우 TeamMemberDto 리스트를 반환하는지 확인 - DTO 변환이 제대로 이루어지는지 검증
   */
  @Test
  void getTeamMemberByTeamId_팀이존재하면_멤버목록반환() {
    // 준비
    Long teamId = 1L;
    Long memberId = 2L;
    Long teamMemberId = 101L;

    // 팀, 멤버, 팀멤버 객체 생성 (테스트 빌더 활용)
    Team team = TestEntityBuilder.createTeamWithId(teamId, "테스트 팀", "TEST-001", "pass123");
    Member member = TestEntityBuilder.createMemberWithId(memberId, "test@example.com", "홍길동");
    TeamMember teamMember = TestEntityBuilder.createTeamMemberWithId(teamMemberId, team, member);

    List<TeamMember> teamMembers = Arrays.asList(teamMember);

    // DTO 설정
    TeamMemberDto dto = TeamMemberDto.builder()
        .id(teamMemberId)
        .teamId(teamId)
        .teamName("테스트 팀")
        .build();

    List<TeamMemberDto> expectedDtos = Arrays.asList(dto);

    // Mock 설정
    when(teamRepository.existsById(teamId)).thenReturn(true);
    when(teamMemberRepository.findByTeamId(teamId)).thenReturn(teamMembers);
    when(teamMemberMapper.toDtoList(teamMembers)).thenReturn(expectedDtos);

    // 실행
    List<TeamMemberDto> result = teamMemberService.getTeamMemberByTeamId(teamId);

    // 검증
    assertEquals(1, result.size());
    assertEquals(expectedDtos, result);
    verify(teamRepository).existsById(teamId);
    verify(teamMemberRepository).findByTeamId(teamId);
  }

  /**
   * 팀 ID로 팀 멤버 목록을 조회할 때 팀이 존재하지 않는 경우 테스트 - 예외가 발생하는지 확인
   */
  @Test
  void getTeamMemberByTeamId_팀이없으면_예외발생() {
    // 준비
    Long teamId = 1L;
    when(teamRepository.existsById(teamId)).thenReturn(false);

    // 실행 및 검증
    assertThrows(CustomLogicException.class, () -> {
      teamMemberService.getTeamMemberByTeamId(teamId);
    });

    verify(teamRepository).existsById(teamId);
    verifyNoInteractions(teamMemberMapper);
  }

  /**
   * 팀 멤버 삭제 기능 테스트 - 유효한 요청일 경우 멤버가 삭제되는지 확인
   */
  @Test
  void removeTeamMember_유효한요청이면_멤버삭제() {
    // 준비
    Long teamId = 1L;
    Long memberId = 2L;
    Long teamMemberId = 101L;

    // 테스트 빌더로 객체 생성
    Team team = TestEntityBuilder.createTeamWithId(teamId, "테스트 팀", "TEST-001", "pass123");
    Member member = TestEntityBuilder.createMemberWithId(memberId, "test@example.com", "홍길동");
    TeamMember teamMember = TestEntityBuilder.createTeamMemberWithId(teamMemberId, team, member);

    when(teamRepository.existsById(teamId)).thenReturn(true);
    when(teamMemberRepository.findById(teamMemberId)).thenReturn(Optional.of(teamMember));

    // 실행
    teamMemberService.removeTeamMember(teamId, teamMemberId);

    // 검증
    verify(teamRepository).existsById(teamId);
    verify(teamMemberRepository).findById(teamMemberId);
    verify(teamMemberRepository).deleteById(teamMemberId);
  }

  /**
   * 팀 멤버 삭제 시 팀이 존재하지 않는 경우 테스트 - 예외가 발생하는지 확인
   */
  @Test
  void removeTeamMember_팀이없으면_예외발생() {
    // 준비
    Long teamId = 1L;
    Long teamMemberId = 101L;
    when(teamRepository.existsById(teamId)).thenReturn(false);

    // 실행 및 검증
    assertThrows(CustomLogicException.class, () -> {
      teamMemberService.removeTeamMember(teamId, teamMemberId);
    });

    verify(teamRepository).existsById(teamId);
    verifyNoInteractions(teamMemberRepository);
  }

  /**
   * 팀 멤버 삭제 시 해당 팀멤버가 존재하지 않는 경우 테스트 - 예외가 발생하는지 확인
   */
  @Test
  void removeTeamMember_멤버가없으면_예외발생() {
    // 준비
    Long teamId = 1L;
    Long teamMemberId = 101L;
    when(teamRepository.existsById(teamId)).thenReturn(true);
    when(teamMemberRepository.findById(teamMemberId)).thenReturn(Optional.empty());

    // 실행 및 검증
    assertThrows(CustomLogicException.class, () -> {
      teamMemberService.removeTeamMember(teamId, teamMemberId);
    });

    verify(teamRepository).existsById(teamId);
    verify(teamMemberRepository).findById(teamMemberId);
  }

  /**
   * 팀 멤버 삭제 시 해당 멤버가 다른 팀에 속한 경우 테스트 - 예외가 발생하는지 확인
   */
  @Test
  void removeTeamMember_다른팀멤버면_예외발생() {
    // 준비
    Long teamId = 1L;
    Long differentTeamId = 2L;
    Long memberId = 3L;
    Long teamMemberId = 101L;

    // 다른 팀 테스트 객체 생성
    Team differentTeam = TestEntityBuilder.createTeamWithId(differentTeamId, "다른 팀", "OTHER-001",
        "pass123");
    Member member = TestEntityBuilder.createMemberWithId(memberId, "test@example.com", "홍길동");
    TeamMember teamMember = TestEntityBuilder.createTeamMemberWithId(teamMemberId, differentTeam,
        member);

    when(teamRepository.existsById(teamId)).thenReturn(true);
    when(teamMemberRepository.findById(teamMemberId)).thenReturn(Optional.of(teamMember));

    // 실행 및 검증
    assertThrows(CustomLogicException.class, () -> {
      teamMemberService.removeTeamMember(teamId, teamMemberId);
    });

    verify(teamRepository).existsById(teamId);
    verify(teamMemberRepository).findById(teamMemberId);
  }
}