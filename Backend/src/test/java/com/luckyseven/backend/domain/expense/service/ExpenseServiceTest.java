package com.luckyseven.backend.domain.expense.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.luckyseven.backend.domain.expense.dto.ExpenseRequest;
import com.luckyseven.backend.domain.expense.dto.ExpenseResponse;
import com.luckyseven.backend.domain.expense.dto.ExpenseUpdateRequest;
import com.luckyseven.backend.domain.expense.entity.Expense;
import com.luckyseven.backend.domain.expense.enums.ExpenseCategory;
import com.luckyseven.backend.domain.expense.enums.PaymentMethod;
import com.luckyseven.backend.domain.expense.repository.ExpenseRepository;
import com.luckyseven.backend.sharedkernel.exception.CustomLogicException;
import com.luckyseven.backend.sharedkernel.exception.ExceptionCode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Captor
    private ArgumentCaptor<Expense> expenseCaptor;

    private ExpenseRequest saveRequest;
    private ExpenseUpdateRequest updateRequest;
    private Long teamId;
    private Long expenseId;

    @BeforeEach
    void setUp() {
        teamId = 10L;
        expenseId = 100L;

        saveRequest = ExpenseRequest.builder()
            .description("Lunch for team")
            .amount(new BigDecimal("15000.00"))
            .category(ExpenseCategory.MEAL)
            .payerId(1L)
            .settlerId(List.of(1L, 2L, 3L))
            .paymentMethod(PaymentMethod.CARD)
            .build();

        updateRequest = ExpenseUpdateRequest.builder()
            .description("Dinner for team")
            .amount(new BigDecimal("20000.00"))
            .category(ExpenseCategory.MEAL)
            .build();
    }

    @Nested
    @DisplayName("saveExpense 메서드 호출 시")
    class SaveExpenseTests {

        @Test
        @DisplayName("지출 저장 후 응답 반환 테스트")
        void saveExpense_success() {
            // Given
            Expense savedExpense = Expense.builder()
                .id(expenseId)
                .description(saveRequest.getDescription())
                .amount(saveRequest.getAmount())
                .category(saveRequest.getCategory())
                .payerId(saveRequest.getPayerId())
                .teamId(teamId)
                .build();
            given(expenseRepository.save(any(Expense.class))).willReturn(savedExpense);

            // When
            ExpenseResponse response = expenseService.saveExpense(teamId, saveRequest);

            // Then
            verify(expenseRepository).save(expenseCaptor.capture());
            Expense toSave = expenseCaptor.getValue();
            assertThat(toSave.getDescription()).isEqualTo(saveRequest.getDescription());
            assertThat(toSave.getAmount()).isEqualByComparingTo(saveRequest.getAmount());
            assertThat(toSave.getCategory()).isEqualTo(saveRequest.getCategory());
            assertThat(toSave.getPayerId()).isEqualTo(saveRequest.getPayerId());
            assertThat(toSave.getTeamId()).isEqualTo(teamId);

            assertThat(response.getExpenseId()).isEqualTo(savedExpense.getId());
            assertThat(response.getAmount()).isEqualByComparingTo(savedExpense.getAmount());
        }
    }

    @Nested
    @DisplayName("updateExpense 메서드 호출 시")
    class UpdateExpenseTests {

        @Test
        @DisplayName("지출 수정 성공 테스트")
        void updateExpense_success() {
            // Given
            Expense existing = Expense.builder()
                .id(expenseId)
                .description("럭키비키즈 점심 식사")
                .amount(new BigDecimal("10000.00"))
                .category(ExpenseCategory.MEAL)
                .payerId(1L)
                .teamId(teamId)
                .build();
            given(expenseRepository.findById(expenseId)).willReturn(Optional.of(existing));

            // When
            ExpenseResponse response = expenseService.updateExpense(expenseId, updateRequest);

            // Then
            assertThat(existing.getDescription()).isEqualTo(updateRequest.getDescription());
            assertThat(existing.getAmount()).isEqualByComparingTo(updateRequest.getAmount());
            assertThat(existing.getCategory()).isEqualTo(updateRequest.getCategory());

            assertThat(response.getExpenseId()).isEqualTo(expenseId);
            assertThat(response.getAmount()).isEqualByComparingTo(updateRequest.getAmount());
        }

        @Test
        @DisplayName("지출 수정 실패")
        void updateExpense_notFound_assertj() {
            // Given
            given(expenseRepository.findById(expenseId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> expenseService.updateExpense(expenseId, updateRequest))
                .isInstanceOf(CustomLogicException.class)
                .hasMessage("지출 내역을 조회할 수 없습니다.")
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.EXPENSE_NOT_FOUND);
        }
    }
}
