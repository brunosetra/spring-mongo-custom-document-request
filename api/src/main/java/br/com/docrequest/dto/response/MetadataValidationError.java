package br.com.docrequest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a validation error for DocRequestMetadata.
 * Can be field-specific or metadata-level (fieldName = null).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetadataValidationError {

    /**
     * The field name that failed validation. Null for metadata-level errors.
     */
    private String fieldName;

    /**
     * The validation error message.
     */
    private String message;

    /**
     * Creates a field-specific validation error.
     */
    public static MetadataValidationError of(String fieldName, String message) {
        return MetadataValidationError.builder()
                .fieldName(fieldName)
                .message(message)
                .build();
    }

    /**
     * Creates a metadata-level validation error (no specific field).
     */
    public static MetadataValidationError of(String message) {
        return MetadataValidationError.builder()
                .message(message)
                .build();
    }
}
