package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.MetadataValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TypeInputTypeCompatibilityValidator.
 * Tests the validation of AUTO_INC input type compatibility with field types.
 */
@ExtendWith(MockitoExtension.class)
class TypeInputTypeCompatibilityValidatorTest {

    private TypeInputTypeCompatibilityValidator validator;
    private DocRequestMetadata metadata;

    @BeforeEach
    void setUp() {
        validator = new TypeInputTypeCompatibilityValidator();
        metadata = new DocRequestMetadata();
    }

    @Test
    void testAutoIncWithIntegerType() {
        // Valid case: AUTO_INC with INTEGER type
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        metadata.setFields(List.of(field));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertTrue(errors.isEmpty(), "AUTO_INC with INTEGER should be valid");
    }

    @Test
    void testAutoIncWithStringType() {
        // Invalid case: AUTO_INC with STRING type
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("invalid_field")
            .type(DocRequestFieldType.STRING)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        metadata.setFields(List.of(field));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertFalse(errors.isEmpty(), "AUTO_INC with STRING should be invalid");
        
        MetadataValidationError error = errors.get(0);
        assertEquals("invalid_field", error.getFieldName());
        assertTrue(error.getMessage().contains("AUTO_INC must have type INTEGER"));
    }

    @Test
    void testAutoIncWithDoubleType() {
        // Invalid case: AUTO_INC with DOUBLE type
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("invalid_field")
            .type(DocRequestFieldType.DOUBLE)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        metadata.setFields(List.of(field));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertFalse(errors.isEmpty(), "AUTO_INC with DOUBLE should be invalid");
        
        MetadataValidationError error = errors.get(0);
        assertEquals("invalid_field", error.getFieldName());
        assertTrue(error.getMessage().contains("AUTO_INC must have type INTEGER"));
    }

    @Test
    void testAutoIncWithDateType() {
        // Invalid case: AUTO_INC with DATE type
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("invalid_field")
            .type(DocRequestFieldType.DATE)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        metadata.setFields(List.of(field));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertFalse(errors.isEmpty(), "AUTO_INC with DATE should be invalid");
        
        MetadataValidationError error = errors.get(0);
        assertEquals("invalid_field", error.getFieldName());
        assertTrue(error.getMessage().contains("AUTO_INC must have type INTEGER"));
    }

    @Test
    void testAutoIncWithListType() {
        // Invalid case: AUTO_INC with LIST_INT type
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("invalid_field")
            .type(DocRequestFieldType.LIST_INT)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        metadata.setFields(List.of(field));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertFalse(errors.isEmpty(), "AUTO_INC with LIST_INT should be invalid");
        
        MetadataValidationError error = errors.get(0);
        assertEquals("invalid_field", error.getFieldName());
        assertTrue(error.getMessage().contains("AUTO_INC must have type INTEGER"));
    }

    @Test
    void testAutoIncWithBooleanType() {
        // Invalid case: AUTO_INC with BOOLEAN type
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("invalid_field")
            .type(DocRequestFieldType.BOOLEAN)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        metadata.setFields(List.of(field));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertFalse(errors.isEmpty(), "AUTO_INC with BOOLEAN should be invalid");
        
        MetadataValidationError error = errors.get(0);
        assertEquals("invalid_field", error.getFieldName());
        assertTrue(error.getMessage().contains("AUTO_INC must have type INTEGER"));
    }

    @Test
    void testAutoIncWithCpfType() {
        // Invalid case: AUTO_INC with CPF type
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("invalid_field")
            .type(DocRequestFieldType.BOOLEAN)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        metadata.setFields(List.of(field));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertFalse(errors.isEmpty(), "AUTO_INC with BOOLEAN should be invalid");
        
        MetadataValidationError error = errors.get(0);
        assertEquals("invalid_field", error.getFieldName());
        assertTrue(error.getMessage().contains("AUTO_INC must have type INTEGER"));
    }

    @Test
    void testAutoIncWithValidAndInvalidFields() {
        // Test metadata with both valid and invalid fields
        DocRequestFieldMetadata validField = DocRequestFieldMetadata.builder()
            .name("valid_field")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        DocRequestFieldMetadata invalidField = DocRequestFieldMetadata.builder()
            .name("invalid_field")
            .type(DocRequestFieldType.STRING)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        metadata.setFields(List.of(validField, invalidField));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertEquals(1, errors.size(), "Should have exactly 1 error for invalid field");
        
        MetadataValidationError error = errors.get(0);
        assertEquals("invalid_field", error.getFieldName());
        assertTrue(error.getMessage().contains("AUTO_INC must have type INTEGER"));
    }

