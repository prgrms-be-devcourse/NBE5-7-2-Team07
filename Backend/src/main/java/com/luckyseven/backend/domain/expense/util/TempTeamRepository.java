package com.luckyseven.backend.domain.expense.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TempTeamRepository extends JpaRepository<TempTeam, Long> {

  @Query("select t from TempTeam t join fetch t.budget where t.id = :teamId")
  TempTeam findTeamWithBudget(@Param("teamId") Long teamId);
}
