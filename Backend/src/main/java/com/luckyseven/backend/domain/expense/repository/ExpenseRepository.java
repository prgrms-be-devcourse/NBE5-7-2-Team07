package com.luckyseven.backend.domain.expense.repository;

import com.luckyseven.backend.domain.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

}
