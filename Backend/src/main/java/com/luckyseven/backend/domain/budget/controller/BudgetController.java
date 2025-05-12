package com.luckyseven.backend.domain.budget.controller;

import com.luckyseven.backend.domain.budget.dto.BudgetCreateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateResponse;
import com.luckyseven.backend.domain.budget.dto.BudgetReadResponse;
import com.luckyseven.backend.domain.budget.dto.BudgetUpdateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetUpdateResponse;
import com.luckyseven.backend.domain.budget.service.BudgetService;
import com.luckyseven.backend.domain.budget.validator.BudgetValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class BudgetController {

  private final BudgetService budgetService;
  private final BudgetValidator budgetValidator;

  @PostMapping("/{teamId}")
  public ResponseEntity<BudgetCreateResponse> create(@PathVariable Long teamId,
      @Valid @RequestBody BudgetCreateRequest request) {

    budgetValidator.validateRequest(request);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(budgetService.save(teamId, request));
  }

  @GetMapping("/{teamId}")
  public ResponseEntity<BudgetReadResponse> read(@PathVariable Long teamId) {
    return ResponseEntity.ok(budgetService.getByTeamId(teamId));
  }

  @PatchMapping("/{teamId}")
  public ResponseEntity<BudgetUpdateResponse> update(@PathVariable Long teamId,
      @Valid @RequestBody BudgetUpdateRequest request) {

    budgetValidator.validateRequest(request);

    return ResponseEntity.status(HttpStatus.OK)
        .body(budgetService.updateByTeamId(teamId, request));
  }
}
