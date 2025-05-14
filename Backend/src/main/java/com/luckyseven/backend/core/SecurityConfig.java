package com.luckyseven.backend.core;


import com.luckyseven.backend.sharedkernel.jwt.utill.JwtTokenizer;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final JwtTokenizer jwtTokenizer;


  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/users/**",
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/webjars/**","/refresh").permitAll()
            .anyRequest().authenticated())
        .logout(logout -> logout
            .logoutUrl("/api/users/logout")
            .invalidateHttpSession(true)
            .clearAuthentication(true)
            .deleteCookies("JSESSIONID")
            .addLogoutHandler((request, response, authentication) -> {
              // Authorization 헤더를 빈 값으로 덮어쓰기
              response.setHeader("Authorization", "");
            })
            // 2) 로그아웃 성공 응답 설정
            .logoutSuccessHandler((request, response, authentication) -> {
              response.setStatus(HttpServletResponse.SC_OK);
              // 편의상 JSON 바디로 신호 주기
              response.setContentType("application/json;charset=UTF-8");
              response.getWriter().write("{\"message\":\"로그아웃 되었습니다\"}");
            })
        )
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer,customAuthenticationEntryPoint),
            UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}

