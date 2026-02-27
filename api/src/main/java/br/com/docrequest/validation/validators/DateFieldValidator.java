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

@Component
public class DateFieldValidator extends AbstractFieldValidator {

    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public DocRequestFieldType getType() {
        return DocRequestFieldType.DATE;
    }

    @Override
    protected Optional<FieldValidationError> validateValue(String fieldName, Object value,
                                                            DocRequestFieldMetadata metadata) {
        String format = (metadata.getFormat() != null && !metadata.getFormat().isBlank())
            ? metadata.getFormat() : DEFAULT_DATE_FORMAT;

        try {
            LocalDate.parse(value.toString().trim(), DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            return Optional.of(buildError(fieldName, metadata, "ERR_DATE_FORMAT",
                "Field '" + fieldName + "' must be a valid date in format: " + format, value));
        }

        return Optional.empty();
    }
}
