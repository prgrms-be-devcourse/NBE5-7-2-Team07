package com.luckyseven.backend.domain.team.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luckyseven.backend.sharedkernel.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
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
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@AttributeOverride(name = "id", column = @Column(name = "member_id"))
public class Member extends BaseEntity {

  private String name;

  private String email;

  private String password;

  private String nickname;


  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @JsonIgnore
  @Schema(hidden = true)
  private List<TeamMember> teamMembers = new ArrayList<>();


  @OneToMany(mappedBy = "leader")
  @Builder.Default
  @JsonIgnore
  @Schema(hidden = true)
  private List<Team> leadingTeams = new ArrayList<>();

  // 양방향 연관관계 유지를 위한 메서드 추가
  public void addLeadingTeam(Team team) {
    this.leadingTeams.add(team);
    if (team.getLeader() != this) {
      team.setLeader(this);
    }
  }

  public void removeLeadingTeam(Team team) {
    this.leadingTeams.remove(team);
    if (team.getLeader() == this) {
      team.setLeader(null);
    }
  }
}
