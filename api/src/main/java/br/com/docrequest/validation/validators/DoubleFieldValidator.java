package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DoubleFieldValidator extends AbstractFieldValidator {

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.DOUBLE;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        double doubleValue;
        try {
            doubleValue = Double.parseDouble(value.toString().trim());
        } catch (NumberFormatException e) {
            return Optional.of(buildError(fieldName, metadata, "ERR_DOUBLE_FORMAT",
                "Field '" + fieldName + "' must be a valid decimal number", value));
        }

        if (metadata.getMin() != null && doubleValue < metadata.getMin()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_DOUBLE_MIN",
                "Field '" + fieldName + "' must be at least " + metadata.getMin(), value));
        }

        if (metadata.getMax() != null && doubleValue > metadata.getMax()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_DOUBLE_MAX",
                "Field '" + fieldName + "' must not exceed " + metadata.getMax(), value));
        }

        return Optional.empty();
    }
}
