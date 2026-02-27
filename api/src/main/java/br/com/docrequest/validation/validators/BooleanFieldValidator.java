package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class BooleanFieldValidator extends AbstractFieldValidator {

    private static final Set<String> VALID_VALUES = Set.of("true", "false", "1", "0", "yes", "no");

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.BOOLEAN;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        if (value instanceof Boolean) {
            return Optional.empty();
        }

        String strValue = value.toString().trim().toLowerCase();
        if (!VALID_VALUES.contains(strValue)) {
            return Optional.of(buildError(fieldName, metadata, "ERR_BOOLEAN_FORMAT",
                "Field '" + fieldName + "' must be a valid boolean (true/false)", value));
        }

        return Optional.empty();
    }
}
