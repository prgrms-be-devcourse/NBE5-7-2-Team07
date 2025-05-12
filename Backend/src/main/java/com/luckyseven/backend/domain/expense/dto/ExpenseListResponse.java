package com.luckyseven.backend.domain.expense.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpenseListResponse {

  private List<ExpenseResponse> content;
  private int page;
  private int size;
  private int totalPages;
  private long totalElements;
}
