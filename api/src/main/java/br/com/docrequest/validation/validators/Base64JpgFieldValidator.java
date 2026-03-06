package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

/**
 * Validates Base64-encoded JPEG image content.
 * Checks that the decoded bytes start with the JPEG magic bytes (FFD8FF).
 */
@Component
public class Base64JpgFieldValidator extends Base64FileFieldValidator {

    // JPEG magic bytes: FF D8 FF
    private static final byte[] JPEG_MAGIC = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.FILE_IMG;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        Optional<FieldValidationError> base64Error = validateBase64(fieldName, value, metadata, null);
        if (base64Error.isPresent()) {
            return base64Error;
        }

        // Validate JPEG magic bytes
        String base64 = value.toString().trim();
        if (base64.contains(",")) {
            base64 = base64.substring(base64.indexOf(',') + 1);
        }

        byte[] decoded = Base64.getDecoder().decode(base64);
        if (decoded.length < 3 || decoded[0] != JPEG_MAGIC[0]
            || decoded[1] != JPEG_MAGIC[1] || decoded[2] != JPEG_MAGIC[2]) {
            return Optional.of(buildError(fieldName, metadata, "ERR_FILE_IMG_NOT_JPEG",
                "Field '" + fieldName + "' must be a valid JPEG image encoded in Base64", null));
        }

        return Optional.empty();
    }
}
