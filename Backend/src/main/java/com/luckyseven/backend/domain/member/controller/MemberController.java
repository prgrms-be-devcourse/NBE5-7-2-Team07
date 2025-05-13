package com.luckyseven.backend.domain.member.controller;

import com.luckyseven.backend.domain.member.service.utill.CustomUserDetails;
import com.luckyseven.backend.domain.member.dto.LoginMemberRequest;
import com.luckyseven.backend.domain.member.dto.RegisterMemberRequest;
import com.luckyseven.backend.domain.member.service.CustomMemberDetailsService;
import com.luckyseven.backend.sharedkernel.jwt.utill.JwtTokenizer;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/users"})
public class MemberController implements MemberApi {
  private final CustomMemberDetailsService  customMemberDetailsService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenizer jwtTokenizer;


  @Override
  @PostMapping("/register")
  public ResponseEntity<String> registerMember(@RequestBody RegisterMemberRequest req) {
    customMemberDetailsService.registerMember(req,passwordEncoder);
    return ResponseEntity.status(HttpStatus.CREATED).body("success");
  }

  //TODO <추후 고려사항> : 사용자가 중간에 이메일을 바꿀경우 boolean형으로?
  @Override
  @PostMapping("/checkEmail")
  public ResponseEntity<Void> checkEmail(@RequestParam String email) {
    customMemberDetailsService.checkDuplicateEmail(email);
    return ResponseEntity.noContent().build();
  }

  //TODO <추후 고려사항> : 사용자가 중간에 닉네임를 바꿀경우 boolean형으로?
  @Override
  @PostMapping("/checkNickname")
  public ResponseEntity<Void> checkNickName(@RequestParam String nickname) {
    customMemberDetailsService.checkDuplicateNickName(nickname);
    return ResponseEntity.noContent().build();
  }

  //TODO <추후 고려사항> : 사용자가 중간에 비밀번호를 바꿀경우 boolean형으로?
  @Override
  @PostMapping("/checkPassword")
  public ResponseEntity<Void> checkPassword(@RequestParam String password,@RequestParam String checkPassword) {
    customMemberDetailsService.checkEqualsPassword(password,checkPassword);
    return ResponseEntity.noContent().build();
  }


  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@CookieValue("refreshToken") String refreshToken, HttpServletResponse resp) {
    customMemberDetailsService.logout(refreshToken,resp);
    return ResponseEntity.noContent().build();
  }


  @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginMemberRequest req,
        HttpServletResponse resp){
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(req.email(), req.password());
      Authentication auth = authenticationManager.authenticate(authToken);

      CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
      String accessToken = jwtTokenizer.reissueTokenPair(resp,customUserDetails);

      log.info("로그인 성공 , userDetails.id = {} , userDetails.getEmail = {}",customUserDetails.getId(),customUserDetails.getEmail());
      log.info("AccessToken : {}", accessToken);

      return ResponseEntity.ok().header("Authorization","Bearer "+accessToken).build();
    }


}
