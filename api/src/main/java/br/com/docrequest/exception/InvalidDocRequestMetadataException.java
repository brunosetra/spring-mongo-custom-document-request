package br.com.docrequest.exception;

import br.com.docrequest.dto.response.MetadataValidationError;
import lombok.Getter;

import java.util.List;

/**
 * Thrown when DocRequestMetadata validation fails.
 * Contains detailed validation errors for each field or metadata-level issue.
 */
@Getter
public class InvalidDocRequestMetadataException extends RuntimeException {

    private final List<MetadataValidationError> errors;

    public InvalidDocRequestMetadataException(String message) {
        super(message);
        this.errors = List.of(MetadataValidationError.of(null, message));
    }

    public InvalidDocRequestMetadataException(String fieldName, String message) {
        super(message);
        this.errors = List.of(MetadataValidationError.of(fieldName, message));
    }

    public InvalidDocRequestMetadataException(List<MetadataValidationError> errors) {
        super("DocRequestMetadata validation failed with " + errors.size() + " error(s)");
        this.errors = errors;
    }
}
