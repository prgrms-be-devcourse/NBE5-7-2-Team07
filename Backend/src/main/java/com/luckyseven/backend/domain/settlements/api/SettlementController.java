package com.luckyseven.backend.domain.settlements.api;

import com.luckyseven.backend.domain.settlements.app.SettlementService;
import com.luckyseven.backend.domain.settlements.dto.SettlementResponse;
import com.luckyseven.backend.domain.settlements.dto.SettlementSearchCondition;
import com.luckyseven.backend.domain.settlements.dto.SettlementUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Settlement")
public class SettlementController {

  private final SettlementService settlementService;

  @Operation(summary = "ID로 정산 조회")
  @ApiResponse(responseCode = "200", description = "Settlement found")
  @ApiResponse(responseCode = "404", description = "Settlement not found")
  @GetMapping("/settlements/{settlementId}")
  public ResponseEntity<SettlementResponse> readSettlement(@PathVariable Long settlementId) {
    return ResponseEntity.ok(settlementService.readSettlement(settlementId));
  }

  @Operation(summary = "팀ID로 정산 목록 조회, 필터조건 추가 가능")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved settlements")
  @ApiResponse(responseCode = "400", description = "TeamID가 null인경우")
  @GetMapping("/teams/{teamId}/settlements")
  public ResponseEntity<Page<SettlementResponse>> readSettlements(
      @PathVariable Long teamId,
      @ModelAttribute SettlementSearchCondition condition,
      @PageableDefault(page = 0, size = 10) Pageable pageable
  ) {
    Page<SettlementResponse> settlementPage = settlementService.readSettlementPage(teamId,
        condition, pageable);
    return ResponseEntity.ok(settlementPage);
  }

  @Operation(summary = "Update settlement")
  @ApiResponse(responseCode = "200", description = "Settlement updated successfully")
  @ApiResponse(responseCode = "404", description = "Settlement not found")
  @PatchMapping("/settlements/{settlementId}")
  public ResponseEntity<SettlementResponse> updateSettlement(
      @PathVariable Long settlementId,
      @Parameter(description = "true면 body를 무시하고 정산처리만 진행") @RequestParam(defaultValue = "false") Boolean settledOnly
      , @Valid @RequestBody SettlementUpdateRequest request) {
    if (settledOnly) {
      return ResponseEntity.ok(settlementService.settleSettlement(settlementId));
    } else {
      return ResponseEntity.ok(settlementService.updateSettlement(settlementId, request));
    }
  }
}
