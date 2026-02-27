package br.com.docrequest.exception;

import br.com.docrequest.dto.response.FieldValidationError;
import lombok.Getter;

import java.util.List;

/**
 * Thrown when one or more field validations fail during DocRequest processing.
 */
@Getter
public class ValidationException extends RuntimeException {

    private final List<FieldValidationError> errors;

    public ValidationException(List<FieldValidationError> errors) {
        super("Document request validation failed with " + errors.size() + " error(s)");
        this.errors = errors;
    }

    public ValidationException(String message, List<FieldValidationError> errors) {
        super(message);
        this.errors = errors;
    }
}
