package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Validates expiration dates — must be a valid date AND must be in the future.
 */
@Component
public class ExpirationDateFieldValidator extends AbstractFieldValidator {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.EXPIRATION_DATE;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        String format = (metadata.getFormat() != null && !metadata.getFormat().isBlank())
            ? metadata.getFormat() : DEFAULT_DATE_FORMAT;

        LocalDate date;
        try {
            date = LocalDate.parse(value.toString().trim(), DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            return Optional.of(buildError(fieldName, metadata, "ERR_EXPIRATION_DATE_FORMAT",
                "Field '" + fieldName + "' must be a valid date in format: " + format, value));
        }

        if (!date.isAfter(LocalDate.now())) {
            return Optional.of(buildError(fieldName, metadata, "ERR_EXPIRATION_DATE_PAST",
                "Field '" + fieldName + "' must be a future date", value));
        }

        return Optional.empty();
    }
}
