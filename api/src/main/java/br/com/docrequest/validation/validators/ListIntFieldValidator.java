package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ListIntFieldValidator extends AbstractFieldValidator {

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.LIST_INT;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        if (!(value instanceof List)) {
            return Optional.of(buildError(fieldName, metadata, "ERR_LIST_INT_FORMAT",
                "Field '" + fieldName + "' must be a list of integers", value));
        }

        List<?> list = (List<?>) value;

        // Validate each element is an integer
        for (Object item : list) {
            try {
                Long.parseLong(item.toString());
            } catch (NumberFormatException e) {
                return Optional.of(buildError(fieldName, metadata, "ERR_LIST_INT_ELEMENT",
                    "Field '" + fieldName + "' contains non-integer value: " + item, value));
            }
        }

        if (metadata.getMin() != null && list.size() < metadata.getMin()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_LIST_INT_MIN_SIZE",
                "Field '" + fieldName + "' must have at least " + metadata.getMin() + " items", value));
        }

        if (metadata.getMax() != null && list.size() > metadata.getMax()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_LIST_INT_MAX_SIZE",
                "Field '" + fieldName + "' must not exceed " + metadata.getMax() + " items", value));
        }

        return Optional.empty();
    }
}
