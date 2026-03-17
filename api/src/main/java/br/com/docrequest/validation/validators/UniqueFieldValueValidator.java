package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.exception.UniqueFieldViolationException;
import br.com.docrequest.repository.mongo.DocRequestRepository;
import br.com.docrequest.security.TenantContext;
import br.com.docrequest.validation.FieldValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validator for unique field constraints.
 * Checks if a field value already exists for the same template and tenant before allowing insertion.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UniqueFieldValueValidator implements FieldValidator {

    private final DocRequestRepository docRequestRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<FieldValidationError> validate(String fieldName, Object value, DocRequestFieldMetadata fieldMeta) {
        if (!fieldMeta.isUnique() || value == null) {
            return Optional.empty();
        }

        try {
            // Check if this value already exists for this template and tenant
            boolean exists = checkUniqueFieldInDatabase(
                TenantContext.getCurrentTenant(),
                fieldMeta.getDocRequestMetadata().getName(),
                fieldName,
                value
            );

            if (exists) {
                return Optional.of(FieldValidationError.of(
                    fieldName,
                    "ERR_UNIQUE_FIELD_VIOLATION",
                    "Field '" + fieldName + "' must have a unique value. The value '" + value + "' already exists.",
                    value
                ));
            }

            return Optional.empty();
        } catch (Exception e) {
            log.error("Error checking unique field validation for field '{}': {}", fieldName, e.getMessage());
            return Optional.of(FieldValidationError.of(
                fieldName,
                "ERR_UNIQUE_FIELD_VALIDATION_FAILED",
                "Error validating unique field '" + fieldName + "': " + e.getMessage()
            ));
        }
    }

    /**
     * Checks if a unique field value exists in the database using MongoDB query.
     * This method uses MongoDB aggregation to search for documents containing the specified field value.
     */
    private boolean checkUniqueFieldInDatabase(String partId, String metadataName, String fieldName, Object value) {
        // try {
            Query query = new Query(Criteria.where("partId").is(partId)
                .and("docRequestMetadataName").is(metadataName)
                .and("fields." + fieldName).is(value));
            
            return mongoTemplate.exists(query, "doc_requests");
        // } catch (Exception e) {
        //     throw new Inva
        //     log.warn("MongoDB query failed, falling back to repository method for field '{}': {}", fieldName, e.getMessage());
        //     // Fallback to repository method if MongoDB query fails
        //     return docRequestRepository.existsByPartIdAndDocRequestMetadataNameAndFieldsContainingValue(
        //         partId, metadataName, fieldName, value
        //     );
        // }
    }

    @Override
    public br.com.docrequest.domain.enums.DocRequestFieldType getType() {
        return br.com.docrequest.domain.enums.DocRequestFieldType.STRING; // Applies to all field types
    }
}