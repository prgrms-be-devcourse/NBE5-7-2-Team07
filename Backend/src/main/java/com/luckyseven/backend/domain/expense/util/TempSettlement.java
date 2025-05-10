package com.luckyseven.backend.domain.expense.util;


import com.luckyseven.backend.domain.expense.entity.Expense;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
    indexes = {
        @Index(name = "idx_settlement_expense_id", columnList = "expense_id")
    }
)
public class TempSettlement {

  @Id
  private Long id;

  @ManyToOne
  private Expense expense;

  @ManyToOne
  private TempMember settler;
}
