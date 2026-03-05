package br.com.docrequest.exception;

import br.com.docrequest.dto.response.ApiErrorResponse;
import br.com.docrequest.dto.response.FieldValidationError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        log.warn("Validation failed: {} errors", ex.getErrors().size());
        return ResponseEntity.unprocessableEntity()
            .body(ApiErrorResponse.ofValidation(ex.getMessage(), request.getRequestURI(), ex.getErrors()));
    }

    @ExceptionHandler(InvalidDocRequestMetadataException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidDocRequestMetadataException(
            InvalidDocRequestMetadataException ex, HttpServletRequest request) {
        log.warn("DocRequestMetadata validation failed: {} errors", ex.getErrors().size());
        
        // Convert MetadataValidationError to FieldValidationError for API response
        List<FieldValidationError> fieldErrors = ex.getErrors().stream()
                .map(metaError -> FieldValidationError.of(
                        metaError.getFieldName(),
                        "ERR_METADATA_VALIDATION",
                        metaError.getMessage(),
                        null))
                .collect(Collectors.toList());
        
        return ResponseEntity.unprocessableEntity()
            .body(ApiErrorResponse.ofValidation(ex.getMessage(), request.getRequestURI(), fieldErrors));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> FieldValidationError.of(
                fieldError.getField(),
                "ERR_VALIDATION_" + fieldError.getField().toUpperCase(),
                fieldError.getDefaultMessage(),
                fieldError.getRejectedValue()))
            .collect(Collectors.toList());

        return ResponseEntity.badRequest()
            .body(ApiErrorResponse.ofValidation("Request validation failed", request.getRequestURI(), errors));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiErrorResponse.of(404, "Not Found", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest()
            .body(ApiErrorResponse.of(400, "Bad Request", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalState(
            IllegalStateException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest()
            .body(ApiErrorResponse.of(400, "Bad Request", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ApiErrorResponse> handleFileStorage(
            FileStorageException ex, HttpServletRequest request) {
        log.error("File storage error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiErrorResponse.of(500, "File Storage Error", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ApiErrorResponse.of(403, "Forbidden",
                "You do not have permission to access this resource", request.getRequestURI()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(
            AuthenticationException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiErrorResponse.of(401, "Unauthorized",
                "Authentication required", request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiErrorResponse.of(500, "Internal Server Error",
                "An unexpected error occurred", request.getRequestURI()));
    }
}
