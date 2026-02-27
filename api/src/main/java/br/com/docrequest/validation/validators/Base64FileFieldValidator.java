package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

/**
 * Validates generic Base64-encoded file content.
 */
@Component
public class Base64FileFieldValidator extends AbstractFieldValidator {

    @Value("${app.file.max-size-mb:10}")
    private int maxFileSizeMb;

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.FILE;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        return validateBase64(fieldName, value, metadata, null);
    }

    protected Optional<FieldValidationError> validateBase64(String fieldName, Object value,
                                                              DocRequestFieldMetadata metadata,
                                                              String expectedMimePrefix) {
        String base64 = value.toString().trim();

        // Strip data URI prefix if present (e.g., "data:image/jpeg;base64,")
        if (base64.contains(",")) {
            base64 = base64.substring(base64.indexOf(',') + 1);
        }

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            return Optional.of(buildError(fieldName, metadata, "ERR_FILE_BASE64_INVALID",
                "Field '" + fieldName + "' must be valid Base64-encoded content", value));
        }

        // Check file size
        long maxBytes = (long) maxFileSizeMb * 1024 * 1024;
        if (decoded.length > maxBytes) {
            return Optional.of(buildError(fieldName, metadata, "ERR_FILE_TOO_LARGE",
                "Field '" + fieldName + "' file size exceeds maximum of " + maxFileSizeMb + "MB", null));
        }

        return Optional.empty();
    }
}
