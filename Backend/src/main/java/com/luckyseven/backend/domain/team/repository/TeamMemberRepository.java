package com.luckyseven.backend.domain.team.repository;

import com.luckyseven.backend.domain.team.entity.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

}
