package com.luckyseven.backend.domain.expense.util;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TempMember {

  @Id
  private Long id;
  private String nickname;

}
