package com.luckyseven.backend.domain.member.repository;

import com.luckyseven.backend.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByNickname(String nickname);

  Optional<Member> findByEmail(String email);


}
