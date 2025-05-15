package com.luckyseven.backend.domain.settlements.app;

import com.luckyseven.backend.domain.expense.entity.Expense;
import com.luckyseven.backend.domain.expense.service.ExpenseService;
import com.luckyseven.backend.domain.member.entity.Member;
import com.luckyseven.backend.domain.member.service.MemberService;
import com.luckyseven.backend.domain.settlements.dao.SettlementRepository;
import com.luckyseven.backend.domain.settlements.dao.SettlementSpecification;
import com.luckyseven.backend.domain.settlements.dto.SettlementCreateRequest;
import com.luckyseven.backend.domain.settlements.dto.SettlementResponse;
import com.luckyseven.backend.domain.settlements.dto.SettlementSearchCondition;
import com.luckyseven.backend.domain.settlements.dto.SettlementUpdateRequest;
import com.luckyseven.backend.domain.settlements.entity.Settlement;
import com.luckyseven.backend.domain.settlements.util.SettlementMapper;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementService {

  private final SettlementRepository settlementRepository;
  private final MemberService memberService;
  private final ExpenseService expenseService;

  @Transactional
  public SettlementResponse createSettlement(SettlementCreateRequest request) {
    Member settler = memberService.findMemberOrThrow(request.getSettlerId());
    Member payer = memberService.findMemberOrThrow(request.getPayerId());
    Expense expense = expenseService.findExpenseOrThrow(request.getExpenseId());
    Settlement settlement = SettlementMapper.fromSettlementCreateRequest(request, settler, payer,
        expense);
    return SettlementMapper.toSettlementResponse(settlementRepository.save(settlement));
  }

  @Transactional(readOnly = true)
  public SettlementResponse readSettlement(Long id) {
    Settlement settlement = settlementRepository.findWithSettlerAndPayerById(id).orElseThrow(
        () -> new CustomLogicException(ExceptionCode.SETTLEMENT_NOT_FOUND)
    );
    return SettlementMapper.toSettlementResponse(settlement);
  }


  /**
   * 검색 조건에 따른 정산 목록을 페이지네이션하여 조회합니다. teamId는 필수 condition 각 속성은 선택
   *
   * @param teamId    팀 ID (필터링 조건)
   * @param condition 지불자 ID, 정산자 ID, 지출 ID, 정산 상태를 포함하는 검색 조건
   * @param pageable  페이지네이션 정보
   * @return 검색 조건에 맞는 정산 응답 객체들이 담긴 Page 객체
   * @throws CustomLogicException teamId가 null인 경우 (BAD_REQUEST)
   */
  @Transactional(readOnly = true)
  public Page<SettlementResponse> readSettlementPage(Long teamId,
      SettlementSearchCondition condition, Pageable pageable) {
    if (teamId == null) {
      throw new CustomLogicException(ExceptionCode.BAD_REQUEST);
    }

    Specification<Settlement> specification = Specification
        .where(SettlementSpecification.hasTeamId(teamId))
        .and(SettlementSpecification.hasPayerId(condition.getPayerId()))
        .and(SettlementSpecification.hasSettlerId(condition.getSettlerId()))
        .and(SettlementSpecification.hasExpenseId(condition.getExpenseId()))
        .and(SettlementSpecification.isSettled(condition.getIsSettled()));
    Page<Settlement> settlementPage = settlementRepository.findAll(specification, pageable);

    return settlementPage.map(SettlementMapper::toSettlementResponse);
  }

  @Transactional
  public SettlementResponse updateSettlement(Long id, SettlementUpdateRequest request) {

    Settlement settlement = settlementRepository.findById(id).orElseThrow(
        () -> new CustomLogicException(ExceptionCode.SETTLEMENT_NOT_FOUND)
    );
    Member settler = request.getSettlerId() != null ?
        memberService.findMemberOrThrow(request.getSettlerId()) : null;
    Member payer = request.getPayerId() != null ?
        memberService.findMemberOrThrow(request.getPayerId()) : null;
    Expense expense = request.getExpenseId() != null ?
        expenseService.findExpenseOrThrow(request.getExpenseId()) : null;

    settlement.update(request.getAmount(), settler, payer, expense, request.getIsSettled());
    return SettlementMapper.toSettlementResponse(settlementRepository.save(settlement));
  }

  @Transactional
  public SettlementResponse settleSettlement(Long id) {
    Settlement settlement = settlementRepository.findById(id).orElseThrow(
        () -> new CustomLogicException(ExceptionCode.SETTLEMENT_NOT_FOUND)
    );
    settlement.setSettled();
    return SettlementMapper.toSettlementResponse(settlementRepository.save(settlement));
  }
}
