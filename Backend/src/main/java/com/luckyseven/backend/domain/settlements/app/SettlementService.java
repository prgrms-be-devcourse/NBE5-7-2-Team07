package com.luckyseven.backend.domain.settlements.app;

import com.luckyseven.backend.domain.settlements.dao.SettlementRepository;
import com.luckyseven.backend.domain.settlements.dto.SettlementResponse;
import com.luckyseven.backend.domain.settlements.entity.Settlement;
import com.luckyseven.backend.domain.settlements.util.SettlementMapper;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementService {

  private final SettlementRepository settlementRepository;

  @Transactional
  public void createSettlement() {

  }

  @Transactional(readOnly = true)
  public SettlementResponse readSettlement(Long id) {
    Settlement settlement = settlementRepository.findById(id).orElseThrow(
        () -> new CustomLogicException(ExceptionCode.SETTLEMENT_NOT_FOUND)
    );
    return SettlementMapper.toSettlementResponse(settlement);
  }
}
