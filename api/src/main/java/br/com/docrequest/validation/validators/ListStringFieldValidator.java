package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ListStringFieldValidator extends AbstractFieldValidator {

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.LIST_STRING;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        if (!(value instanceof List)) {
            return Optional.of(buildError(fieldName, metadata, "ERR_LIST_STRING_FORMAT",
                "Field '" + fieldName + "' must be a list of strings", value));
        }

        List<?> list = (List<?>) value;

        if (metadata.getMin() != null && list.size() < metadata.getMin()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_LIST_STRING_MIN_SIZE",
                "Field '" + fieldName + "' must have at least " + metadata.getMin() + " items", value));
        }

        if (metadata.getMax() != null && list.size() > metadata.getMax()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_LIST_STRING_MAX_SIZE",
                "Field '" + fieldName + "' must not exceed " + metadata.getMax() + " items", value));
        }

        return Optional.empty();
    }
}
