package br.com.docrequest.validation;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.dto.response.FieldValidationError;

import java.util.Optional;

/**
 * Abstract base class for field validators.
 * Handles the required check and delegates type-specific validation to subclasses.
 */
public abstract class AbstractFieldValidator implements FieldValidator {

    @Override
    public Optional<FieldValidationError> validate(String fieldName, Object value, DocRequestFieldMetadata metadata) {
        // Check required constraint
        if (value == null || isBlankString(value)) {
            if (metadata.isRequired()) {
                return Optional.of(buildError(fieldName, metadata,
                    "ERR_REQUIRED", "Field '" + fieldName + "' is required"));
            }
            return Optional.empty(); // Not required and not provided — OK
        }
        // Delegate to type-specific validation
        return validateValue(fieldName, value, metadata);
    }

    /**
     * Perform type-specific validation. Called only when value is non-null.
     */
    protected abstract Optional<FieldValidationError> validateValue(
        String fieldName, Object value, DocRequestFieldMetadata metadata);

    protected FieldValidationError buildError(String fieldName, DocRequestFieldMetadata metadata,
                                               String defaultCode, String message) {
        String errorCode = (metadata.getErrorCodeReference() != null && !metadata.getErrorCodeReference().isBlank())
            ? metadata.getErrorCodeReference()
            : defaultCode;
        return FieldValidationError.of(fieldName, errorCode, message);
    }

    protected FieldValidationError buildError(String fieldName, DocRequestFieldMetadata metadata,
                                               String defaultCode, String message, Object rejectedValue) {
        String errorCode = (metadata.getErrorCodeReference() != null && !metadata.getErrorCodeReference().isBlank())
            ? metadata.getErrorCodeReference()
            : defaultCode;
        return FieldValidationError.of(fieldName, errorCode, message, rejectedValue);
    }

    private boolean isBlankString(Object value) {
        return value instanceof String s && s.isBlank();
    }
}
