package com.luckyseven.backend.domain.settlements.app;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luckyseven.backend.domain.settlements.TempExpense;
import com.luckyseven.backend.domain.settlements.TempMember;
import com.luckyseven.backend.domain.settlements.TempTeam;
import com.luckyseven.backend.domain.settlements.dao.SettlementRepository;
import com.luckyseven.backend.domain.settlements.dto.SettlementCreateRequest;
import com.luckyseven.backend.domain.settlements.dto.SettlementResponse;
import com.luckyseven.backend.domain.settlements.dto.SettlementSearchCondition;
import com.luckyseven.backend.domain.settlements.dto.SettlementUpdateRequest;
import com.luckyseven.backend.domain.settlements.entity.Settlement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

  @Mock
  private SettlementRepository settlementRepository;

  @InjectMocks
  private SettlementService settlementService;

  private TempTeam team;
  private Settlement settlement;
  private TempMember settler;
  private TempMember payer;
  private TempExpense expense;

  @BeforeEach
  void setUp() {
    team = new TempTeam();
    settler = new TempMember(team);
    payer = new TempMember(team);
    expense = new TempExpense(team);
    settlement = Settlement.builder()
        .amount(BigDecimal.valueOf(1000))
        .settler(settler)
        .payer(payer)
        .expense(expense)
        .build();
  }

  @Test
  @DisplayName("정산생성")
  void createSettlement_ShouldSaveSettlement() {
    //given
    when(settlementRepository.save(any(Settlement.class))).thenReturn(settlement);
    SettlementCreateRequest request = new SettlementCreateRequest();
    request.setAmount(BigDecimal.valueOf(1000));
    request.setPayerId(payer.getId());
    request.setSettlerId(settler.getId());
    request.setExpenseId(expense.getId());

    //when
    SettlementResponse created = settlementService.createSettlement(request);

    //then
    assertNotNull(created);
    assertThat(created.getAmount()).isEqualTo(BigDecimal.valueOf(1000));
    assertThat(created.getSettlerId()).isEqualTo(settler.getId());
    assertThat(created.getPayerId()).isEqualTo(payer.getId());
    assertThat(created.getExpenseId()).isEqualTo(expense.getId());
    assertFalse(created.getIsSettled());
  }

  @Test
  @DisplayName("정산롼료")
  void setSettled_ShouldUpdateSettlementStatus() {
    //given
    when(settlementRepository.findById(anyLong())).thenReturn(Optional.of(settlement));
    when(settlementRepository.save(any(Settlement.class))).thenReturn(settlement);

    SettlementResponse updated = settlementService.settleSettlement(1L);

    assertTrue(updated.getIsSettled());
    verify(settlementRepository).save(settlement);
  }

  @Test
  @DisplayName("정산 조회")
  void findSettlement_ShouldReturnSettlement() {
    //given
    when(settlementRepository.findById(anyLong())).thenReturn(Optional.of(settlement));

    //when
    SettlementResponse found = settlementService.readSettlement(1L);

    //then
    assertNotNull(found);
    assertThat(found.getAmount()).isEqualTo(BigDecimal.valueOf(1000));
    assertThat(found.getSettlerId()).isEqualTo(settler.getId());
    assertThat(found.getPayerId()).isEqualTo(payer.getId());
    assertThat(found.getExpenseId()).isEqualTo(expense.getId());
  }

  @Test
  @DisplayName("정산 수정")
  void updateSettlement_ShouldUpdateSettlement() {
    //given
    when(settlementRepository.findById(anyLong())).thenReturn(Optional.of(settlement));
    when(settlementRepository.save(any(Settlement.class))).thenReturn(settlement);

    BigDecimal newAmount = BigDecimal.valueOf(2000);
    SettlementUpdateRequest request = new SettlementUpdateRequest();
    request.setAmount(newAmount);
    request.setPayerId(payer.getId());
    request.setSettlerId(settler.getId());
    request.setExpenseId(expense.getId());

    //when
    SettlementResponse updated = settlementService.updateSettlement(1L, request);

    //then
    assertNotNull(updated);
    assertThat(updated.getAmount()).isEqualTo(newAmount);
    assertThat(updated.getSettlerId()).isEqualTo(settler.getId());
    assertThat(updated.getPayerId()).isEqualTo(payer.getId());
    assertThat(updated.getExpenseId()).isEqualTo(expense.getId());
  }

  @Test
  @DisplayName("정산목록조회_페이지네이션_명세")
  void findAllSettlements_ShouldReturnAllSettlements() {
    //given
    List<Settlement> settlements = new ArrayList<>();
    settlements.add(settlement);
    Page<Settlement> mockPage = new PageImpl<>(settlements);
    when(settlementRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(mockPage);

    SettlementSearchCondition condition = new SettlementSearchCondition();
    condition.setPayerId(1L);
    condition.setSettlerId(1L);
    condition.setExpenseId(1L);
    condition.setIsSettled(false);

    //when
    Page<SettlementResponse> result = settlementService.readSettlementPage(1L, condition,
        PageRequest.of(0, 10));

    //then
    assertThat(result.getContent().size()).isEqualTo(1);
    assertThat(result.getContent()).allMatch(s -> s.getAmount().equals(BigDecimal.valueOf(1000)));
    assertThat(result.getContent()).allMatch(s -> !s.getIsSettled());
    verify(settlementRepository).findAll(any(Specification.class), any(Pageable.class));
  }

}