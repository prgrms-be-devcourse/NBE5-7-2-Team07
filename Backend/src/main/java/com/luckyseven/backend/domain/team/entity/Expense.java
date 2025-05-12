package com.luckyseven.backend.domain.team.entity;

import com.luckyseven.backend.sharedkernel.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Expense extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private Long payerId;

    private String category;
    private Integer amount;
}