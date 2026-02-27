package br.com.docrequest.validation;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;

import java.util.Optional;

/**
 * Strategy interface for field-level validation.
 * Each implementation handles validation for a specific DocRequestFieldType.
 */
public interface FieldValidator {

    /**
     * Validates the given value against the field metadata constraints.
     *
     * @param fieldName the name of the field being validated
     * @param value     the resolved value to validate
     * @param metadata  the field metadata containing constraints
     * @return an Optional containing a FieldValidationError if validation fails, empty if valid
     */
    Optional<FieldValidationError> validate(String fieldName, Object value, DocRequestFieldMetadata metadata);

    /**
     * Returns the DocRequestFieldType this validator handles.
     */
    DocRequestFieldType getType();
}
