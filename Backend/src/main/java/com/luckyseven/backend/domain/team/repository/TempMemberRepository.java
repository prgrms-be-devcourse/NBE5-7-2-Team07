package com.luckyseven.backend.domain.team.repository;

import com.luckyseven.backend.domain.team.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempMemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByEmail(String email);
}
