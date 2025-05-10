package com.luckyseven.backend.domain.expense.util;

import com.luckyseven.backend.sharedkernel.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TempMember extends BaseEntity {

  @Id
  private String email;
  

}
