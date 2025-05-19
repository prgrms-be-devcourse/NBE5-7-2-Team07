package com.luckyseven.backend.domain.settlements.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.luckyseven.backend.domain.expense.entity.Expense;
import com.luckyseven.backend.domain.member.entity.Member;
import com.luckyseven.backend.domain.settlements.entity.Settlement;
import com.luckyseven.backend.domain.team.entity.Team;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
class SettlementRepositoryTest {

  @Autowired
  private SettlementRepository settlementRepository;

  @PersistenceContext
  private EntityManager entityManager;

  Team team1;
  Team team2;
  Member settler1;
  Member settler2;
  Member payer1;
  Member payer2;
  Expense expense1;
  Expense expense2;

  @BeforeEach
  void setUp() {
    team1 = new Team();
    team2 = new Team();

    // 먼저 팀 엔티티 저장
    entityManager.persist(team1);
    entityManager.persist(team2);

    settler1 = Member.builder().email("123@123").build();
    settler2 = Member.builder().email("123@123").build();
    payer1 = Member.builder().email("123@123").build();
    payer2 = Member.builder().email("123@123").build();

    // 멤버 엔티티 저장
    entityManager.persist(settler1);
    entityManager.persist(settler2);
    entityManager.persist(payer1);
    entityManager.persist(payer2);

    expense1 = Expense.builder().amount(BigDecimal.valueOf(1000)).build();
    expense2 = Expense.builder().amount(BigDecimal.valueOf(1000)).build();

    // 비용 엔티티 저장
    entityManager.persist(expense1);
    entityManager.persist(expense2);

    // 변경사항 반영
    entityManager.flush();

    for (int i = 0; i < 20; i++) {
      Settlement settlement = Settlement.builder()
          .amount(BigDecimal.valueOf(1000))
          .settler(i < 5 ? settler1 : settler2)
          .payer(i < 10 ? payer1 : payer2)
          .expense(i < 15 ? expense1 : expense2)
          .build();
      if (i % 2 == 0) {
        settlement.setSettled();
      }
      settlementRepository.save(settlement);
    }
  }

  @Test
  @DisplayName("팀_명세")
  void findAllWithTeamSpecification() {
    // given
    Specification<Settlement> team1Spec = Specification.where(
        SettlementSpecification.hasTeamId(team1.getId()));
    PageRequest pageRequest = PageRequest.of(0, 10);

    // when
    Page<Settlement> result = settlementRepository.findAll(team1Spec, pageRequest);

    // then
    assertThat(result.getContent()).hasSize(10);
    assertThat(result.getTotalElements()).isEqualTo(15);
    assertThat(result.getContent()).allMatch(s -> s.getExpense().getTeam().equals(team1));
  }

  @Test
  @DisplayName("정산_명세")
  void findAllWithExpenseSpecification() {
    // given
    Specification<Settlement> team1Spec = Specification.where(
        SettlementSpecification.hasExpenseId(expense1.getId()));
    PageRequest pageRequest = PageRequest.of(0, 10);

    // when
    Page<Settlement> result = settlementRepository.findAll(team1Spec, pageRequest);

    // then
    assertThat(result.getContent()).hasSize(10);
    assertThat(result.getTotalElements()).isEqualTo(15);
    assertThat(result.getContent()).allMatch(s -> s.getExpense().equals(expense1));
  }

  @Test
  @DisplayName("정산완료여부_명세")
  void findAllWithSettledSpecification() {
    // given
    Specification<Settlement> settledSpec = Specification.where(
        SettlementSpecification.isSettled(true));
    PageRequest pageRequest = PageRequest.of(0, 10);

    // when
    Page<Settlement> result = settlementRepository.findAll(settledSpec, pageRequest);

    // then
    assertThat(result.getContent()).hasSize(10);
    assertThat(result.getTotalElements()).isEqualTo(10);
    assertThat(result.getContent()).allMatch(Settlement::getIsSettled);
  }

  @Test
  @DisplayName("지불자_명세")
  void findAllWithPayerSpecification() {
    // given
    Specification<Settlement> payerSpec = Specification.where(
        SettlementSpecification.hasPayerId(payer1.getId()));
    PageRequest pageRequest = PageRequest.of(0, 10);

    // when
    Page<Settlement> result = settlementRepository.findAll(payerSpec, pageRequest);

    // then
    assertThat(result.getContent()).hasSize(10);
    assertThat(result.getTotalElements()).isEqualTo(10);
    assertThat(result.getContent()).allMatch(s -> s.getPayer().equals(payer1));
  }

  @Test
  @DisplayName("정산자_명세")
  void findAllWithSettlerSpecification() {
    // given
    Specification<Settlement> payerSpec = Specification.where(
        SettlementSpecification.hasSettlerId(settler1.getId()));
    PageRequest pageRequest = PageRequest.of(0, 10);

    // when
    Page<Settlement> result = settlementRepository.findAll(payerSpec, pageRequest);

    // then
    assertThat(result.getContent()).hasSize(5);
    assertThat(result.getTotalElements()).isEqualTo(5);
    assertThat(result.getContent()).allMatch(s -> s.getSettler().equals(settler1));
  }
}