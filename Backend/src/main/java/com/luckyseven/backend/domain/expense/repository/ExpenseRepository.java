package com.luckyseven.backend.domain.expense.repository;

import com.luckyseven.backend.domain.expense.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  Page<Expense> findByTeamId(Long teamId, Pageable pageable);
}
