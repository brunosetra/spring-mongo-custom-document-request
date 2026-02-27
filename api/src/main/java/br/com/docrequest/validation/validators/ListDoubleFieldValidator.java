package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ListDoubleFieldValidator extends AbstractFieldValidator {

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.LIST_DOUBLE;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        if (!(value instanceof List)) {
            return Optional.of(buildError(fieldName, metadata, "ERR_LIST_DOUBLE_FORMAT",
                "Field '" + fieldName + "' must be a list of decimal numbers", value));
        }

        List<?> list = (List<?>) value;

        for (Object item : list) {
            try {
                Double.parseDouble(item.toString());
            } catch (NumberFormatException e) {
                return Optional.of(buildError(fieldName, metadata, "ERR_LIST_DOUBLE_ELEMENT",
                    "Field '" + fieldName + "' contains non-decimal value: " + item, value));
            }
        }

        if (metadata.getMin() != null && list.size() < metadata.getMin()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_LIST_DOUBLE_MIN_SIZE",
                "Field '" + fieldName + "' must have at least " + metadata.getMin() + " items", value));
        }

        if (metadata.getMax() != null && list.size() > metadata.getMax()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_LIST_DOUBLE_MAX_SIZE",
                "Field '" + fieldName + "' must not exceed " + metadata.getMax() + " items", value));
        }

        return Optional.empty();
    }
}
