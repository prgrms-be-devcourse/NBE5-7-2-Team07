package com.luckyseven.backend.domain.expense.repository;

import com.luckyseven.backend.domain.expense.dto.ExpenseResponse;
import com.luckyseven.backend.domain.expense.entity.Expense;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  @EntityGraph(attributePaths = {"payer"})
  Page<Expense> findByTeamId(Long teamId, Pageable pageable);

  // TODO: 쿼리 최적화
  @EntityGraph(attributePaths = "payer")
  @Query(
      value = """
            select new com.luckyseven.backend.domain.expense.dto.ExpenseResponse(
              e.id,
              e.description,
              e.amount,
              e.category,
              p.id,
              p.nickname,
              e.createdAt,
              e.updatedAt,
              e.paymentMethod
            )
            from Expense e
            join e.payer p
            where e.team.id = :teamId
          """,
      countQuery = "select count(e) from Expense e where e.team.id = :teamId"
  )
  Page<ExpenseResponse> findResponsesByTeamId(Long teamId, Pageable pageable);


  @Query("""
        select e from Expense e
         join fetch e.payer p
         where e.id = :expenseId
      """)
  Optional<Expense> findByIdWithPayer(Long expenseId);


  @Query("""
         select e from Expense e
          join fetch e.team t
          join fetch t.budget
          where e.id = :expenseId
      """)
  Optional<Expense> findWithTeamAndBudgetById(Long expenseId);

  @Query("""
      select e.category as category, sum(e.amount) as totalAmount from Expense e
            where e.team.id = :teamId
                  GROUP BY e.category
      """)
  Optional<List<CategoryExpenseSum>> findCategoryExpenseSumsByTeamId(Long teamId);
}
