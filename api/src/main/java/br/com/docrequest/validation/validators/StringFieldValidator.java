package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StringFieldValidator extends AbstractFieldValidator {

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.STRING;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        String strValue = value.toString();

        if (metadata.getMin() != null && strValue.length() < metadata.getMin()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_STRING_MIN_LENGTH",
                "Field '" + fieldName + "' must have at least " + metadata.getMin() + " characters",
                strValue));
        }

        if (metadata.getMax() != null && strValue.length() > metadata.getMax()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_STRING_MAX_LENGTH",
                "Field '" + fieldName + "' must not exceed " + metadata.getMax() + " characters",
                strValue));
        }

        return Optional.empty();
    }
}
