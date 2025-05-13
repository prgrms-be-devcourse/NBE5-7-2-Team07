package com.luckyseven.backend.domain.team.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luckyseven.backend.sharedkernel.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "team", indexes = {
    @Index(name = "idx_team_leader_id", columnList = "leader_id")
})
@AttributeOverride(name = "id", column = @Column(name = "team_id"))
public class Team extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String teamCode;

  @Column(nullable = false)
  private String teamPassword;

  /**
   * 팀장 ID
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "leader_id", nullable = false)
  private Member leader;

  /**
   * 팀의 예산 정보
   */
  @OneToOne(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
  private Budget budget;


  @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @JsonIgnore
  @Schema(hidden = true)
  private List<TeamMember> teamMembers = new ArrayList<>();

  public void addTeamMember(TeamMember teamMember) {
    this.teamMembers.add(teamMember);
    teamMember.setTeam(this);
  }

  public void removeTeamMember(TeamMember teamMember) {
    this.teamMembers.remove(teamMember);
    teamMember.setTeam(null);
  }

}
