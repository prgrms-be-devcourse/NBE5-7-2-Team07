package com.luckyseven.backend.domain.budget.dao;

import com.luckyseven.backend.domain.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

}
