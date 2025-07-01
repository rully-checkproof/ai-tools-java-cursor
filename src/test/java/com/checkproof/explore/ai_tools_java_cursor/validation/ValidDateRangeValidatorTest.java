package com.checkproof.explore.ai_tools_java_cursor.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidDateRangeValidator Tests")
class ValidDateRangeValidatorTest {

    @Mock
    private ValidDateRange validDateRangeAnnotation;

    @Mock
    private ConstraintValidatorContext context;

    private ValidDateRangeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ValidDateRangeValidator();
    }

    @Test
    @DisplayName("Should initialize with default values")
    void shouldInitializeWithDefaultValues() {
        // Given
        when(validDateRangeAnnotation.startField()).thenReturn("startDate");
        when(validDateRangeAnnotation.endField()).thenReturn("endDate");
        when(validDateRangeAnnotation.allowEqual()).thenReturn(false);

        // When
        validator.initialize(validDateRangeAnnotation);

        // Then
        assertNotNull(validator);
    }

    @Test
    @DisplayName("Should initialize with custom values")
    void shouldInitializeWithCustomValues() {
        // Given
        when(validDateRangeAnnotation.startField()).thenReturn("beginDate");
        when(validDateRangeAnnotation.endField()).thenReturn("finishDate");
        when(validDateRangeAnnotation.allowEqual()).thenReturn(true);

        // When
        validator.initialize(validDateRangeAnnotation);

        // Then
        assertNotNull(validator);
    }

    @Test
    @DisplayName("Should return true for null value")
    void shouldReturnTrueForNullValue() {
        // Given
        setupDefaultValidator();

        // When
        boolean result = validator.isValid(null, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should validate valid date range when start is before end")
    void shouldValidateValidDateRangeWhenStartIsBeforeEnd() {
        // Given
        setupDefaultValidator();
        TestObject testObject = new TestObject();
        testObject.setStartDate(LocalDateTime.of(2024, 1, 15, 9, 0));
        testObject.setEndDate(LocalDateTime.of(2024, 1, 15, 17, 0));

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject invalid date range when start is after end")
    void shouldRejectInvalidDateRangeWhenStartIsAfterEnd() {
        // Given
        setupDefaultValidator();
        TestObject testObject = new TestObject();
        testObject.setStartDate(LocalDateTime.of(2024, 1, 15, 17, 0));
        testObject.setEndDate(LocalDateTime.of(2024, 1, 15, 9, 0));

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate equal dates when allowEqual is true")
    void shouldValidateEqualDatesWhenAllowEqualIsTrue() {
        // Given
        setupValidatorWithAllowEqual(true);
        TestObject testObject = new TestObject();
        LocalDateTime sameTime = LocalDateTime.of(2024, 1, 15, 9, 0);
        testObject.setStartDate(sameTime);
        testObject.setEndDate(sameTime);

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject equal dates when allowEqual is false")
    void shouldRejectEqualDatesWhenAllowEqualIsFalse() {
        // Given
        setupDefaultValidator();
        TestObject testObject = new TestObject();
        LocalDateTime sameTime = LocalDateTime.of(2024, 1, 15, 9, 0);
        testObject.setStartDate(sameTime);
        testObject.setEndDate(sameTime);

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return true when start date is null")
    void shouldReturnTrueWhenStartDateIsNull() {
        // Given
        setupDefaultValidator();
        TestObject testObject = new TestObject();
        testObject.setStartDate(null);
        testObject.setEndDate(LocalDateTime.of(2024, 1, 15, 17, 0));

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return true when end date is null")
    void shouldReturnTrueWhenEndDateIsNull() {
        // Given
        setupDefaultValidator();
        TestObject testObject = new TestObject();
        testObject.setStartDate(LocalDateTime.of(2024, 1, 15, 9, 0));
        testObject.setEndDate(null);

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return true when both dates are null")
    void shouldReturnTrueWhenBothDatesAreNull() {
        // Given
        setupDefaultValidator();
        TestObject testObject = new TestObject();
        testObject.setStartDate(null);
        testObject.setEndDate(null);

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when start field is not LocalDateTime")
    void shouldReturnFalseWhenStartFieldIsNotLocalDateTime() {
        // Given
        setupDefaultValidator();
        TestObjectWithStringStart testObject = new TestObjectWithStringStart();
        testObject.setStartDate("2024-01-15");
        testObject.setEndDate(LocalDateTime.of(2024, 1, 15, 17, 0));

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should return false when end field is not LocalDateTime")
    void shouldReturnFalseWhenEndFieldIsNotLocalDateTime() {
        // Given
        setupDefaultValidator();
        TestObjectWithStringEnd testObject = new TestObjectWithStringEnd();
        testObject.setStartDate(LocalDateTime.of(2024, 1, 15, 9, 0));
        testObject.setEndDate("2024-01-15");

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should validate with custom field names")
    void shouldValidateWithCustomFieldNames() {
        // Given
        setupValidatorWithCustomFields();
        TestObjectWithCustomFields testObject = new TestObjectWithCustomFields();
        testObject.setBeginDate(LocalDateTime.of(2024, 1, 15, 9, 0));
        testObject.setFinishDate(LocalDateTime.of(2024, 1, 15, 17, 0));

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should reject with custom field names when invalid")
    void shouldRejectWithCustomFieldNamesWhenInvalid() {
        // Given
        setupValidatorWithCustomFields();
        TestObjectWithCustomFields testObject = new TestObjectWithCustomFields();
        testObject.setBeginDate(LocalDateTime.of(2024, 1, 15, 17, 0));
        testObject.setFinishDate(LocalDateTime.of(2024, 1, 15, 9, 0));

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle edge case with same date but different times")
    void shouldHandleEdgeCaseWithSameDateButDifferentTimes() {
        // Given
        setupDefaultValidator();
        TestObject testObject = new TestObject();
        testObject.setStartDate(LocalDateTime.of(2024, 1, 15, 9, 0));
        testObject.setEndDate(LocalDateTime.of(2024, 1, 15, 9, 1));

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should handle edge case with same date and time when allowEqual is true")
    void shouldHandleEdgeCaseWithSameDateAndTimeWhenAllowEqualIsTrue() {
        // Given
        setupValidatorWithAllowEqual(true);
        TestObject testObject = new TestObject();
        LocalDateTime sameTime = LocalDateTime.of(2024, 1, 15, 9, 0, 0, 0);
        testObject.setStartDate(sameTime);
        testObject.setEndDate(sameTime);

        // When
        boolean result = validator.isValid(testObject, context);

        // Then
        assertTrue(result);
    }

    private void setupDefaultValidator() {
        when(validDateRangeAnnotation.startField()).thenReturn("startDate");
        when(validDateRangeAnnotation.endField()).thenReturn("endDate");
        when(validDateRangeAnnotation.allowEqual()).thenReturn(false);
        validator.initialize(validDateRangeAnnotation);
    }

    private void setupValidatorWithAllowEqual(boolean allowEqual) {
        when(validDateRangeAnnotation.startField()).thenReturn("startDate");
        when(validDateRangeAnnotation.endField()).thenReturn("endDate");
        when(validDateRangeAnnotation.allowEqual()).thenReturn(allowEqual);
        validator.initialize(validDateRangeAnnotation);
    }

    private void setupValidatorWithCustomFields() {
        when(validDateRangeAnnotation.startField()).thenReturn("beginDate");
        when(validDateRangeAnnotation.endField()).thenReturn("finishDate");
        when(validDateRangeAnnotation.allowEqual()).thenReturn(false);
        validator.initialize(validDateRangeAnnotation);
    }

    // Test helper classes
    public static class TestObject {
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        public LocalDateTime getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDateTime startDate) {
            this.startDate = startDate;
        }

        public LocalDateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDateTime endDate) {
            this.endDate = endDate;
        }
    }

    public static class TestObjectWithStringStart {
        private String startDate;
        private LocalDateTime endDate;

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public LocalDateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDateTime endDate) {
            this.endDate = endDate;
        }
    }

    public static class TestObjectWithStringEnd {
        private LocalDateTime startDate;
        private String endDate;

        public LocalDateTime getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDateTime startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }

    public static class TestObjectWithCustomFields {
        private LocalDateTime beginDate;
        private LocalDateTime finishDate;

        public LocalDateTime getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(LocalDateTime beginDate) {
            this.beginDate = beginDate;
        }

        public LocalDateTime getFinishDate() {
            return finishDate;
        }

        public void setFinishDate(LocalDateTime finishDate) {
            this.finishDate = finishDate;
        }
    }
} 