package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.document.DocRequest;
import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validator for AUTO_INC input type fields.
 * Handles the generation of auto-increment integer values that are unique
 * within the same tenant and metadata template.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AutoIncrementFieldValidator extends AbstractFieldValidator {

    private final MongoTemplate mongoTemplate;

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.INTEGER;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                        DocRequestFieldMetadata metadata) {
        // AUTO_INC fields are system-managed, so user-provided values are ignored
        // The actual value generation happens in the DocRequestValidationEngine
        return Optional.empty();
    }

    /**
     * Generates the next auto-increment value for a field.
     * 
     * @param fieldMetadata the field metadata
     * @return the next auto-increment value
     */
    public Long generateAutoIncrementValue(DocRequestFieldMetadata fieldMetadata) {
        DocRequestMetadata metadata = fieldMetadata.getDocRequestMetadata();
        String fieldName = fieldMetadata.getName();
        
        try {
            String tenantId = br.com.docrequest.security.TenantContext.getCurrentTenant();
            
            // Query for the maximum current value for this field
            Query query = new Query(Criteria.where("partId").is(tenantId)
                .and("docRequestMetadataName").is(metadata.getName())
                .and("fields." + fieldName).exists(true));
            
            query.fields().include("fields." + fieldName);
            query.with(Sort.by(Sort.Direction.DESC, "fields." + fieldName));
            query.limit(1);
            
            // Find the document with the maximum value for this field
            DocRequest latestDoc = mongoTemplate.findOne(query, DocRequest.class);
            
            if (latestDoc == null) {
                log.debug("No existing documents found for field '{}', starting with value 1", fieldName);
                return 1L; // First document
            }
            
            Object fieldValue = latestDoc.getFields().get(fieldName);
            if (fieldValue instanceof Number) {
                long maxValue = ((Number) fieldValue).longValue();
                long nextValue = maxValue + 1;
                log.debug("Generated auto-increment value {} for field '{}' (previous max: {})", 
                    nextValue, fieldName, maxValue);
                return nextValue;
            }
            
            log.warn("Field '{}' has non-numeric value {}, starting with value 1", 
                fieldName, fieldValue);
            return 1L; // Fallback for non-numeric values
            
        } catch (Exception e) {
            log.error("Error generating auto-increment value for field '{}': {}", 
                fieldName, e.getMessage(), e);
            throw new RuntimeException("Failed to generate auto-increment value for field '" + fieldName + "'", e);
        }
    }

    /**
     * Validates that the generated value is valid for the field type.
     * 
     * @param value the generated value
     * @param fieldMetadata the field metadata
     * @return validation error if any
     */
    public Optional<FieldValidationError> validateGeneratedValue(Long value, DocRequestFieldMetadata fieldMetadata) {
        // Validate that the value is within acceptable range for INTEGER type
        if (value == null) {
            return Optional.of(buildError(fieldMetadata.getName(), fieldMetadata,
                "ERR_AUTO_INCREMENT_NULL", "Auto-increment value cannot be null"));
        }
        
        // Integer range validation (assuming standard 32-bit integer)
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            return Optional.of(buildError(fieldMetadata.getName(), fieldMetadata,
                "ERR_AUTO_INCREMENT_RANGE", 
                String.format("Auto-increment value %d is outside valid integer range", value), value));
        }
        
        return Optional.empty();
    }
}