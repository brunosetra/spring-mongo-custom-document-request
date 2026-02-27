package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class EmailFieldValidator extends AbstractFieldValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"
    );

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.EMAIL;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        String email = value.toString().trim();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return Optional.of(buildError(fieldName, metadata, "ERR_EMAIL_INVALID",
                "Field '" + fieldName + "' must be a valid email address", value));
        }

        return Optional.empty();
    }
}
