package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.MetadataValidationError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static br.com.docrequest.domain.enums.DocRequestFieldType.DATE;
import static br.com.docrequest.domain.enums.DocRequestFieldType.DATETIME;
import static br.com.docrequest.domain.enums.DocRequestFieldType.EXPIRATION_DATE;

/**
 * Validates date format for DATE, DATETIME, and EXPIRATION_DATE field types.
 * Ensures the format string is a valid Java DateTimeFormatter pattern.
 */
@Component
@Order(20)
@Slf4j
public class DateFormatValidator implements MetadataValidator {

    private static final String ERROR_MESSAGE = "Invalid date format for field '%s': %s";

    @Override
    public List<MetadataValidationError> validate(DocRequestMetadata metadata) {
        List<MetadataValidationError> errors = new ArrayList<>();

        for (DocRequestFieldMetadata field : metadata.getFields()) {
            if (requiresDateFormat(field.getType())) {
                validateDateFormat(field, errors);
            }
        }

        return errors;
    }

    private boolean requiresDateFormat(DocRequestFieldType type) {
        return type == DATE || type == DATETIME || type == EXPIRATION_DATE;
    }

    private void validateDateFormat(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        String format = field.getFormat();
        if (format == null || format.isBlank()) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Date format must not be empty for field '%s'", field.getName())
            ));
            return;
        }

        try {
            DateTimeFormatter.ofPattern(format).format(LocalDateTime.now());
        } catch (IllegalArgumentException e) {
            log.debug("Invalid date format '{}' for field '{}': {}", format, field.getName(), e.getMessage());
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format(ERROR_MESSAGE, field.getName(), e.getMessage())
            ));
        }
    }

    @Override
    public int getOrder() {
        return 20;
    }
}
