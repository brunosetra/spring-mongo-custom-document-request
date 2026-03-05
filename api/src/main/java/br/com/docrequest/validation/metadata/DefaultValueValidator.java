package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.dto.response.MetadataValidationError;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.com.docrequest.domain.enums.DocRequestFieldInputType.DEFAULT;
import static br.com.docrequest.domain.enums.DocRequestFieldInputType.FIXED;

/**
 * Validates that defaultValue is provided for FIXED and DEFAULT input types.
 */
@Component
@Order(30)
public class DefaultValueValidator implements MetadataValidator {

    private static final String ERROR_MESSAGE = "defaultValue must not be empty for field '%s' with InputType %s";

    @Override
    public List<MetadataValidationError> validate(DocRequestMetadata metadata) {
        List<MetadataValidationError> errors = new ArrayList<>();

        for (DocRequestFieldMetadata field : metadata.getFields()) {
            if (requiresDefaultValue(field.getInputType())) {
                validateDefaultValue(field, errors);
            }
        }

        return errors;
    }

    private boolean requiresDefaultValue(DocRequestFieldInputType inputType) {
        return inputType == FIXED || inputType == DEFAULT;
    }

    private void validateDefaultValue(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        if (field.getDefaultValue() == null || field.getDefaultValue().isBlank()) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format(ERROR_MESSAGE, field.getName(), field.getInputType())
            ));
        }
    }

    @Override
    public int getOrder() {
        return 30;
    }
}
