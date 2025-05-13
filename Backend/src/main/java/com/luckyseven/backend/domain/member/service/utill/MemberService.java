package com.luckyseven.backend.domain.member.service.utill;

import com.luckyseven.backend.domain.member.dto.LoginMemberRequest;
import com.luckyseven.backend.domain.member.dto.RegisterMemberRequest;
import com.luckyseven.backend.domain.member.entity.Member;
import com.luckyseven.backend.domain.member.repository.MemberRepository;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import com.luckyseven.backend.sharedkernel.jwt.entity.BlackListToken;
import com.luckyseven.backend.sharedkernel.jwt.repository.BlackListTokenRepository;
import com.luckyseven.backend.sharedkernel.jwt.utill.JwtTokenizer;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

  private final MemberRepository memberRepository;
  private final JwtTokenizer jwtTokenizer;
  private final BlackListTokenRepository blackListTokenRepository;
  private final AuthenticationManager authenticationManager;


  public void checkDuplicateNickName(String nickname) {
    if(memberRepository.findByNickname(nickname).isPresent()) {
      throw new CustomLogicException(ExceptionCode.MEMBER_NICKNAME_DUPLICATE,(Object) nickname);
    }
  }

  public void checkDuplicateEmail(String email){
    if(memberRepository.findByEmail(email).isPresent()) {
      throw new CustomLogicException(ExceptionCode.MEMBER_EMAIL_DUPLICATE,(Object) email);
    }
  }

  public void checkEqualsPassword(String password,String checkPassword){
    if(!password.equals(checkPassword)) {
      throw new CustomLogicException(ExceptionCode.MEMBER_PASSWORD_MISMATCH);
    }
  }

  public String registerMember(RegisterMemberRequest req, PasswordEncoder passwordEncoder){
    checkDuplicateEmail(req.email());
    checkDuplicateNickName(req.nickname());
    checkEqualsPassword(req.password(), req.checkPassword());

    String encodePassword = passwordEncoder.encode(req.password());
    //TODO : {Mapper} : 설정
    Member newMember = Member.builder()
        .email(req.email())
        .password(encodePassword)
        .nickname(req.nickname())
        .build();
    memberRepository.save(newMember);
    return req.email();
  }

  public void logout(
      String refreshToken,
      HttpServletResponse resp
  ){
    blackListTokenRepository.save(
        BlackListToken.builder()
            .tokenValue(refreshToken)
            .expirationTime(
                jwtTokenizer.parseRefreshToken(refreshToken).getExpiration().toInstant())
            .build()
    );

    //TODO: 올바른 삭제 방법인가?
    Cookie expired = new Cookie("refreshToken", null);
    expired.setPath("/");
    expired.setMaxAge(0);
    resp.addCookie(expired);
  }

  public String Login(LoginMemberRequest req,HttpServletResponse resp){
    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(req.email(), req.password());
    Authentication auth = authenticationManager.authenticate(token);
    CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
    return jwtTokenizer.reissueTokenPair(resp,customUserDetails);
  }



}
