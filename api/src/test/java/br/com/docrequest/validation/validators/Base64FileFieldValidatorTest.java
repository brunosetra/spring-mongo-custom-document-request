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

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for Base64FileFieldValidator.
 * Tests all validation scenarios including edge cases, error handling, and different Base64 formats.
 */
class Base64FileFieldValidatorTest {

    private Base64FileFieldValidator base64FileFieldValidator;
    private DocRequestMetadata metadata;

    @BeforeEach
    void setUp() throws Exception {
        base64FileFieldValidator = new Base64FileFieldValidator();
        
        // Use reflection to set the maxFileSizeMb field to 5MB for testing
        Field maxFileSizeMbField = Base64FileFieldValidator.class.getDeclaredField("maxFileSizeMb");
        maxFileSizeMbField.setAccessible(true);
        maxFileSizeMbField.setInt(base64FileFieldValidator, 5);
        
        // Create test metadata
        metadata = DocRequestMetadata.builder()
            .uuid("test-metadata-uuid")
            .name("Test Metadata")
            .description("Test metadata for Base64 file field validation")
            .build();
    }

    @Nested
    @DisplayName("getType - Basic Functionality")
    class GetTypeTests {

        @Test
        @DisplayName("Should return FILE type")
        void shouldReturnFileType() {
            // Act
            DocRequestFieldType result = base64FileFieldValidator.getType();

            // Assert
            assertEquals(DocRequestFieldType.FILE, result);
        }
    }

    @Nested
    @DisplayName("validateValue - Positive Scenarios")
    class ValidateValuePositiveTests {

