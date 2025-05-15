package com.luckyseven.backend.sharedkernel.config;

import com.luckyseven.backend.domain.team.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


@Profile("local")   // 로컬 테스트용 프로파일에서만 활성화
public class FakeUserFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
  ) throws ServletException, IOException {
    System.out.println(">>> FakeUserFilter applied!");
    // 1) 가짜 Member 엔티티 생성 (필요한 최소 정보만 채워두세요)
    Member fake = Member.builder()
        .id(1L)
        .name("swagger-test")
        .build();

    // 2) Authentication 토큰 생성
    UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(
            fake,
            null,
            List.of()   // ROLE_USER 등 필요하면 권한 추가
        );

    // 3) SecurityContext 에 심기
    SecurityContextHolder.getContext().setAuthentication(auth);

    // 4) 다음 필터 진행
    filterChain.doFilter(request, response);
  }
}