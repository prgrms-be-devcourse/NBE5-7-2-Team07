package com.luckyseven.backend.domain.expense.util;

import com.luckyseven.backend.sharedkernel.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TempTeam extends BaseEntity {

  @Id
  private Long id;

  @OneToOne
  private TempBudget budget;
}
