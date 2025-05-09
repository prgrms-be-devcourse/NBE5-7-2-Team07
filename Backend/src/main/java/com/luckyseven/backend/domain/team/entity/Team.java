package com.luckyseven.backend.domain.team.entity;

import com.luckyseven.backend.sharedkernel.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
public class Team extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String teamCode;

  @Column(name = "leader_id")
  private Long leaderId;

  @Column(name = "budget_id")
  private Long budgetId;

  @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TeamMember> teamMembers = new ArrayList<>();
}