    @Test
    void testAutoIncWithMultipleInvalidFields() {
        // Test metadata with multiple invalid AUTO_INC fields
        DocRequestFieldMetadata invalidField1 = DocRequestFieldMetadata.builder()
            .name("invalid_field_1")
            .type(DocRequestFieldType.STRING)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        DocRequestFieldMetadata invalidField2 = DocRequestFieldMetadata.builder()
            .name("invalid_field_2")
            .type(DocRequestFieldType.DOUBLE)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        metadata.setFields(List.of(invalidField1, invalidField2));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertEquals(2, errors.size(), "Should have exactly 2 errors for invalid fields");
        
        // Check both errors are present
        assertTrue(errors.stream().anyMatch(e -> e.getFieldName().equals("invalid_field_1")));
        assertTrue(errors.stream().anyMatch(e -> e.getFieldName().equals("invalid_field_2")));
    }

    @Test
    void testAutoIncWithRequiredField() {
        // Invalid case: AUTO_INC field marked as required
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("invalid_field")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(true)
            .build();

        metadata.setFields(List.of(field));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertFalse(errors.isEmpty(), "AUTO_INC with required should be invalid");
        
        MetadataValidationError error = errors.get(0);
        assertEquals("invalid_field", error.getFieldName());
        assertTrue(error.getMessage().contains("AUTO_INC cannot be required"));
    }

    @Test
    void testAutoIncWithEditableField() {
        // Invalid case: AUTO_INC field marked as editable
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("invalid_field")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .editable(true)
            .build();

        metadata.setFields(List.of(field));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertFalse(errors.isEmpty(), "AUTO_INC with editable should be invalid");
        
        MetadataValidationError error = errors.get(0);
        assertEquals("invalid_field", error.getFieldName());
        assertTrue(error.getMessage().contains("AUTO_INC cannot be editable"));
    }

    @Test
    void testAutoIncWithRequiredAndEditable() {
        // Invalid case: AUTO_INC field marked as both required and editable
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("invalid_field")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(true)
            .editable(true)
            .build();

        metadata.setFields(List.of(field));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertEquals(2, errors.size(), "Should have exactly 2 errors for required and editable");
        
        // Check both errors are present
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("AUTO_INC cannot be required")));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("AUTO_INC cannot be editable")));
    }

    @Test
    void testAutoIncWithValidAndInvalidBusinessRules() {
        // Test metadata with valid type but invalid business rules
        DocRequestFieldMetadata validTypeField = DocRequestFieldMetadata.builder()
            .name("valid_type_field")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(false)
            .build();

        DocRequestFieldMetadata invalidRequiredField = DocRequestFieldMetadata.builder()
            .name("invalid_required_field")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(true)
            .editable(false)
            .build();

        DocRequestFieldMetadata invalidEditableField = DocRequestFieldMetadata.builder()
            .name("invalid_editable_field")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .required(false)
            .editable(true)
            .build();

        metadata.setFields(List.of(validTypeField, invalidRequiredField, invalidEditableField));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertEquals(2, errors.size(), "Should have exactly 2 errors for invalid business rules");
        
        // Check both business rule errors are present
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("AUTO_INC cannot be required")));
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().contains("AUTO_INC cannot be editable")));
    }

    @Test
    void testAutoIncWithOtherValidInputTypes() {
        // Test that other input types work normally with INTEGER
        DocRequestFieldMetadata inField = DocRequestFieldMetadata.builder()
            .name("in_field")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.IN)
            .build();

        DocRequestFieldMetadata fixedField = DocRequestFieldMetadata.builder()
            .name("fixed_field")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.FIXED)
            .build();

        DocRequestFieldMetadata defaultField = DocRequestFieldMetadata.builder()
            .name("default_field")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.DEFAULT)
            .build();

        metadata.setFields(List.of(inField, fixedField, defaultField));
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertTrue(errors.isEmpty(), "Other input types with INTEGER should be valid");
    }

    @Test
    void testValidatorOrder() {
        // Test that the validator has the correct order
        assertEquals(40, validator.getOrder(), "TypeInputTypeCompatibilityValidator should have order 40");
    }

    @Test
    void testEmptyMetadata() {
        // Test validation with empty metadata
        metadata.setFields(List.of());
        
        List<MetadataValidationError> errors = validator.validate(metadata);
        assertTrue(errors.isEmpty(), "Empty metadata should have no validation errors");
    }

}