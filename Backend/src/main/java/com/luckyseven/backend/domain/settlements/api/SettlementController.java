package com.luckyseven.backend.domain.settlements.api;

import com.luckyseven.backend.domain.settlements.app.SettlementService;
import com.luckyseven.backend.domain.settlements.dto.SettlementResponse;
import com.luckyseven.backend.domain.settlements.dto.SettlementUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SettlementController {

  private final SettlementService settlementService;

  @GetMapping("/settlements/{settlementId}")
  public ResponseEntity<SettlementResponse> readSettlement(@PathVariable Long settlementId) {
    return ResponseEntity.ok(settlementService.readSettlement(settlementId));
  }

  @GetMapping("/teams/{teamId}/settlements")
  public ResponseEntity<Page<SettlementResponse>> readSettlements(
      @PathVariable Long teamId,
      @RequestParam(required = false) Long expenseId,
      @RequestParam(required = false) Long payerId,
      @RequestParam(required = false) Long settlerId,
      @RequestParam(required = false) Boolean isSettled,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "10") Integer size) {
    Page<SettlementResponse> settlementPage = settlementService.readSettlementPage(teamId, payerId,
        settlerId, expenseId, isSettled, PageRequest.of(page, size));
    return ResponseEntity.ok(settlementPage);
  }

  @PatchMapping("/settlements/{settlementId}")
  public ResponseEntity<SettlementResponse> updateSettlement(@PathVariable Long settlementId,
      @RequestParam(defaultValue = "false") Boolean settledOnly
      , @RequestBody SettlementUpdateRequest request) {
    if (settledOnly) {
      return ResponseEntity.ok(settlementService.settleSettlement(settlementId));
    } else {
      return ResponseEntity.ok(settlementService.updateSettlement(settlementId, request));
    }
  }
}
