package com.luckyseven.backend.domain.team.repository;

import com.luckyseven.backend.domain.team.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByTeamId(Long teamId);
}