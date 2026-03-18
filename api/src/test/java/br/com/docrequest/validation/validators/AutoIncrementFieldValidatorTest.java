package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.document.DocRequest;
import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.security.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AutoIncrementFieldValidator.
 * Tests the auto-increment value generation and validation logic.
 */
@ExtendWith(MockitoExtension.class)
class AutoIncrementFieldValidatorTest {

    @Mock
    private MongoTemplate mongoTemplate;

    private AutoIncrementFieldValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AutoIncrementFieldValidator(mongoTemplate);
        
        // Set up tenant context for testing
        TenantContext.setCurrentTenant("test-tenant");
    }

    @Test
    void testGetType() {
        assertEquals(DocRequestFieldType.INTEGER, validator.getType());
    }

    @Test
    void testValidateValueForAutoIncField() {
        // Test that validateValue returns empty for AUTO_INC fields (system-managed)
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .build();

        Optional<FieldValidationError> result = validator.validateValue("sequence_number", null, field);
        assertTrue(result.isEmpty(), "AUTO_INC fields should not validate user-provided values");
    }

    @Test
    void testGenerateFirstValue() {
        // Test generating the first auto-increment value when no documents exist
        when(mongoTemplate.findOne(any(Query.class), any(Class.class))).thenReturn(null);

        DocRequestMetadata metadata = DocRequestMetadata.builder()
            .name("test_metadata")
            .build();

        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .docRequestMetadata(metadata)
            .build();

        Long result = validator.generateAutoIncrementValue(field);
        assertEquals(1L, result, "First value should be 1");
        
        // Verify the query was constructed correctly
        verify(mongoTemplate).findOne(any(Query.class), any(Class.class));
    }

    @Test
    void testGenerateSequentialValues() {
        // Test generating sequential values
        DocRequest existingDoc = new DocRequest();
        Map<String, Object> fields = Map.of("sequence_number", 5);
        existingDoc.setFields(fields);

        Query query = new Query(Criteria.where("partId").is("test-tenant")
            .and("docRequestMetadataName").is("test_metadata")
            .and("fields.sequence_number").exists(true));
        query.fields().include("fields.sequence_number");
        query.with(Sort.by(Sort.Direction.DESC, "fields.sequence_number"));
        query.limit(1);

        when(mongoTemplate.findOne(query, DocRequest.class)).thenReturn(existingDoc);

        DocRequestMetadata metadata = DocRequestMetadata.builder()
            .name("test_metadata")
            .build();

        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .docRequestMetadata(metadata)
            .build();

        Long result = validator.generateAutoIncrementValue(field);
        assertEquals(6L, result, "Next value should be 6 (5 + 1)");
    }

    @Test
    void testGenerateValueWithNonNumericField() {
        // Test handling of non-numeric field values
        DocRequest existingDoc = new DocRequest();
        Map<String, Object> fields = Map.of("sequence_number", "not-a-number");
        existingDoc.setFields(fields);

        Query query = new Query(Criteria.where("partId").is("test-tenant")
            .and("docRequestMetadataName").is("test_metadata")
            .and("fields.sequence_number").exists(true));
        query.fields().include("fields.sequence_number");
        query.with(Sort.by(Sort.Direction.DESC, "fields.sequence_number"));
        query.limit(1);

        when(mongoTemplate.findOne(query, DocRequest.class)).thenReturn(existingDoc);

        DocRequestMetadata metadata = DocRequestMetadata.builder()
            .name("test_metadata")
            .build();

        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .docRequestMetadata(metadata)
            .build();

        Long result = validator.generateAutoIncrementValue(field);
        assertEquals(1L, result, "Should fallback to 1 for non-numeric values");
    }

    @Test
    void testGenerateValueWithDoubleField() {
        // Test handling of double field values
        DocRequest existingDoc = new DocRequest();
        Map<String, Object> fields = Map.of("sequence_number", 5.7);
        existingDoc.setFields(fields);

        Query query = new Query(Criteria.where("partId").is("test-tenant")
            .and("docRequestMetadataName").is("test_metadata")
            .and("fields.sequence_number").exists(true));
        query.fields().include("fields.sequence_number");
        query.with(Sort.by(Sort.Direction.DESC, "fields.sequence_number"));
        query.limit(1);

        when(mongoTemplate.findOne(query, DocRequest.class)).thenReturn(existingDoc);

        DocRequestMetadata metadata = DocRequestMetadata.builder()
            .name("test_metadata")
            .build();

        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .docRequestMetadata(metadata)
            .build();

        Long result = validator.generateAutoIncrementValue(field);
        assertEquals(6L, result, "Should handle double values correctly");
    }

    @Test
    void testValidateGeneratedValueValid() {
        // Test validation of a valid generated value
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .build();

        Optional<FieldValidationError> result = validator.validateGeneratedValue(100L, field);
        assertTrue(result.isEmpty(), "Valid value should not produce error");
    }

    @Test
    void testValidateGeneratedValueNull() {
        // Test validation of null generated value
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .build();

        Optional<FieldValidationError> result = validator.validateGeneratedValue(null, field);
        assertTrue(result.isPresent(), "Null value should produce error");
        assertEquals("ERR_AUTO_INCREMENT_NULL", result.get().getErrorCode());
        assertEquals("Auto-increment value cannot be null", result.get().getMessage());
    }

    @Test
    void testValidateGeneratedValueTooLarge() {
        // Test validation of value that exceeds Integer.MAX_VALUE
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .build();

        Optional<FieldValidationError> result = validator.validateGeneratedValue((long) Integer.MAX_VALUE + 1, field);
        assertTrue(result.isPresent(), "Value exceeding Integer.MAX_VALUE should produce error");
        assertEquals("ERR_AUTO_INCREMENT_RANGE", result.get().getErrorCode());
        assertTrue(result.get().getMessage().contains("outside valid integer range"));
    }

    @Test
    void testValidateGeneratedValueTooSmall() {
        // Test validation of value that is below Integer.MIN_VALUE
        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .build();

        Optional<FieldValidationError> result = validator.validateGeneratedValue((long) Integer.MIN_VALUE - 1, field);
        assertTrue(result.isPresent(), "Value below Integer.MIN_VALUE should produce error");
        assertEquals("ERR_AUTO_INCREMENT_RANGE", result.get().getErrorCode());
        assertTrue(result.get().getMessage().contains("outside valid integer range"));
    }

    @Test
    void testGenerateValueWithDatabaseError() {
        // Test handling of database errors
        when(mongoTemplate.findOne(any(Query.class), any(Class.class)))
            .thenThrow(new RuntimeException("Database connection failed"));

        DocRequestMetadata metadata = DocRequestMetadata.builder()
            .name("test_metadata")
            .build();

        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .docRequestMetadata(metadata)
            .build();

        assertThrows(RuntimeException.class, () -> {
            validator.generateAutoIncrementValue(field);
        }, "Should throw exception when database fails");
    }

    @Test
    void testGenerateValueWithTenantIsolation() {
        // Test that values are properly tenant-isolated
        DocRequest existingDoc = new DocRequest();
        Map<String, Object> fields = Map.of("sequence_number", 10);
        existingDoc.setFields(fields);

        Query query = new Query(Criteria.where("partId").is("test-tenant")
            .and("docRequestMetadataName").is("test_metadata")
            .and("fields.sequence_number").exists(true));
        query.fields().include("fields.sequence_number");
        query.with(Sort.by(Sort.Direction.DESC, "fields.sequence_number"));
        query.limit(1);

        when(mongoTemplate.findOne(query, DocRequest.class)).thenReturn(existingDoc);

        DocRequestMetadata metadata = DocRequestMetadata.builder()
            .name("test_metadata")
            .build();

        DocRequestFieldMetadata field = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .docRequestMetadata(metadata)
            .build();

        Long result = validator.generateAutoIncrementValue(field);
        assertEquals(11L, result, "Should generate next value for the same tenant");
    }

    @Test
    void testGenerateValueWithTemplateIsolation() {
        // Test that values are properly template-isolated
        DocRequest existingDoc1 = new DocRequest();
        Map<String, Object> fields1 = Map.of("sequence_number", 5);
        existingDoc1.setFields(fields1);

        Query query1 = new Query(Criteria.where("partId").is("test-tenant")
            .and("docRequestMetadataName").is("template1")
            .and("fields.sequence_number").exists(true));
        query1.fields().include("fields.sequence_number");
        query1.with(Sort.by(Sort.Direction.DESC, "fields.sequence_number"));
        query1.limit(1);

        when(mongoTemplate.findOne(query1, DocRequest.class)).thenReturn(existingDoc1);

        DocRequestMetadata metadata1 = DocRequestMetadata.builder()
            .name("template1")
            .build();

        DocRequestFieldMetadata field1 = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .docRequestMetadata(metadata1)
            .build();

        Long result1 = validator.generateAutoIncrementValue(field1);
        assertEquals(6L, result1, "Should generate next value for template1");

        // Different template should have different sequence
        DocRequest existingDoc2 = new DocRequest();
        Map<String, Object> fields2 = Map.of("sequence_number", 1);
        existingDoc2.setFields(fields2);

        Query query2 = new Query(Criteria.where("partId").is("test-tenant")
            .and("docRequestMetadataName").is("template2")
            .and("fields.sequence_number").exists(true));
        query2.fields().include("fields.sequence_number");
        query2.with(Sort.by(Sort.Direction.DESC, "fields.sequence_number"));
        query2.limit(1);

        when(mongoTemplate.findOne(query2, DocRequest.class)).thenReturn(existingDoc2);

        DocRequestMetadata metadata2 = DocRequestMetadata.builder()
            .name("template2")
            .build();

        DocRequestFieldMetadata field2 = DocRequestFieldMetadata.builder()
            .name("sequence_number")
            .type(DocRequestFieldType.INTEGER)
            .inputType(DocRequestFieldInputType.AUTO_INC)
            .docRequestMetadata(metadata2)
            .build();

        Long result2 = validator.generateAutoIncrementValue(field2);
        assertEquals(2L, result2, "Should generate next value for template2");
    }
}