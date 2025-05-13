package com.luckyseven.backend.sharedkernel.jwt.Controller;

import com.luckyseven.backend.domain.member.service.CustomMemberDetailsService;
import com.luckyseven.backend.domain.member.service.utill.CustomUserDetails;
import com.luckyseven.backend.sharedkernel.jwt.utill.JwtTokenizer;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RefreshTokenController {
  private final JwtTokenizer jwtTokenizer;
  private final CustomMemberDetailsService customMemberDetailsService;
  @PostMapping("/refresh")
  public ResponseEntity<Void> refreshToken(@CookieValue(name = "refreshToken") String token,HttpServletResponse response) {
    Long memberId = jwtTokenizer.Long_validateRefreshToken(token,response);
    CustomUserDetails userDetails = customMemberDetailsService.loadUserById(memberId);
    String accessToken = jwtTokenizer.reissueTokenPair(response,userDetails);
    return ResponseEntity.ok().header("Authorization", "Bearer " + accessToken).build();
  }
}
