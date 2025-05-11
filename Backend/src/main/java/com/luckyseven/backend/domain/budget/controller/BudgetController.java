package com.luckyseven.backend.domain.budget.controller;

import com.luckyseven.backend.domain.budget.dto.BudgetCreateRequest;
import com.luckyseven.backend.domain.budget.dto.BudgetCreateResponse;
import com.luckyseven.backend.domain.budget.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/{teamId}")
  public ResponseEntity<BudgetCreateResponse> create(@PathVariable Long teamId,
      @RequestBody @Valid BudgetCreateRequest request) {

    // TODO: teamId가 유효한 지 검증 필요
    request.validate();

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(budgetService.save(teamId, request));
  }

}
