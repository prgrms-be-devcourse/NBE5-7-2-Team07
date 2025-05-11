package com.luckyseven.backend.domain.team.repository;

import com.luckyseven.backend.domain.team.entity.Team;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
  Optional<Team> findByTeamCode(String teamCode);
}
