package com.luckyseven.backend.domain.expense.util;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TempSettlementRepository extends JpaRepository<TempSettlement, Long> {

  List<TempSettlement> findByExpenseId(Long expenseId);

  @Query("""
        select s
        from TempSettlement s
          join fetch s.expense e
          join fetch e.team t
          join fetch t.budget
          join fetch s.settler m
        where e.id = :expenseId
      """)
  List<TempSettlement> findAllWithExpenseAndTeamAndMemberByExpenseId(
      @Param("expenseId") Long expenseId);
}
