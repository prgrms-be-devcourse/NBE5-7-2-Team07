package com.luckyseven.backend.domain.team.repository;

import com.luckyseven.backend.domain.team.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByTeamId(Long teamId);
}