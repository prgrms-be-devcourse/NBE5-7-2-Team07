package com.luckyseven.backend.domain.member.service;

import com.luckyseven.backend.domain.member.service.utill.CustomUserDetails;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomMemberDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;
  private final JwtTokenizer jwtTokenizer;
  private final BlackListTokenRepository blackListTokenRepository;

  public void checkDuplicateNickName(String nickName){
    log.info("[CustomMemberDetailsService] checkDuplicateNickName 호출, email = {}", nickName);
    if(memberRepository.findByNickname(nickName).isPresent()){
      throw new CustomLogicException(ExceptionCode.MEMBER_NICKNAME_DUPLICATE,(Object) nickName);
    }
  }

  public void checkDuplicateEmail(String email){
    log.info("[CustomMemberDetailsService] checkDuplicateEmail 호출, email = {}", email);
    if(memberRepository.findByEmail(email).isPresent()){
      throw new CustomLogicException(ExceptionCode.MEMBER_EMAIL_DUPLICATE,(Object) email);
    }
  }

  public void checkEqualsPassword(String password ,String checkPassword){
    log.info("[CustomMemberDetailsService] checkEqualsPassword 호출, password = {} , checkpassword = {}", password, checkPassword);
    if(!password.equals(checkPassword)){
      throw new CustomLogicException(ExceptionCode.MEMBER_PASSWORD_MISMATCH);
    }
  }

  public void registerMember(RegisterMemberRequest req, PasswordEncoder passwordEncoder){
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
  }

  public void logout(
      String refreshToken,
      HttpServletResponse resp) {


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



  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomLogicException(ExceptionCode.MEMBER_EMAIL_NOTFOUND,email));
    return new CustomUserDetails(member);
  }
  public CustomUserDetails loadUserById(Long id){
    Member member = memberRepository.findById(id)
        .orElseThrow(() -> new CustomLogicException(ExceptionCode.MEMBER_ID_NOTFOUND,id));
    return new CustomUserDetails(member);
  }

}