        @Test
        @DisplayName("Should validate valid Base64 content")
        void shouldValidateValidBase64Content() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String validBase64 = "SGVsbG8gV29ybGQh"; // "Hello World!" in Base64

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", validBase64, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate Base64 with data URI prefix")
        void shouldValidateBase64WithDataUriPrefix() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String base64WithUri = "data:application/octet-stream;base64,SGVsbG8gV29ybGQh";

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", base64WithUri, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate Base64 with image data URI prefix")
        void shouldValidateBase64WithImageDataUriPrefix() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String base64WithImageUri = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/8QAFRABAQADAQAAAAAAAAAAAAAAAQIDAAURBiL/2gAMAwEAAhEDEQA/AP0ooooA//Z";

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", base64WithImageUri, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate Base64 with whitespace")
        void shouldValidateBase64WithWhitespace() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String base64WithWhitespace = "   SGVsbG8gV29ybGQh   ";

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", base64WithWhitespace, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate small Base64 content")
        void shouldValidateSmallBase64Content() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String smallBase64 = "SGVsbG8="; // "Hello" in Base64

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", smallBase64, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should validate Base64 content exactly at size limit")
        void shouldValidateBase64ContentExactlyAtSizeLimit() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            // Create Base64 content that decodes to exactly 5MB
            int targetSizeBytes = 5 * 1024 * 1024;
            int base64Size = (targetSizeBytes / 3) * 4 + (targetSizeBytes % 3 != 0 ? 4 : 0);
            
            String largeBase64 = "A".repeat(base64Size - 1);

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", largeBase64, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("validateValue - Negative Scenarios")
    class ValidateValueNegativeTests {

        @Test
        @DisplayName("Should return error for invalid Base64 content")
        void shouldReturnErrorForInvalidBase64Content() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String invalidBase64 = "This is not valid Base64 content!@#$";

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", invalidBase64, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_FILE_BASE64_INVALID", result.get().getErrorCode());
            assertEquals("Field 'fileContent' must be valid Base64-encoded content", result.get().getMessage());
            assertEquals(invalidBase64, result.get().getRejectedValue());
        }

        @Test
        @DisplayName("Should return error for Base64 with invalid characters")
        void shouldReturnErrorForBase64WithInvalidCharacters() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String invalidCharsBase64 = "SGVsbG8gV29ybGQh$%^"; // Contains invalid characters

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", invalidCharsBase64, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_FILE_BASE64_INVALID", result.get().getErrorCode());
            assertEquals("Field 'fileContent' must be valid Base64-encoded content", result.get().getMessage());
            assertEquals(invalidCharsBase64, result.get().getRejectedValue());
        }

        @Test
        @DisplayName("Should return error for Base64 content exceeding size limit")
        void shouldReturnErrorForBase64ContentExceedingSizeLimit() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            // Create Base64 content that decodes to more than 5MB
            int targetSizeBytes = 6 * 1024 * 1024; // 6MB
            int base64Size = (targetSizeBytes / 3) * 4 + (targetSizeBytes % 3 != 0 ? 4 : 0);
            
            StringBuilder base64Builder = new StringBuilder();
            for (int i = 0; i < base64Size; i++) {
                base64Builder.append("A");
            }
            String largeBase64 = base64Builder.toString();

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", largeBase64, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_FILE_TOO_LARGE", result.get().getErrorCode());
            assertEquals("Field 'fileContent' file size exceeds maximum of 5MB", result.get().getMessage());
            assertNull(result.get().getRejectedValue());
        }

        @Test
        @DisplayName("Should return error for Base64 content with data URI prefix exceeding size limit")
        void shouldReturnErrorForBase64ContentWithUriPrefixExceedingSizeLimit() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            // Create Base64 content that decodes to more than 5MB
            int targetSizeBytes = 6 * 1024 * 1024; // 6MB
            int base64Size = (targetSizeBytes / 3) * 4 + (targetSizeBytes % 3 != 0 ? 4 : 0);
            
            StringBuilder base64Builder = new StringBuilder();
            for (int i = 0; i < base64Size; i++) {
                base64Builder.append("A");
            }
            String largeBase64 = "data:application/octet-stream;base64," + base64Builder.toString();

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", largeBase64, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_FILE_TOO_LARGE", result.get().getErrorCode());
            assertEquals("Field 'fileContent' file size exceeds maximum of 5MB", result.get().getMessage());
            assertNull(result.get().getRejectedValue());
        }


        @Test
        @DisplayName("Should return error for non-string value")
        void shouldReturnErrorForNonStringValue() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            Integer testValue = -9808555;

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_FILE_BASE64_INVALID", result.get().getErrorCode());
            assertEquals( "Field 'fileContent' must be valid Base64-encoded content", result.get().getMessage());
        }
    }

    @Nested
    @DisplayName("validateValue - Edge Cases")
    class ValidateValueEdgeCasesTests {

        @Test
        @DisplayName("Should handle Base64 with multiple commas")
        void shouldHandleBase64WithMultipleCommas() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String base64WithMultipleCommas = "data:application/octet-stream;base64,SGVsbG8gV29ybGQh,data:image/jpeg;base64,SGVsbG8=";

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", base64WithMultipleCommas, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_FILE_BASE64_INVALID", result.get().getErrorCode());
        }

        @Test
        @DisplayName("Should handle Base64 with no data prefix")
        void shouldHandleBase64WithNoDataPrefix() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String base64NoPrefix = "SGVsbG8gV29ybGQh";

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", base64NoPrefix, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle Base64 with special characters in data URI")
        void shouldHandleBase64WithSpecialCharactersInDataUri() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String base64WithSpecialChars = "data:application/octet-stream; charset=utf-8;base64,SGVsbG8gV29ybGQh";

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", base64WithSpecialChars, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle Base64 with very long data URI")
        void shouldHandleBase64WithVeryLongDataUri() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            StringBuilder longUri = new StringBuilder("data:");
            for (int i = 0; i < 1000; i++) {
                longUri.append("x");
            }
            longUri.append(";base64,SGVsbG8gV29ybGQh");

            String base64WithLongUri = longUri.toString();

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", base64WithLongUri, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle Base64 with binary content")
        void shouldHandleBase64WithBinaryContent() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            // Create binary-like Base64 content
            String binaryBase64 = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+/wABAgMEBQYHCAkKCwwNDg8QERITFBUWFxgZGhscHR4fICEiIyQlJicoKSorLC0uLzAxMjM0NTY3ODk6Ozw9Pj9AQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpbXF1eX2BhYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ent8fX5/gIGCg4SFhoeIiYqLjI2Oj5CRkpOUlZaXmJmam5ydnp+goaKjpKWmp6ipqqusra6vsLGys7S1tre4ubq7vL2+v8DBwsPExcbHyMnKy8zNzs/Q0dLT1NXW19jZ2tvc3d7f4OHi4+Tl5ufo6err7O3u7/Dx8vP09fb3+Pn6+/z9/v8AAQIDBAUGBwgJCgsMDQ4PEBESExQVFhcYGRobHB0eHyAhIiMkJSYnKCkqKywtLi8wMTIzNDU2Nzg5Ojs8PT4/QEFCQ0RFRkdISUpLTE1OT1BRUlNUVVZXWFlaW1xdXl9gYWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXp7fH1+f4CBgoOEhYaHiImKi4yNjo+QkZKTlJWWl5iZmpucnZ6foKGio6SlpqeoqaqrrK2ur7CxsrO0tba3uLm6u7y9vr/AwcLDxMXGx8jJysvMzc7P0NHS09TV1tfY2drb3N3e3+Dh4uPk5ebn6Onq6+zt7u/w8fLz9PX29/j5+vv8/f7/AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+/wABAgMEBQYHCAkKCwwNDg8QERITFBUWFxgZGhscHR4fICEiIyQlJicoKSorLC0uLzAxMjM0NTY3ODk6Ozw9Pj9AQUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVpbXF1eX2BhYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ent8fX5/gIGCg4SFhoeIiYqLjI2Oj5CRkpOUlZaXmJmam5ydnp+goaKjpKWmp6ipqqusra6vsLGys7S1tre4ubq7vL2+v8DBwsPExcbHyMnKy8zNzs/Q0dLT1NXW19jZ2tvc3d7f4OHi4+Tl5ufo6err7O3u7/Dx8vP09fb3+Pn6+/z9/v8=";

            // Act
            Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", binaryBase64, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("validateValue - Performance Tests")
    class ValidateValuePerformanceTests {

        @Test
        @DisplayName("Should handle multiple Base64 validations efficiently")
        void shouldHandleMultipleBase64ValidationsEfficiently() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("fileContent")
                .description("File content")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String validBase64 = "SGVsbG8gV29ybGQh";
            int iterations = 1000;
            long startTime = System.currentTimeMillis();

            // Act
            for (int i = 0; i < iterations; i++) {
                Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("fileContent", validBase64, fieldMetadata);
                assertTrue(result.isEmpty());
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Assert
            assertTrue(duration < 1000, "Multiple Base64 validations should complete within 1 second");
        }

        @Test
        @DisplayName("Should handle large Base64 validations efficiently")
        void shouldHandleLargeBase64ValidationsEfficiently() {
            // Arrange
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("largeFile")
                .description("Large file")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            // Create Base64 content that decodes to 4MB (under 5MB limit)
            int targetSizeBytes = 4 * 1024 * 1024;
            int base64Size = (targetSizeBytes / 3) * 4 + (targetSizeBytes % 3 != 0 ? 4 : 0);
            
            StringBuilder base64Builder = new StringBuilder();
            for (int i = 0; i < base64Size; i++) {
                base64Builder.append("A");
            }
            String largeBase64 = base64Builder.toString();

            int iterations = 100;
            long startTime = System.currentTimeMillis();

            // Act
            for (int i = 0; i < iterations; i++) {
                Optional<FieldValidationError> result = base64FileFieldValidator.validateValue("largeFile", largeBase64, fieldMetadata);
                assertTrue(result.isEmpty());
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Assert
            assertTrue(duration < 5000, "Large Base64 validations should complete within 5 seconds");
        }
    }

    @Nested
    @DisplayName("Integration with AbstractFieldValidator")
    class IntegrationWithAbstractFieldValidatorTests {

        @Test
        @DisplayName("Should handle required field validation through abstract validator")
        void shouldHandleRequiredFieldValidationThroughAbstractValidator() {
            // Arrange
            Base64FileFieldValidator validator = new Base64FileFieldValidator();
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("requiredFile")
                .description("Required file")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "";

            // Act
            Optional<FieldValidationError> result = validator.validate("requiredFile", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_REQUIRED", result.get().getErrorCode());
            assertEquals("Field 'requiredFile' is required", result.get().getMessage());
        }

        @Test
        @DisplayName("Should handle non-required field validation through abstract validator")
        void shouldHandleNonRequiredFieldValidationThroughAbstractValidator() {
            // Arrange
            Base64FileFieldValidator validator = new Base64FileFieldValidator();
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("optionalFile")
                .description("Optional file")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(false)
                .fieldOrder(1)
                .build();

            String testValue = "";

            // Act
            Optional<FieldValidationError> result = validator.validate("optionalFile", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle null value for non-required field through abstract validator")
        void shouldHandleNullValueForNonRequiredFieldThroughAbstractValidator() {
            // Arrange
            Base64FileFieldValidator validator = new Base64FileFieldValidator();
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("optionalFile")
                .description("Optional file")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(false)
                .fieldOrder(1)
                .build();

            Object testValue = null;

            // Act
            Optional<FieldValidationError> result = validator.validate("optionalFile", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should handle null value for required field through abstract validator")
        void shouldHandleNullValueForRequiredFieldThroughAbstractValidator() {
            // Arrange
            Base64FileFieldValidator validator = new Base64FileFieldValidator();
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("requiredFile")
                .description("Required file")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            Object testValue = null;

            // Act
            Optional<FieldValidationError> result = validator.validate("requiredFile", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_REQUIRED", result.get().getErrorCode());
            assertEquals("Field 'requiredFile' is required", result.get().getMessage());
        }

        @Test
        @DisplayName("Should handle blank string for required field through abstract validator")
        void shouldHandleBlankStringForRequiredFieldThroughAbstractValidator() {
            // Arrange
            Base64FileFieldValidator validator = new Base64FileFieldValidator();
            DocRequestFieldMetadata fieldMetadata = DocRequestFieldMetadata.builder()
                .uuid("field-uuid")
                .docRequestMetadata(metadata)
                .name("requiredFile")
                .description("Required file")
                .type(DocRequestFieldType.FILE)
                .inputType(DocRequestFieldInputType.IN)
                .required(true)
                .fieldOrder(1)
                .build();

            String testValue = "   ";

            // Act
            Optional<FieldValidationError> result = validator.validate("requiredFile", testValue, fieldMetadata);

            // Assert
            assertTrue(result.isPresent());
            assertEquals("ERR_REQUIRED", result.get().getErrorCode());
            assertEquals("Field 'requiredFile' is required", result.get().getMessage());
        }
    }
}