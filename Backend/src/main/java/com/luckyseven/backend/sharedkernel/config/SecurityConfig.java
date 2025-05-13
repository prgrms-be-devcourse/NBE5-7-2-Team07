package com.luckyseven.backend.sharedkernel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  @Profile("local")
  public FakeUserFilter fakeUserFilter() {
    return new FakeUserFilter();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, FakeUserFilter fakeUserFilter) throws Exception {
    http.csrf(csrf -> csrf.disable());// API 서버의 경우 CSRF 비활성화가 일반적
    if (fakeUserFilter != null) {
      // ❷ 오직 SecurityFilterChain 안에만 한 번 삽입
      http.addFilterBefore(fakeUserFilter, SecurityContextPersistenceFilter.class
          // SecurityContext 바로 뒤
      );
    }
    http.authorizeHttpRequests(auth -> auth
        // Swagger UI 관련 경로 허용
        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
            "/swagger-resources/**", "/webjars/**").permitAll()
        // 필요한 경우 다른 공개 API 경로도 추가
        //.requestMatchers("/api/public/**").permitAll()
        // 나머지 요청은 인증 필요
        .anyRequest().authenticated()).httpBasic(Customizer.withDefaults());
    ;

    return http.build();
  }
}