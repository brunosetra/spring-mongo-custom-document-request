package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IntegerFieldValidator extends AbstractFieldValidator {

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.INTEGER;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        long intValue;
        try {
            intValue = Long.parseLong(value.toString().trim());
        } catch (NumberFormatException e) {
            return Optional.of(buildError(fieldName, metadata, "ERR_INTEGER_FORMAT",
                "Field '" + fieldName + "' must be a valid integer", value));
        }

        if (metadata.getMin() != null && intValue < metadata.getMin()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_INTEGER_MIN",
                "Field '" + fieldName + "' must be at least " + metadata.getMin(), value));
        }

        if (metadata.getMax() != null && intValue > metadata.getMax()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_INTEGER_MAX",
                "Field '" + fieldName + "' must not exceed " + metadata.getMax(), value));
        }

        return Optional.empty();
    }
}
