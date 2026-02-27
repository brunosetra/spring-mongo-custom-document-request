package br.com.docrequest.validation.validators;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.validation.AbstractFieldValidator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Component
public class DateTimeFieldValidator extends AbstractFieldValidator {

    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.DATETIME;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        String format = (metadata.getFormat() != null && !metadata.getFormat().isBlank())
            ? metadata.getFormat() : DEFAULT_DATETIME_FORMAT;

        try {
            LocalDateTime.parse(value.toString().trim(), DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            return Optional.of(buildError(fieldName, metadata, "ERR_DATETIME_FORMAT",
                "Field '" + fieldName + "' must be a valid datetime in format: " + format, value));
        }

        return Optional.empty();
    }
}
