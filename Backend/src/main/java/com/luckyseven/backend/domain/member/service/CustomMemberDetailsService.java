package com.luckyseven.backend.domain.member.service;

import com.luckyseven.backend.domain.member.service.utill.memberDetails;
import com.luckyseven.backend.domain.member.entity.Member;
import com.luckyseven.backend.domain.member.repository.MemberRepository;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomMemberDetailsService implements UserDetailsService {

  private final MemberRepository memberRepository;


  @Override
  public memberDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new CustomLogicException(ExceptionCode.MEMBER_EMAIL_NOTFOUND,email));
    return new memberDetails(member);
  }
  public memberDetails loadUserById(Long id){
    Member member = memberRepository.findById(id)
        .orElseThrow(() -> new CustomLogicException(ExceptionCode.MEMBER_ID_NOTFOUND,id));
    return new memberDetails(member);
  }

}
