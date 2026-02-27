package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

/**
 * Validates Base64-encoded WSQ (Wavelet Scalar Quantization) fingerprint image content.
 * WSQ magic bytes: FF A0 (SOI marker for WSQ format)
 */
@Component
public class Base64WsqFieldValidator extends Base64FileFieldValidator {

    // WSQ magic bytes: FF A0
    private static final byte[] WSQ_MAGIC = {(byte) 0xFF, (byte) 0xA0};

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.FILE_WSQ;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        Optional<FieldValidationError> base64Error = validateBase64(fieldName, value, metadata, null);
        if (base64Error.isPresent()) {
            return base64Error;
        }

        String base64 = value.toString().trim();
        if (base64.contains(",")) {
            base64 = base64.substring(base64.indexOf(',') + 1);
        }

        byte[] decoded = Base64.getDecoder().decode(base64);
        if (decoded.length < 2 || decoded[0] != WSQ_MAGIC[0] || decoded[1] != WSQ_MAGIC[1]) {
            return Optional.of(buildError(fieldName, metadata, "ERR_FILE_WSQ_INVALID",
                "Field '" + fieldName + "' must be a valid WSQ fingerprint image encoded in Base64", null));
        }

        return Optional.empty();
    }
}
