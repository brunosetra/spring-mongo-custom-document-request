package br.com.docrequest.util;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.exception.ConversionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for DateFieldConverter utility.
 * Tests all conversion scenarios including edge cases, error handling, and different date types.
 */
class DateFieldConverterTest {

    private DocRequestMetadata metadata;
    private DocRequestFieldMetadata dateField;
    private DocRequestFieldMetadata dateTimeField;
    private DocRequestFieldMetadata expirationDateField;
    private DocRequestFieldMetadata nonDateField;

    @BeforeEach
    void setUp() {
        // Create test metadata with various date fields
        metadata = DocRequestMetadata.builder()
            .uuid("test-metadata-uuid")
            .name("Test Metadata")
            .description("Test metadata for date conversion")
            .build();

        // DATE field with dd/MM/yyyy format
        dateField = DocRequestFieldMetadata.builder()
            .uuid("date-field-uuid")
            .docRequestMetadata(metadata)
            .name("birthDate")
            .description("Date of birth")
            .type(DocRequestFieldType.DATE)
            .inputType(DocRequestFieldInputType.IN)
            .format("dd/MM/yyyy")
            .required(true)
            .fieldOrder(1)
            .build();

        // DATETIME field with dd/MM/yyyy HH:mm format
        dateTimeField = DocRequestFieldMetadata.builder()
            .uuid("datetime-field-uuid")
            .docRequestMetadata(metadata)
            .name("createdAt")
            .description("Creation timestamp")
            .type(DocRequestFieldType.DATETIME)
            .inputType(DocRequestFieldInputType.IN)
            .format("dd/MM/yyyy HH:mm")
            .required(true)
            .fieldOrder(2)
            .build();

        // EXPIRATION_DATE field with yyyy-MM-dd format
        expirationDateField = DocRequestFieldMetadata.builder()
            .uuid("expiration-field-uuid")
            .docRequestMetadata(metadata)
            .name("expirationDate")
            .description("Expiration date")
            .type(DocRequestFieldType.EXPIRATION_DATE)
            .inputType(DocRequestFieldInputType.IN)
            .format("yyyy-MM-dd")
            .required(true)
            .fieldOrder(3)
            .build();

        // Non-date field for comparison
        nonDateField = DocRequestFieldMetadata.builder()
            .uuid("string-field-uuid")
            .docRequestMetadata(metadata)
            .name("name")
            .description("Full name")
            .type(DocRequestFieldType.STRING)
            .inputType(DocRequestFieldInputType.IN)
            .required(true)
            .fieldOrder(4)
            .build();

        metadata.setFields(List.of(dateField, dateTimeField, expirationDateField, nonDateField));
    }

    @Nested
    @DisplayName("convertToIsoFormat - Positive Scenarios")
    class ConvertToIsoFormatPositiveTests {

