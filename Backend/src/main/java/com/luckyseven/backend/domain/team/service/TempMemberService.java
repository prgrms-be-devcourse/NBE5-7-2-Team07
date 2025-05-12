package com.luckyseven.backend.domain.team.service;

import com.luckyseven.backend.domain.team.entity.Member;
import com.luckyseven.backend.domain.team.repository.TempMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TempMemberService {

  private final TempMemberRepository tempMemberRepository;

  public Member getCurrentMember() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Long memberId;

    if(authentication.getPrincipal() instanceof Long){
      memberId = (Long) authentication.getPrincipal();
    }else {
      memberId = Long.parseLong(authentication.getName());
    }

    return tempMemberRepository.findById(memberId)
        .orElseThrow(() -> new IllegalArgumentException(""));
  }
}