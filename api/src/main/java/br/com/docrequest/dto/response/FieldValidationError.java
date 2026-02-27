package br.com.docrequest.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents a single field validation error in the API response.
 */
@Getter
@Builder
public class FieldValidationError {

    private final String field;
    private final String errorCode;
    private final String message;
    private final Object rejectedValue;

    public static FieldValidationError of(String field, String errorCode, String message, Object rejectedValue) {
        return FieldValidationError.builder()
            .field(field)
            .errorCode(errorCode)
            .message(message)
            .rejectedValue(rejectedValue)
            .build();
    }

    public static FieldValidationError of(String field, String errorCode, String message) {
        return of(field, errorCode, message, null);
    }
}
