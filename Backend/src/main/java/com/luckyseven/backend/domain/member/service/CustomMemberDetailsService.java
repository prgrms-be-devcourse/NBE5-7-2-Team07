package com.luckyseven.backend.domain.member.service;

import com.luckyseven.backend.domain.member.dto.LoginMemberRequest;
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
  

  @Override
  public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
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
