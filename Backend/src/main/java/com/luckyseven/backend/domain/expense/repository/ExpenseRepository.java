package com.luckyseven.backend.domain.expense.repository;

import com.luckyseven.backend.domain.expense.entity.Expense;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  @Query("""
        select e
          from Expense e
          left join fetch e.payer
          left join fetch e.team t
          left join fetch t.budget
         where e.id = :expenseId
      """)
  List<Expense> findExpenseWithAll(@Param("expenseId") Long expenseId);

  @Query("""
        select e
          from Expense e
          where e.team.id = :teamId
      """)
  Page<Expense> findByTeamId(@Param("teamId") Long teamId, Pageable pageable);
}
