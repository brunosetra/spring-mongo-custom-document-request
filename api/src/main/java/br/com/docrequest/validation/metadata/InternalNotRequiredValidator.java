package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.response.MetadataValidationError;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.com.docrequest.domain.enums.DocRequestFieldInputType.*;

/**
 * Validates the INTERNAL field must not be required, since it will be update after user submission.
 */
@Component
@Order(50)
public class InternalNotRequiredValidator implements MetadataValidator {

    @Override
    public List<MetadataValidationError> validate(DocRequestMetadata metadata) {
        List<MetadataValidationError> errors = new ArrayList<>();

        for (DocRequestFieldMetadata field : metadata.getFields()) {
            validateInternalNotRequired(field, errors);
        }

        return errors;
    }

    private void validateInternalNotRequired(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        if (field.getInputType() == INTERNAL && field.isRequired()) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with InputType INTERNAL must not be required", field.getName())
            ));
        }
    }

    @Override
    public int getOrder() {
        return 50;
    }
}
