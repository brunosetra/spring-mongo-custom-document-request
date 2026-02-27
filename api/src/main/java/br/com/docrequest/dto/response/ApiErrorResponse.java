package br.com.docrequest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standard API error response body.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<FieldValidationError> details;

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .error(error)
            .message(message)
            .path(path)
            .build();
    }

    public static ApiErrorResponse ofValidation(String message, String path, List<FieldValidationError> details) {
        return ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(422)
            .error("Validation Failed")
            .message(message)
            .path(path)
            .details(details)
            .build();
    }
}
