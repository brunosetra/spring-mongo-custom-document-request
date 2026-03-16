package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.dto.response.FieldValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for StringFieldValidator.
 * Tests all validation scenarios including edge cases, error handling, and different constraint combinations.
 */
class StringFieldValidatorTest {

    private StringFieldValidator stringFieldValidator;
    private DocRequestMetadata metadata;

    @BeforeEach
    void setUp() {
        stringFieldValidator = new StringFieldValidator();
        
        // Create test metadata
        metadata = DocRequestMetadata.builder()
            .uuid("test-metadata-uuid")
            .name("Test Metadata")
            .description("Test metadata for string field validation")
            .build();
    }

    @Nested
    @DisplayName("getType - Basic Functionality")
    class GetTypeTests {

        @Test
        @DisplayName("Should return STRING type")
        void shouldReturnStringType() {
            // Act
            DocRequestFieldType result = stringFieldValidator.getType();

            // Assert
            assertEquals(DocRequestFieldType.STRING, result);
        }
    }

    @Nested
    @DisplayName("validateValue - Positive Scenarios")
    class ValidateValuePositiveTests {

        @Test
        @DisplayName("Should validate string within min and max length constraints")
        void shouldValidateStringWithinConstraints() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("username")
                .description("User username")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(3)
                .max(20)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "john_doe";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("username", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate string with only min constraint")
        void shouldValidateStringWithOnlyMinConstraint() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("description")
                .description("Field description")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(10)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "This is a valid description that meets minimum length";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("description", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate string with only max constraint")
        void shouldValidateStringWithOnlyMaxConstraint() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("title")
                .description("Field title")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .max(100)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "Short title";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("title", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate string with exact min length")
        void shouldValidateStringWithExactMinLength() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("code")
                .description("Field code")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(5)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "abcde"; // Exactly 5 characters

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("code", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate string with exact max length")
        void shouldValidateStringWithExactMaxLength() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("name")
                .description("Field name")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .max(10)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "abcdefghij"; // Exactly 10 characters

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("name", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate empty string when no constraints")
        void shouldValidateEmptyStringWhenNoConstraints() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("optionalField")
                .description("Optional field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .required(false)
                .fieldOrder(1)
                .build();

            String testValue = "";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("optionalField", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate string with special characters")
        void shouldValidateStringWithSpecialCharacters() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("password")
                .description("Field password")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(8)
                .max(50)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "P@ssw0rd!@#$%^&*()";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("password", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate string with unicode characters")
        void shouldValidateStringWithUnicodeCharacters() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("comment")
                .description("Field comment")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(5)
                .max(100)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "Olá mundo! 🌍 This has unicode characters";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("comment", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate string with spaces")
        void shouldValidateStringWithSpaces() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fullName")
                .description("Full name")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(5)
                .max(50)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "John Doe Smith";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("fullName", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate string with numbers")
        void shouldValidateStringWithNumbers() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("productId")
                .description("Product ID")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(5)
                .max(20)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "PROD-12345";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("productId", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("validateValue - Negative Scenarios")
    class ValidateValueNegativeTests {

        @Test
        @DisplayName("Should return error for string shorter than min length")
        void shouldReturnErrorForStringShorterThanMinLength() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("username")
                .description("User username")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(5)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "abc"; // Only 3 characters, less than min 5

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("username", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_STRING_MIN_LENGTH", result.get().getErrorCode());
            assertEquals("Field 'username' must have at least 5 characters", result.get().getMessage());
            assertEquals("abc", result.get().getRejectedValue());
        }

        @Test
        @DisplayName("Should return error for string longer than max length")
        void shouldReturnErrorForStringLongerThanMaxLength() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("description")
                .description("Field description")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .max(20)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "This is a very long description that exceeds the maximum allowed length of 20 characters";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("description", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_STRING_MAX_LENGTH", result.get().getErrorCode());
            assertEquals("Field 'description' must not exceed 20 characters", result.get().getMessage());
            assertEquals(testValue, result.get().getRejectedValue());
        }

        @Test
        @DisplayName("Should return error for string both shorter than min and longer than max")
        void shouldReturnErrorForStringBothShorterThanMinAndLongerThanMax() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("code")
                .description("Field code")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(5)
                .max(10)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "shor"; // 5 characters, but should trigger min error first

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("code", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_STRING_MIN_LENGTH", result.get().getErrorCode());
            assertEquals("Field 'code' must have at least 5 characters", result.get().getMessage());
            assertEquals("shor", result.get().getRejectedValue());
        }

        @Test
        @DisplayName("Should return error for string with custom error code reference")
        void shouldReturnErrorWithCustomErrorCodeReference() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("title")
                .description("Field title")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(3)
                .max(50)
                .errorCodeReference("CUSTOM_TITLE_ERROR")
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "a"; // Too short

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("title", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("CUSTOM_TITLE_ERROR", result.get().getErrorCode());
            assertEquals("Field 'title' must have at least 3 characters", result.get().getMessage());
            assertEquals("a", result.get().getRejectedValue());
        }
    }

    @Nested
    @DisplayName("validateValue - Edge Cases")
    class ValidateValueEdgeCasesTests {

        @Test
        @DisplayName("Should handle very large strings efficiently")
        void shouldHandleVeryLargeStringsEfficiently() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("largeText")
                .description("Large text field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .max(10000)
                .required(true)
                .fieldOrder(1)
                .build();

            StringBuilder largeString = new StringBuilder();
            for (int i = 0; i < 5000; i++) {
                largeString.append("a");
            }

            String testValue = largeString.toString();

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("largeText", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle string with exactly min length boundary")
        void shouldHandleStringWithExactlyMinLengthBoundary() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("boundaryTest")
                .description("Boundary test field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(1000)
                .required(true)
                .fieldOrder(1)
                .build();

            StringBuilder boundaryString = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                boundaryString.append("a");
            }

            String testValue = boundaryString.toString();

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("boundaryTest", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle string with exactly max length boundary")
        void shouldHandleStringWithExactlyMaxLengthBoundary() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("boundaryTest")
                .description("Boundary test field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .max(1000)
                .required(true)
                .fieldOrder(1)
                .build();

            StringBuilder boundaryString = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                boundaryString.append("a");
            }

            String testValue = boundaryString.toString();

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("boundaryTest", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle string with one character less than min length")
        void shouldHandleStringWithOneCharacterLessThanMinLength() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("boundaryTest")
                .description("Boundary test field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(10)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "123456789"; // 9 characters, one less than min 10

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("boundaryTest", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_STRING_MIN_LENGTH", result.get().getErrorCode());
            assertEquals("Field 'boundaryTest' must have at least 10 characters", result.get().getMessage());
            assertEquals("123456789", result.get().getRejectedValue());
        }

        @Test
        @DisplayName("Should handle string with one character more than max length")
        void shouldHandleStringWithOneCharacterMoreThanMaxLength() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("boundaryTest")
                .description("Boundary test field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .max(10)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "12345678901"; // 11 characters, one more than max 10

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("boundaryTest", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_STRING_MAX_LENGTH", result.get().getErrorCode());
            assertEquals("Field 'boundaryTest' must not exceed 10 characters", result.get().getMessage());
            assertEquals("12345678901", result.get().getRejectedValue());
        }

        @Test
        @DisplayName("Should handle string with mixed whitespace characters")
        void shouldHandleStringWithMixedWhitespaceCharacters() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("whitespaceTest")
                .description("Whitespace test field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(5)
                .max(20)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "  \t\n  mixed whitespace  \t\n  ";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("whitespaceTest", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle string with emoji characters")
        void shouldHandleStringWithEmojiCharacters() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("emojiTest")
                .description("Emoji test field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(5)
                .max(20)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "Hello 🌍 World! 😊";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("emojiTest", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("validateValue - Constraint Combinations")
    class ValidateValueConstraintCombinationsTests {

        @Test
        @DisplayName("Should validate string with both min and max constraints within range")
        void shouldValidateStringWithBothMinAndMaxConstraintsWithinRange() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("username")
                .description("User username")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(3)
                .max(20)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "john_doe";

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("username", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate string with both min and max constraints at boundaries")
        void shouldValidateStringWithBothMinAndMaxConstraintsAtBoundaries() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("code")
                .description("Field code")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(5)
                .max(10)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "abcde"; // Exactly at min boundary

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("code", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return min error when string is below min but within max range")
        void shouldReturnMinErrorWhenStringIsBelowMinButWithinMaxRange() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("username")
                .description("User username")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(5)
                .max(20)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "abc"; // 3 characters, below min but within max

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("username", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_STRING_MIN_LENGTH", result.get().getErrorCode());
            assertEquals("Field 'username' must have at least 5 characters", result.get().getMessage());
            assertEquals("abc", result.get().getRejectedValue());
        }

        @Test
        @DisplayName("Should return max error when string is above max but within min range")
        void shouldReturnMaxErrorWhenStringIsAboveMaxButWithinMinRange() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("username")
                .description("User username")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(3)
                .max(10)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "this_is_too_long"; // 16 characters, above max but within min

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("username", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_STRING_MAX_LENGTH", result.get().getErrorCode());
            assertEquals("Field 'username' must not exceed 10 characters", result.get().getMessage());
            assertEquals("this_is_too_long", result.get().getRejectedValue());
        }

        @Test
        @DisplayName("Should return min error when min constraint is violated first")
        void shouldReturnMinErrorWhenMinConstraintIsViolatedFirst() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("field")
                .description("Test field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(10)
                .max(5) // Invalid constraint (min > max)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "short"; // 5 characters

            // Act
            Optional<FieldValidationError> result = stringFieldValidator.validateValue("field", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_STRING_MIN_LENGTH", result.get().getErrorCode());
            assertEquals("Field 'field' must have at least 10 characters", result.get().getMessage());
            assertEquals("short", result.get().getRejectedValue());
        }
    }

    @Nested
    @DisplayName("validateValue - Performance Tests")
    class ValidateValuePerformanceTests {

        @Test
        @DisplayName("Should handle multiple validations efficiently")
        void shouldHandleMultipleValidationsEfficiently() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("testField")
                .description("Test field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .min(5)
                .max(50)
                .required(true)
                .fieldOrder(1)
                .build();

            int iterations = 1000;
            long startTime = System.currentTimeMillis();

            // Act
            for (int i = 0; i < iterations; i++) {
                String testValue = "testValue" + i;
                Optional<FieldValidationError> result = stringFieldValidator.validateValue("testField", testValue, fieldMetadata);
                assertTrue(result.isEmpty());
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Assert
            assertTrue(duration < 1000, "Multiple validations should complete within 1 second");
        }

        @Test
        @DisplayName("Should handle large string validations efficiently")
        void shouldHandleLargeStringValidationsEfficiently() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("largeField")
                .description("Large field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .max(10000)
                .required(true)
                .fieldOrder(1)
                .build();

            StringBuilder largeString = new StringBuilder();
            for (int i = 0; i < 5000; i++) {
                largeString.append("a");
            }

            String testValue = largeString.toString();
            int iterations = 100;
            long startTime = System.currentTimeMillis();

            // Act
            for (int i = 0; i < iterations; i++) {
                Optional<FieldValidationError> result = stringFieldValidator.validateValue("largeField", testValue, fieldMetadata);
                assertTrue(result.isEmpty());
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Assert
            assertTrue(duration < 1000, "Large string validations should complete within 1 second");
        }
    }

    @Nested
    @DisplayName("Integration with AbstractFieldValidator")
    class IntegrationWithAbstractFieldValidatorTests {

        @Test
        @DisplayName("Should handle required field validation through abstract validator")
        void shouldHandleRequiredFieldValidationThroughAbstractValidator() {
            // Arrange
            StringFieldValidator validator = new StringFieldValidator();
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("requiredField")
                .description("Required field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "";

            // Act
            Optional<FieldValidationError> result = validator.validate("requiredField", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_REQUIRED", result.get().getErrorCode());
            assertEquals("Field 'requiredField' is required", result.get().getMessage());
        }

        @Test
        @DisplayName("Should handle non-required field validation through abstract validator")
        void shouldHandleNonRequiredFieldValidationThroughAbstractValidator() {
            // Arrange
            StringFieldValidator validator = new StringFieldValidator();
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("optionalField")
                .description("Optional field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .required(false)
                .fieldOrder(1)
                .build();

            String testValue = "";

            // Act
            Optional<FieldValidationError> result = validator.validate("optionalField", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle null value for non-required field through abstract validator")
        void shouldHandleNullValueForNonRequiredFieldThroughAbstractValidator() {
            // Arrange
            StringFieldValidator validator = new StringFieldValidator();
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("optionalField")
                .description("Optional field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .required(false)
                .fieldOrder(1)
                .build();

            Object testValue = null;

            // Act
            Optional<FieldValidationError> result = validator.validate("optionalField", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle null value for required field through abstract validator")
        void shouldHandleNullValueForRequiredFieldThroughAbstractValidator() {
            // Arrange
            StringFieldValidator validator = new StringFieldValidator();
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("requiredField")
                .description("Required field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            Object testValue = null;

            // Act
            Optional<FieldValidationError> result = validator.validate("requiredField", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_REQUIRED", result.get().getErrorCode());
            assertEquals("Field 'requiredField' is required", result.get().getMessage());
        }

        @Test
        @DisplayName("Should handle blank string for required field through abstract validator")
        void shouldHandleBlankStringForRequiredFieldThroughAbstractValidator() {
            // Arrange
            StringFieldValidator validator = new StringFieldValidator();
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("requiredField")
                .description("Required field")
                .type(DocRequestFieldType.STRING)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "   ";

            // Act
            Optional<FieldValidationError> result = validator.validate("requiredField", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_REQUIRED", result.get().getErrorCode());
            assertEquals("Field 'requiredField' is required", result.get().getMessage());
        }
    }
}