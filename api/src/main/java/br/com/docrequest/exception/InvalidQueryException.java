package br.com.docrequest.exception;

import lombok.Getter;

/**
 * Exception thrown when a query is invalid.
 * This can be due to:
 * - Invalid field name
 * - Invalid operator for field type
 * - Invalid value type
 * - Query too complex
 * - Template not found
 */
@Getter
public class InvalidQueryException extends RuntimeException {
    
    private final String errorCode;
    
    public InvalidQueryException(String message) {
        super(message);
        this.errorCode = "INVALID_QUERY";
    }
    
    public InvalidQueryException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "INVALID_QUERY";
    }
    
    public InvalidQueryException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public static InvalidQueryException of(String field) {
        return new InvalidQueryException(
            "Field not found in template: " + field,
            "FIELD_NOT_FOUND"
        );
    }
    
    public static InvalidQueryException operatorInvalid(String operator, String fieldType) {
        return new InvalidQueryException(
            String.format("Operator %s is not valid for field type %s", operator, fieldType),
            "OPERATOR_INVALID"
        );
    }
    
    public static InvalidQueryException valueInvalid(Object value, String fieldType) {
        return new InvalidQueryException(
            String.format("Invalid value type %s for field type %s", 
                value.getClass().getSimpleName(), fieldType),
            "VALUE_INVALID"
        );
    }
    
    public static InvalidQueryException templateNotFound(String templateName) {
        return new InvalidQueryException(
            "Template not found: " + templateName,
            "TEMPLATE_NOT_FOUND"
        );
    }
    
    public static InvalidQueryException tooComplex() {
        return new InvalidQueryException(
            "Query is too complex. Maximum depth or conditions exceeded.",
            "QUERY_TOO_COMPLEX"
        );
    }
}
