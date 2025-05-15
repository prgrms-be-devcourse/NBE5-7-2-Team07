package com.luckyseven.backend.domain.team.service;

import com.luckyseven.backend.domain.member.entity.Member;
import com.luckyseven.backend.domain.team.repository.TempMemberRepository;
import com.luckyseven.backend.domain.team.util.TestEntityBuilder;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class TempMemberServiceTest {

  @Mock
  private TempMemberRepository tempMemberRepository;

  @Mock
  private Authentication authentication;

  @Mock
  private SecurityContext securityContext;

  @InjectMocks
  private TempMemberService tempMemberService;

  @BeforeEach
  void setUp() {
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void Long타입_id로_멤버_받으면_리턴() {
    // Given
    Long memberId = 1L;
    String email = "test@example.com";
    String name = "테스트 사용자";

    // TestEntityBuilder를 사용하여 Member 객체 생성
    Member expectedMember = TestEntityBuilder.createMemberWithId(memberId, email, name);

    when(authentication.getPrincipal()).thenReturn(memberId);
    when(tempMemberRepository.findById(memberId)).thenReturn(Optional.of(expectedMember));

    // When
    Member result = tempMemberService.getCurrentMember();

    // Then
    assertEquals(expectedMember, result);
    verify(tempMemberRepository).findById(memberId);
  }


  @Test
  void getCurrentMember_멤버가_없다면_exception을_던진다() {
    // Given
    Long memberId = 1L;
    when(authentication.getPrincipal()).thenReturn(memberId);
    when(tempMemberRepository.findById(memberId)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(CustomLogicException.class, () -> tempMemberService.getCurrentMember());
    verify(tempMemberRepository).findById(memberId);
  }
}