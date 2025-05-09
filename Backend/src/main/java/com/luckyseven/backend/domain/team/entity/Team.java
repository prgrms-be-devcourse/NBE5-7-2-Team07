package com.luckyseven.backend.domain.team.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "team", indexes = {
    @Index(name = "idx_team_leader_id", columnList = "leader_id"),
    @Index(name = "idx_team_budget_id", columnList = "budget_id")
})
public class Team {

  @Id
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String teamCode;

  @Column(name = "leader_id")
  private Long leaderId;

  @Column(name = "budget_id")
  private Long budgetId;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}

