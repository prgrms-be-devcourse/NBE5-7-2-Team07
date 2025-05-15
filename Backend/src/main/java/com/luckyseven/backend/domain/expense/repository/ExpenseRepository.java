package com.luckyseven.backend.domain.expense.repository;

import com.luckyseven.backend.domain.expense.entity.Expense;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  Page<Expense> findByTeamId(Long teamId, Pageable pageable);

  @Query("""
        select e
          from Expense e
          join fetch e.team t
          join fetch t.budget
         where e.id = :expenseId
      """)
  Optional<Expense> findByIdWithTeamAndBudget(Long expenseId);
}
