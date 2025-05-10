package com.luckyseven.backend.domain.expense.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.luckyseven.backend.domain.expense.dto.CreateExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseBalanceResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseRequest;
import com.luckyseven.backend.domain.expense.dto.ExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseUpdateRequest;
import com.luckyseven.backend.domain.expense.entity.Expense;
import com.luckyseven.backend.domain.expense.enums.ExpenseCategory;
import com.luckyseven.backend.domain.expense.enums.PaymentMethod;
import com.luckyseven.backend.domain.expense.repository.ExpenseRepository;
import com.luckyseven.backend.domain.expense.util.TempBudget;
import com.luckyseven.backend.domain.expense.util.TempMember;
import com.luckyseven.backend.domain.expense.util.TempSettlement;
import com.luckyseven.backend.domain.expense.util.TempSettlementRepository;
import com.luckyseven.backend.domain.expense.util.TempTeam;
import com.luckyseven.backend.domain.expense.util.TempTeamRepository;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

  // TODO: Temp 수정
  // TODO: 너무 코드양이 많아서 리팩토링 예정
  @Mock
  private TempTeamRepository teamRepository;

  @Mock
  private TempSettlementRepository settlementRepository;

  @Mock
  private ExpenseRepository expenseRepository;

  @InjectMocks
  private ExpenseService expenseService;

  private TempTeam team;
  private TempBudget budget;

  @BeforeEach
  void setUp() {
    budget = TempBudget.builder()
        .id(1L)
        .balance(new BigDecimal("100000.00"))
        .foreignBalance(new BigDecimal("50.00"))
        .build();
    team = TempTeam.builder()
        .id(1L)
        .budget(budget)
        .build();
  }

  @Nested
  @DisplayName("지출 등록 테스트")
  class SaveExpenseTests {

    @Test
    @DisplayName("지출 등록 성공")
    void success() {
      // given
      ExpenseRequest request = ExpenseRequest.builder()
          .description("럭키비키즈 점심 식사")
          .amount(new BigDecimal("50000.00"))
          .category(ExpenseCategory.MEAL)
          .settlerId(Collections.singletonList(1L))
          .paymentMethod(PaymentMethod.CASH)
          .build();

      when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
      when(expenseRepository.save(any(Expense.class)))
          .thenAnswer(invocation -> invocation.getArgument(0));

      // when
      CreateExpenseResponse response = expenseService.saveExpense(1L, request);

      // then
      ArgumentCaptor<Expense> captor = ArgumentCaptor.forClass(Expense.class);
      verify(expenseRepository).save(captor.capture());
      Expense saved = captor.getValue();
      assertThat(saved.getDescription()).isEqualTo(request.getDescription());

      BigDecimal expectedBalance = new BigDecimal("100000.00").subtract(request.getAmount());
      assertThat(budget.getBalance()).isEqualByComparingTo(expectedBalance);
      assertThat(response.getBalance()).isEqualByComparingTo(expectedBalance);
    }

    @Nested
    class ExceptionCases {

      @Test
      @DisplayName("존재하지 않는 팀")
      void teamNotFound_throwsException() {
        when(teamRepository.findById(999L)).thenReturn(Optional.empty());
        ExpenseRequest request = ExpenseRequest.builder()
            .description("변종된 럭키비키즈 나쁜 점심 식사")
            .amount(new BigDecimal("1000.00"))
            .category(ExpenseCategory.MEAL)
            .settlerId(Collections.singletonList(1L))
            .paymentMethod(PaymentMethod.CASH)
            .build();

        assertThatThrownBy(() -> expenseService.saveExpense(999L, request))
            .isInstanceOf(CustomLogicException.class)
            .extracting("exceptionCode")
            .isEqualTo(ExceptionCode.TEAM_NOT_FOUND);
      }

      @Test
      @DisplayName("예산보다 큰 지출 금액")
      void insufficientBalance_throwsException() {
        when(teamRepository.findById(1L)).thenReturn(Optional.of(team));
        ExpenseRequest request = ExpenseRequest.builder()
            .description("럭키비키즈 팀 배부르게 식사")
            .amount(new BigDecimal("1000000.00"))
            .category(ExpenseCategory.MEAL)
            .settlerId(Collections.singletonList(1L))
            .paymentMethod(PaymentMethod.CARD)
            .build();

        assertThatThrownBy(() -> expenseService.saveExpense(1L, request))
            .isInstanceOf(CustomLogicException.class)
            .extracting("exceptionCode")
            .isEqualTo(ExceptionCode.INSUFFICIENT_BALANCE);
      }
    }
  }

  @Nested
  @DisplayName("지출 조회 테스트")
  class GetExpenseTests {

    @Test
    @DisplayName("지출 조회 성공")
    void success() {

      // given
      TempMember payer = new TempMember();

      Expense expense = Expense.builder()
          .id(1L)
          .description("럭키비키즈 점심 식사")
          .amount(new BigDecimal("50000.00"))
          .category(ExpenseCategory.MEAL)
          .paymentMethod(PaymentMethod.CARD)
          .payer(payer)
          .team(team)
          .build();

      TempSettlement s1 = TempSettlement.builder()
          .id(100L)
          .expense(expense)
          .settler(new TempMember(10L, "박유한1"))
          .build();

      TempSettlement s2 = TempSettlement.builder()
          .id(200L)
          .expense(expense)
          .settler(new TempMember(20L, "박유한2"))
          .build();

      when(expenseRepository.findById(expense.getId())).thenReturn(Optional.of(expense));
      when(settlementRepository.findByExpenseId(expense.getId()))
          .thenReturn(List.of(s1, s2));

      // when
      ExpenseResponse response = expenseService.getExpense(expense.getId());

      // then
      assertThat(response.getDescription()).isEqualTo("럭키비키즈 점심 식사");
      assertThat(response.getAmount()).isEqualByComparingTo(new BigDecimal("50000"));
      assertThat(response.getCategory()).isEqualTo(ExpenseCategory.MEAL);
      assertThat(response.getPaymentMethod()).isEqualTo(PaymentMethod.CARD);
      assertThat(response.getSettlers().get(0).getId()).isEqualTo(10L);
      assertThat(response.getSettlers().get(1).getId()).isEqualTo(20L);

      verify(expenseRepository).findById(expense.getId());
      verify(settlementRepository).findByExpenseId(expense.getId());
    }

    @Test
    @DisplayName("존재하지 않는 지출 조회 시 예외 발생")
    void notFound_throwsException() {
      // given
      Long expenseId = 999L;
      when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> expenseService.getExpense(expenseId))
          .isInstanceOf(CustomLogicException.class)
          .extracting("exceptionCode")
          .isEqualTo(ExceptionCode.EXPENSE_NOT_FOUND);
    }
  }


  @Nested
  @DisplayName("지출 수정 테스트")
  class UpdateExpenseTests {

    @Test
    @DisplayName("지출 금액 증가 수정 성공")
    void increaseAmountSuccess() {

      // given
      ExpenseUpdateRequest request = ExpenseUpdateRequest.builder()
          .description("잘못 계산해서 수정한 럭키비키즈 점심 식사")
          .amount(new BigDecimal("70000.00"))
          .category(ExpenseCategory.MEAL)
          .build();

      // TODO: Temp 엔티티 수정
      TempMember payer = new TempMember();

      Expense original = Expense.builder()
          .id(1L)
          .description("원래 럭키비키즈 점심 식사")
          .amount(new BigDecimal("50000.00"))
          .category(ExpenseCategory.MEAL)
          .payer(payer)
          .team(team)
          .build();

      when(expenseRepository.findById(1L)).thenReturn(Optional.of(original));

      // when
      CreateExpenseResponse response = expenseService.updateExpense(1L, request);

      // then
      assertThat(original.getDescription()).isEqualTo(request.getDescription());
      assertThat(original.getAmount()).isEqualByComparingTo(request.getAmount());
      BigDecimal expectedDelta = request.getAmount().subtract(new BigDecimal("50000.00"));
      BigDecimal expectedBalance = new BigDecimal("100000.00").subtract(expectedDelta);
      assertThat(budget.getBalance()).isEqualByComparingTo(expectedBalance);
      assertThat(response.getBalance()).isEqualByComparingTo(expectedBalance);
    }

    @Test
    @DisplayName("지출 금액 감소 수정 성공")
    void decreaseAmountSuccess() {
      // given
      ExpenseUpdateRequest request = ExpenseUpdateRequest.builder()
          .description("업데이트된 점심 식사")
          .amount(new BigDecimal("30000.00"))
          .category(ExpenseCategory.MEAL)
          .build();

      TempMember payer = new TempMember();

      Expense original = Expense.builder()
          .id(1L)
          .description("원본 점심 식사")
          .amount(new BigDecimal("50000.00"))
          .category(ExpenseCategory.MEAL)
          .payer(payer)
          .team(team)
          .build();
      when(expenseRepository.findById(1L)).thenReturn(Optional.of(original));

      // when
      CreateExpenseResponse response = expenseService.updateExpense(1L, request);

      // then
      BigDecimal expectedDelta = request.getAmount().subtract(new BigDecimal("50000.00"));
      BigDecimal expectedBalance = new BigDecimal("100000.00").subtract(expectedDelta);
      assertThat(budget.getBalance()).isEqualByComparingTo(expectedBalance);
      assertThat(response.getBalance()).isEqualByComparingTo(expectedBalance);
    }

    @Test
    @DisplayName("존재하지 않는 지출")
    void expenseNotFound_throwsException() {
      when(expenseRepository.findById(999L)).thenReturn(Optional.empty());
      ExpenseUpdateRequest request = ExpenseUpdateRequest.builder()
          .description("없는 지출 수정")
          .amount(new BigDecimal("1000.00"))
          .category(ExpenseCategory.MEAL)
          .build();

      assertThatThrownBy(() -> expenseService.updateExpense(999L, request))
          .isInstanceOf(CustomLogicException.class)
          .extracting("exceptionCode")
          .isEqualTo(ExceptionCode.EXPENSE_NOT_FOUND);
    }

    @Test
    @DisplayName("예산 부족으로 수정 실패")
    void insufficientBalance_throwsException() {
      // given: 예산 10만, 원본 지출 5만, 업데이트 요청 200만
      ExpenseUpdateRequest request = ExpenseUpdateRequest.builder()
          .description("너무 많이 먹었는데 예산보다 많은 지출 수정")
          .amount(new BigDecimal("200000.00"))
          .category(ExpenseCategory.MEAL)
          .build();

      TempMember payer = new TempMember();

      Expense original = Expense.builder()
          .id(1L)
          .description("럭키비키즈 원래 점심 식사")
          .amount(new BigDecimal("50000.00"))
          .category(ExpenseCategory.MEAL)
          .payer(payer)
          .team(team)
          .build();
      when(expenseRepository.findById(1L)).thenReturn(Optional.of(original));

      // when & then
      assertThatThrownBy(() -> expenseService.updateExpense(1L, request))
          .isInstanceOf(CustomLogicException.class)
          .extracting("exceptionCode")
          .isEqualTo(ExceptionCode.INSUFFICIENT_BALANCE);
    }
  }

  @Nested
  @DisplayName("지출 삭제 테스트")
  class DeleteExpenseTests {

    @Test
    @DisplayName("지출 삭제 성공")
    void success() {
      // given: 기존 지출 금액 10만원, 삭제할 지출 3만원
      Expense expense = Expense.builder()
          .id(1L)
          .amount(new BigDecimal("30000.00"))
          .team(team)
          .build();
      when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

      // when
      ExpenseBalanceResponse response = expenseService.deleteExpense(1L);

      // then
      // 예산 13만원으로 증가
      BigDecimal expectedBalance = new BigDecimal("130000.00");
      assertThat(team.getBudget().getBalance()).isEqualByComparingTo(expectedBalance);
      assertThat(response.getBalance()).isEqualByComparingTo(expectedBalance);

      verify(expenseRepository).delete(expense);
    }

    @Test
    @DisplayName("존재하지 않는 지출 삭제 시 예외 발생")
    void expenseNotFound_throwsException() {
      when(expenseRepository.findById(999L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> expenseService.deleteExpense(999L))
          .isInstanceOf(CustomLogicException.class)
          .extracting("exceptionCode")
          .isEqualTo(ExceptionCode.EXPENSE_NOT_FOUND);
    }
  }
}
