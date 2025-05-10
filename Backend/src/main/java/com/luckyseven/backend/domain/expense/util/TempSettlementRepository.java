package com.luckyseven.backend.domain.expense.util;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempSettlementRepository extends JpaRepository<TempSettlement, Long> {

  List<TempSettlement> findByExpenseId(Long expenseId);
}
