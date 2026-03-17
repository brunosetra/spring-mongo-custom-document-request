package br.com.docrequest.exception;

import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.FieldValidator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when a unique field constraint is violated.
 * This exception is used to signal that a field value already exists
 * for the same template and tenant.
 */
@Getter
@Slf4j
public class UniqueFieldViolationException extends RuntimeException {

    private final String fieldName;
    private final Object fieldValue;
    private final List<FieldValidationError> validationErrors;

    public UniqueFieldViolationException(String fieldName, Object fieldValue) {
        super(String.format("Field '%s' must have a unique value. The value '%s' already exists.", fieldName, fieldValue));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.validationErrors = Collections.singletonList(
            FieldValidationError.of(fieldName, "ERR_UNIQUE_FIELD_VIOLATION", 
                "Field '" + fieldName + "' must have a unique value. The value '" + fieldValue + "' already exists.", 
                fieldValue)
        );
        log.warn("Unique field violation for field '{}': value '{}' already exists", fieldName, fieldValue);
    }

    public UniqueFieldViolationException(String fieldName, Object fieldValue, String message) {
        super(message);
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.validationErrors = Collections.singletonList(
            FieldValidationError.of(fieldName, "ERR_UNIQUE_FIELD_VIOLATION", message, fieldValue)
        );
        log.warn("Unique field violation for field '{}': {}", fieldName, message);
    }
}