package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.repository.mongo.DocRequestRepository;
import br.com.docrequest.security.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UniqueFieldValueValidator
 */
@ExtendWith(MockitoExtension.class)
class UniqueFieldValueValidatorTest {

    @Mock
    private DocRequestRepository docRequestRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private UniqueFieldValueValidator validator;

    private DocRequestFieldMetadata fieldMetadata;
    private DocRequestMetadata metadata;

    @BeforeEach
    void setUp() {
        metadata = DocRequestMetadata.builder()
            .name("student")
            .version(1)
            .enabled(true)
            .build();

        fieldMetadata = DocRequestFieldMetadata.builder()
            .name("social-security-number")
            .type(DocRequestFieldType.STRING)
            .inputType(DocRequestFieldInputType.IN)
            .unique(true)
            .docRequestMetadata(metadata)
            .build();
    }

    @Test
    void validate_WhenFieldIsNotUnique_ShouldReturnEmpty() {
        fieldMetadata.setUnique(false);
        
        Optional<FieldValidationError> result = validator.validate("social-security-number", "12345", fieldMetadata);
        
        assertTrue(result.isEmpty());
        verifyNoInteractions(docRequestRepository);
        verifyNoInteractions(mongoTemplate);
    }

    @Test
    void validate_WhenValueIsNull_ShouldReturnEmpty() {
        Optional<FieldValidationError> result = validator.validate("social-security-number", null, fieldMetadata);
        
        assertTrue(result.isEmpty());
        verifyNoInteractions(docRequestRepository);
        verifyNoInteractions(mongoTemplate);
    }

    @Test
    void validate_WhenValueDoesNotExist_ShouldReturnEmpty() {
        try (MockedStatic<TenantContext> mockedTenant = mockStatic(TenantContext.class)) {
            mockedTenant.when(TenantContext::getCurrentTenant).thenReturn("tenant-123");
            
            when(mongoTemplate.exists(any(Query.class), eq("doc_requests"))).thenReturn(false);
            
            Optional<FieldValidationError> result = validator.validate("social-security-number", "12345", fieldMetadata);
            
            assertTrue(result.isEmpty());
            verify(mongoTemplate).exists(any(Query.class), eq("doc_requests"));
        }
    }

    @Test
    void validate_WhenValueExists_ShouldReturnValidationError() {
        try (MockedStatic<TenantContext> mockedTenant = mockStatic(TenantContext.class)) {
            mockedTenant.when(TenantContext::getCurrentTenant).thenReturn("tenant-123");
            
            when(mongoTemplate.exists(any(Query.class), eq("doc_requests"))).thenReturn(true);
            
            Optional<FieldValidationError> result = validator.validate("social-security-number", "12345", fieldMetadata);
            
            assertTrue(result.isPresent());
            assertEquals("ERR_UNIQUE_FIELD_VIOLATION", result.get().getErrorCode());
            assertEquals("Field 'social-security-number' must have a unique value. The value '12345' already exists.", 
                        result.get().getMessage());
            assertEquals("12345", result.get().getRejectedValue());
            
            verify(mongoTemplate).exists(any(Query.class), eq("doc_requests"));
        }
    }

    @Test
    void validate_WhenMongoQueryFails_ShouldFallbackToRepository() {
        try (MockedStatic<TenantContext> mockedTenant = mockStatic(TenantContext.class)) {
            mockedTenant.when(TenantContext::getCurrentTenant).thenReturn("tenant-123");
            
            when(mongoTemplate.exists(any(Query.class), eq("doc_requests"))).thenThrow(new RuntimeException("MongoDB error"));
            // when(docRequestRepository.existsByPartIdAndDocRequestMetadataNameAndFieldsContainingValue(
            //     eq("tenant-123"), eq("student"), eq("social-security-number"), eq("12345"))).thenReturn(true);
            
            Optional<FieldValidationError> result = validator.validate("social-security-number", "12345", fieldMetadata);
            
            assertTrue(result.isPresent());
            assertEquals("ERR_UNIQUE_FIELD_VALIDATION_FAILED", result.get().getErrorCode());
            
            verify(mongoTemplate).exists(any(Query.class), eq("doc_requests"));
            // verify(docRequestRepository).existsByPartIdAndDocRequestMetadataNameAndFieldsContainingValue(
            //     eq("tenant-123"), eq("student"), eq("social-security-number"), eq("12345"));
        }
    }

    @Test
    void getType_ShouldReturnStringType() {
        assertEquals(DocRequestFieldType.STRING, validator.getType());
    }

}