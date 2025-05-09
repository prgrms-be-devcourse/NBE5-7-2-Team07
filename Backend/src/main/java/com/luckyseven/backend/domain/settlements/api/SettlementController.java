package com.luckyseven.backend.domain.settlements.api;

import com.luckyseven.backend.domain.settlements.app.SettlementService;
import com.luckyseven.backend.domain.settlements.dto.SettlementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SettlementController {

  private final SettlementService settlementService;

  @GetMapping("/{settlementId}")
  public ResponseEntity<SettlementResponse> readSettlement(@PathVariable Long settlementId) {
    return ResponseEntity.ok(settlementService.readSettlement(settlementId));
  }
}
