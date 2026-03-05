package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.dto.response.MetadataValidationError;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.com.docrequest.domain.enums.DocRequestFieldInputType.*;

/**
 * Validates the editable flag based on input type.
 * - FIXED fields must not be editable
 * - INTERNAL fields must be editable
 * - CALCULATED and DOMAIN_CALCULATED fields must not be editable
 */
@Component
@Order(50)
public class EditableValidator implements MetadataValidator {

    @Override
    public List<MetadataValidationError> validate(DocRequestMetadata metadata) {
        List<MetadataValidationError> errors = new ArrayList<>();

        for (DocRequestFieldMetadata field : metadata.getFields()) {
            validateFixedNotEditable(field, errors);
            validateInternalMustBeEditable(field, errors);
            validateCalculatedNotEditable(field, errors);
        }

        return errors;
    }

    private void validateFixedNotEditable(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        if (field.getInputType() == FIXED && field.isEditable()) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with InputType FIXED must not be editable", field.getName())
            ));
        }
    }

    private void validateInternalMustBeEditable(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        if (field.getInputType() == INTERNAL && !field.isEditable()) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with InputType INTERNAL must be editable", field.getName())
            ));
        }
    }

    private void validateCalculatedNotEditable(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        if ((field.getInputType() == CALCULATED || field.getInputType() == DOMAIN_CALCULATED) && field.isEditable()) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with InputType %s must not be editable", 
                            field.getName(), field.getInputType())
            ));
        }
    }

    @Override
    public int getOrder() {
        return 50;
    }
}
