package com.luckyseven.backend.domain.expense.util;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TempTeamRepository extends JpaRepository<TempTeam, Long> {

  // TODO: 실제 레포지토리에 추가
  @Query("select t from TempTeam t join fetch t.budget where t.id = :teamId")
  Optional<TempTeam> findTeamWithBudget(@Param("teamId") Long teamId);
}