        @Test
        @DisplayName("Should convert DATE field from dd/MM/yyyy to ISO format")
        void shouldConvertDateFieldToIsoFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "15/03/2023");
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals("2023-03-15", result.get("birthDate"));
            assertEquals("John Doe", result.get("name"));
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should convert DATETIME field from dd/MM/yyyy HH:mm to ISO format")
        void shouldConvertDateTimeFieldToIsoFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("createdAt", "15/03/2023 14:30");
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals("2023-03-15T14:30:00", result.get("createdAt"));
            assertEquals("John Doe", result.get("name"));
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should convert EXPIRATION_DATE field from yyyy-MM-dd to ISO format")
        void shouldConvertExpirationDateFieldToIsoFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("expirationDate", "2023-12-31");
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals("2023-12-31", result.get("expirationDate"));
            assertEquals("John Doe", result.get("name"));
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should handle multiple date fields simultaneously")
        void shouldConvertMultipleDateFieldsToIsoFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "15/03/1990");
            inputFields.put("createdAt", "15/03/2023 14:30");
            inputFields.put("expirationDate", "2023-12-31");
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals("1990-03-15", result.get("birthDate"));
            assertEquals("2023-03-15T14:30:00", result.get("createdAt"));
            assertEquals("2023-12-31", result.get("expirationDate"));
            assertEquals("John Doe", result.get("name"));
            assertEquals(4, result.size());
        }

        @Test
        @DisplayName("Should preserve non-date fields unchanged")
        void shouldPreserveNonDateFields() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "15/03/2023");
            inputFields.put("name", "John Doe");
            inputFields.put("age", 30);

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals("2023-03-15", result.get("birthDate"));
            assertEquals("John Doe", result.get("name"));
            assertEquals(30, result.get("age"));
            assertEquals(3, result.size());
        }

        @Test
        @DisplayName("Should handle leap year dates correctly")
        void shouldHandleLeapYearDates() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "29/02/2024"); // 2024 is a leap year
            inputFields.put("createdAt", "29/02/2024 12:00");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals("2024-02-29", result.get("birthDate"));
            assertEquals("2024-02-29T12:00:00", result.get("createdAt"));
        }

        @Test
        @DisplayName("Should handle month/day boundary values")
        void shouldHandleMonthDayBoundaries() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "31/01/2023"); // January has 31 days
            inputFields.put("createdAt", "28/02/2023 23:59"); // February non-leap year

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals("2023-01-31", result.get("birthDate"));
            assertEquals("2023-02-28T23:59:00", result.get("createdAt"));
        }
    }

    @Nested
    @DisplayName("convertToIsoFormat - Negative Scenarios")
    class ConvertToIsoFormatNegativeTests {

        @Test
        @DisplayName("Should throw ConversionException for invalid date format")
        void shouldThrowConversionExceptionForInvalidDateFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "2023/03/15"); // Wrong format
            inputFields.put("name", "John Doe");

            // Act & Assert
            assertThrows(ConversionException.class, () -> {
                DateFieldConverter.convertToIsoFormat(inputFields, metadata);
            });
        }

        @Test
        @DisplayName("Should throw ConversionException for invalid datetime format")
        void shouldThrowConversionExceptionForInvalidDateTimeFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("createdAt", "15-03-2023 14:30"); // Wrong format
            inputFields.put("name", "John Doe");

            // Act & Assert
            assertThrows(ConversionException.class, () -> {
                DateFieldConverter.convertToIsoFormat(inputFields, metadata);
            });
        }

        @Test
        @DisplayName("Should throw ConversionException for invalid time values")
        void shouldThrowConversionExceptionForInvalidTimeValues() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("createdAt", "15/03/2023 25:70"); // Invalid time
            inputFields.put("name", "John Doe");

            // Act & Assert
            assertThrows(ConversionException.class, () -> {
                DateFieldConverter.convertToIsoFormat(inputFields, metadata);
            });
        }


        @Test
        @DisplayName("Should preserve non-string date values unchanged")
        void shouldPreserveNonStringDateValues() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", 20230315); // Integer instead of string
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals(20230315, result.get("birthDate")); // Should remain unchanged
            assertEquals("John Doe", result.get("name"));
            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("convertToTemplateFormat - Positive Scenarios")
    class ConvertToTemplateFormatPositiveTests {

        @Test
        @DisplayName("Should convert DATE field from ISO to dd/MM/yyyy format")
        void shouldConvertDateFieldToTemplateFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "2023-03-15");
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToTemplateFormat(inputFields, metadata);

            // Assert
            assertEquals("15/03/2023", result.get("birthDate"));
            assertEquals("John Doe", result.get("name"));
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should convert DATETIME field from ISO to dd/MM/yyyy HH:mm format")
        void shouldConvertDateTimeFieldToTemplateFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("createdAt", "2023-03-15T14:30:00");
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToTemplateFormat(inputFields, metadata);

            // Assert
            assertEquals("15/03/2023 14:30", result.get("createdAt"));
            assertEquals("John Doe", result.get("name"));
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should convert EXPIRATION_DATE field from ISO to yyyy-MM-dd format")
        void shouldConvertExpirationDateFieldToTemplateFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("expirationDate", "2023-12-31");
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToTemplateFormat(inputFields, metadata);

            // Assert
            assertEquals("2023-12-31", result.get("expirationDate"));
            assertEquals("John Doe", result.get("name"));
            assertEquals(2, result.size());
        }

        @Test
        @DisplayName("Should handle multiple date fields simultaneously")
        void shouldConvertMultipleDateFieldsToTemplateFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "1990-03-15");
            inputFields.put("createdAt", "2023-03-15T14:30:00");
            inputFields.put("expirationDate", "2023-12-31");
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToTemplateFormat(inputFields, metadata);

            // Assert
            assertEquals("15/03/1990", result.get("birthDate"));
            assertEquals("15/03/2023 14:30", result.get("createdAt"));
            assertEquals("2023-12-31", result.get("expirationDate"));
            assertEquals("John Doe", result.get("name"));
            assertEquals(4, result.size());
        }

        @Test
        @DisplayName("Should preserve non-date fields unchanged")
        void shouldPreserveNonDateFieldsInTemplateFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "2023-03-15");
            inputFields.put("name", "John Doe");
            inputFields.put("age", 30);

            // Act
            Map<String, Object> result = DateFieldConverter.convertToTemplateFormat(inputFields, metadata);

            // Assert
            assertEquals("15/03/2023", result.get("birthDate"));
            assertEquals("John Doe", result.get("name"));
            assertEquals(30, result.get("age"));
            assertEquals(3, result.size());
        }
    }

    @Nested
    @DisplayName("convertToTemplateFormat - Negative Scenarios")
    class ConvertToTemplateFormatNegativeTests {

        @Test
        @DisplayName("Should throw ConversionException for invalid ISO date format")
        void shouldThrowConversionExceptionForInvalidIsoDateFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "2023/03/15"); // Wrong ISO format
            inputFields.put("name", "John Doe");

            // Act & Assert
            assertThrows(ConversionException.class, () -> {
                DateFieldConverter.convertToTemplateFormat(inputFields, metadata);
            });
        }

        @Test
        @DisplayName("Should throw ConversionException for non-existent date in ISO format")
        void shouldThrowConversionExceptionForNonExistentDateInIsoFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "2023-04-31"); // April has 30 days
            inputFields.put("name", "John Doe");

            // Act & Assert
            assertThrows(ConversionException.class, () -> {
                DateFieldConverter.convertToTemplateFormat(inputFields, metadata);
            });
        }

        @Test
        @DisplayName("Should throw ConversionException for non-string ISO date values")
        void shouldThrowConversionExceptionForNonStringIsoDateValues() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", LocalDate.of(2023, 3, 15)); // LocalDate object
            inputFields.put("name", "John Doe");

            // Act & Assert
            assertThrows(ConversionException.class, () -> {
                DateFieldConverter.convertToTemplateFormat(inputFields, metadata);
            });
        }

        @ParameterizedTest
        @CsvSource({
            "invalid-date-string"
        })
        @DisplayName("Should throw ConversionException for invalid ISO date values")
        void shouldThrowConversionExceptionForInvalidIsoDateValues(String invalidValue) {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "null".equals(invalidValue) ? null : invalidValue);
            inputFields.put("name", "John Doe");

            // Act & Assert
            assertThrows(ConversionException.class, () -> {
                DateFieldConverter.convertToTemplateFormat(inputFields, metadata);
            });
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Conditions")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle year 2000 transition (leap year)")
        void shouldHandleYear2000Transition() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "29/02/2000"); // Year 2000 was a leap year
            inputFields.put("createdAt", "01/01/2000 00:00");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals("2000-02-29", result.get("birthDate"));
            assertEquals("2000-01-01T00:00:00", result.get("createdAt"));
        }

        @Test
        @DisplayName("Should handle year 1900 transition (not a leap year)")
        void shouldHandleYear1900Transition() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "28/02/1900"); // Year 1900 was not a leap year
            inputFields.put("createdAt", "01/01/1900 23:59");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals("1900-02-28", result.get("birthDate"));
            assertEquals("1900-01-01T23:59:00", result.get("createdAt"));
        }

        @Test
        @DisplayName("Should handle minimum and maximum valid dates")
        void shouldHandleMinAndMaxValidDates() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "01/01/0001"); // Minimum year
            inputFields.put("createdAt", "31/12/9999 23:59"); // Maximum year

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals("0001-01-01", result.get("birthDate"));
            assertEquals("9999-12-31T23:59:00", result.get("createdAt"));
        }

        @Test
        @DisplayName("Should handle timezone conversion scenarios")
        void shouldHandleTimezoneScenarios() {
            // This test verifies that the conversion works correctly regardless of timezone
            // Since we're using LocalDate and LocalDateTime, timezone is handled appropriately
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "15/03/2023");
            inputFields.put("createdAt", "15/03/2023 14:30");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert - Verify the conversion is timezone-agnostic
            assertEquals("2023-03-15", result.get("birthDate"));
            assertEquals("2023-03-15T14:30:00", result.get("createdAt"));
        }
    }

    @Nested
    @DisplayName("Null and Empty Input Handling")
    class NullAndEmptyInputTests {

        @Test
        @DisplayName("Should return null fields map when input fields is null")
        void shouldReturnNullWhenInputFieldsIsNull() {
            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(null, metadata);

            // Assert
            assertNull(result);
        }

        @Test
        @DisplayName("Should return empty map when input fields is empty")
        void shouldReturnEmptyMapWhenInputFieldsIsEmpty() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should return null when metadata is null")
        void shouldReturnNullWhenMetadataIsNull() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", "15/03/2023");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, null);

            // Assert
            assertEquals(inputFields, result); // Should return original fields unchanged
        }

        @Test
        @DisplayName("Should return empty map when metadata has no date fields")
        void shouldReturnEmptyMapWhenMetadataHasNoDateFields() {
            // Arrange
            DocRequestMetadata metadataWithoutDateFields = DocRequestMetadata.builder()
                .uuid("test-metadata-uuid")
                .name("Test Metadata")
                .description("Test metadata without date fields")
                .fields(List.of(nonDateField))
                .build();

            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadataWithoutDateFields);

            // Assert
            assertEquals(1, result.size());
            assertEquals("John Doe", result.get("name"));
        }
    }

    @Nested
    @DisplayName("Parameterized Tests")
    class ParameterizedTests {

        @ParameterizedTest
        @CsvSource({
            "01/01/2023, 2023-01-01",
            "31/12/2023, 2023-12-31",
            "29/02/2024, 2024-02-29", // Leap year
            "28/02/2023, 2023-02-28", // Non-leap year
            "15/06/2023, 2023-06-15"
        })
        @DisplayName("Should convert various valid date formats to ISO")
        void shouldConvertVariousValidDateFormatsToIso(String inputDate, String expectedIsoDate) {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", inputDate);
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals(expectedIsoDate, result.get("birthDate"));
            assertEquals("John Doe", result.get("name"));
        }

        @ParameterizedTest
        @CsvSource({
            "2023-01-01, 01/01/2023",
            "2023-12-31, 31/12/2023",
            "2024-02-29, 29/02/2024", // Leap year
            "2023-02-28, 28/02/2023", // Non-leap year
            "2023-06-15, 15/06/2023"
        })
        @DisplayName("Should convert various valid ISO dates to template format")
        void shouldConvertVariousValidIsoDatesToTemplate(String inputIsoDate, String expectedTemplateDate) {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", inputIsoDate);
            inputFields.put("name", "John Doe");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToTemplateFormat(inputFields, metadata);

            // Assert
            assertEquals(expectedTemplateDate, result.get("birthDate"));
            assertEquals("John Doe", result.get("name"));
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "32/01/2023",   // Invalid day
            "01/13/2023",   // Invalid month
            "15/13/2023",   // Invalid month
            "2023-13-01",   // Invalid month in ISO
            "2023-01-32",   // Invalid day in ISO
            "2023-02-29",   // Invalid leap year in ISO
            "2023-04-31",   // Invalid day for April in ISO
            "invalid-date",  // Completely invalid format
            "",             // Empty string
            "15/03/23",     // Two-digit year
            "15-03-2023",   // Wrong separator
            "2023/03/15",   // Wrong format
            "15.03.2023"    // Wrong separator
        })
        @DisplayName("Should handle various invalid date formats")
        void shouldHandleVariousInvalidDateFormats(String invalidDate) {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("birthDate", invalidDate);
            inputFields.put("name", "John Doe");

            // Act
            assertThrows(ConversionException.class, () -> {
            DateFieldConverter.convertToIsoFormat(inputFields, metadata);
            });

            // Assert
        }
    }

    @Nested
    @DisplayName("Different Date Format Tests")
    class DifferentDateFormatTests {

        @BeforeEach
        void setUpDifferentFormats() {
            // Create metadata with different date formats
            DocRequestMetadata differentFormatMetadata = DocRequestMetadata.builder()
                .uuid("different-format-metadata-uuid")
                .name("Different Format Metadata")
                .description("Metadata with different date formats")
                .build();

            DocRequestFieldMetadata usDateFormat = DocRequestFieldMetadata.builder()
                .uuid("us-date-field-uuid")
                .docRequestMetadata(differentFormatMetadata)
                .name("usDate")
                .description("US format date")
                .type(DocRequestFieldType.DATE)
                .inputType(DocRequestFieldInputType.IN)
                .format("MM/dd/yyyy")
                .required(true)
                .fieldOrder(1)
                .build();

            DocRequestFieldMetadata isoDateFormat = DocRequestFieldMetadata.builder()
                .uuid("iso-date-field-uuid")
                .docRequestMetadata(differentFormatMetadata)
                .name("isoDate")
                .description("ISO format date")
                .type(DocRequestFieldType.DATE)
                .inputType(DocRequestFieldInputType.IN)
                .format("yyyy-MM-dd")
                .required(true)
                .fieldOrder(2)
                .build();

            DocRequestFieldMetadata europeanDateFormat = DocRequestFieldMetadata.builder()
                .uuid("european-date-field-uuid")
                .docRequestMetadata(differentFormatMetadata)
                .name("europeanDate")
                .description("European format date")
                .type(DocRequestFieldType.DATE)
                .inputType(DocRequestFieldInputType.IN)
                .format("dd.MM.yyyy")
                .required(true)
                .fieldOrder(3)
                .build();

            differentFormatMetadata.setFields(List.of(usDateFormat, isoDateFormat, europeanDateFormat));
            metadata = differentFormatMetadata;
        }

        @Test
        @DisplayName("Should handle US date format (MM/dd/yyyy)")
        void shouldHandleUsDateFormat() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("usDate", "03/15/2023");
            inputFields.put("isoDate", "2023-03-15");
            inputFields.put("europeanDate", "15.03.2023");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToIsoFormat(inputFields, metadata);

            // Assert
            assertEquals("2023-03-15", result.get("usDate"));
            assertEquals("2023-03-15", result.get("isoDate"));
            assertEquals("2023-03-15", result.get("europeanDate"));
        }

        @Test
        @DisplayName("Should convert back to original formats from ISO")
        void shouldConvertBackToOriginalFormats() {
            // Arrange
            Map<String, Object> inputFields = new HashMap<>();
            inputFields.put("usDate", "2023-03-15");
            inputFields.put("isoDate", "2023-03-15");
            inputFields.put("europeanDate", "2023-03-15");

            // Act
            Map<String, Object> result = DateFieldConverter.convertToTemplateFormat(inputFields, metadata);

            // Assert
            assertEquals("03/15/2023", result.get("usDate"));
            assertEquals("2023-03-15", result.get("isoDate"));
            assertEquals("15.03.2023", result.get("europeanDate"));
        }
    }
}