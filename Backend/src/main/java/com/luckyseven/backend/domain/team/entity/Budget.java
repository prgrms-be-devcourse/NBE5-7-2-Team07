package com.luckyseven.backend.domain.team.entity;

import com.luckyseven.backend.sharedkernel.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
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
public class Budget extends BaseEntity {
    
    @OneToOne
    @JoinColumn(name = "team_id", unique = true)
    private Team team;
    
    private BigDecimal currency;
    private BigDecimal balance;
    private BigDecimal foreignBalance;
    private BigDecimal totalAmount;
    private BigDecimal exchangeRate;
    private BigDecimal avgExchangeRate;
}