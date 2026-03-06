package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.response.MetadataValidationError;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.com.docrequest.domain.enums.DocRequestFieldInputType.*;
import static br.com.docrequest.domain.enums.DocRequestFieldType.*;

/**
 * Validates compatibility between field types and input types.
 * Ensures that certain field types only work with specific input types.
 */
@Component
@Order(40)
public class TypeInputTypeCompatibilityValidator implements MetadataValidator {

    @Override
    public List<MetadataValidationError> validate(DocRequestMetadata metadata) {
        List<MetadataValidationError> errors = new ArrayList<>();

        for (DocRequestFieldMetadata field : metadata.getFields()) {
            validateCpfEmailCompatibility(field, errors);
            validateNameCompatibility(field, errors);
            validateProfilesCompatibility(field, errors);
            validateExpirationDateCompatibility(field, errors);
            validateCalculatedCompatibility(field, errors);
            validateDomainCalculatedCompatibility(field, errors);
        }

        return errors;
    }

    private void validateCpfEmailCompatibility(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        if ((field.getType() == CPF || field.getType() == EMAIL) && field.getInputType() != IN) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with type %s must have InputType IN", field.getName(), field.getType())
            ));
        }
    }

    private void validateNameCompatibility(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        if (field.getType() == NAME && field.getInputType() != IN && field.getInputType() != CALCULATED) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with type NAME must have InputType IN or CALCULATED", field.getName())
            ));
        }
    }

    private void validateProfilesCompatibility(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        if (field.getType() == PROFILES && field.getInputType() != IN && field.getInputType() != DEFAULT && field.getInputType() != FIXED) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with type PROFILES must have InputType IN, DEFAULT, or FIXED", field.getName())
            ));
        }
    }

    private void validateExpirationDateCompatibility(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        if (field.getType() == EXPIRATION_DATE) {
            if (field.getInputType() != IN && field.getInputType() != FIXED && 
                field.getInputType() != DEFAULT && field.getInputType() != INTERNAL) {
                errors.add(MetadataValidationError.of(
                        field.getName(),
                        String.format("Field '%s' with type EXPIRATION_DATE must have InputType IN, FIXED, DEFAULT, or INTERNAL", field.getName())
                ));
            }
        }
    }

    private void validateCalculatedCompatibility(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        if (field.getInputType() == CALCULATED && field.getType() != STRING && field.getType() != NAME) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with InputType CALCULATED must have type STRING or NAME", field.getName())
            ));
        }
    }

    private void validateDomainCalculatedCompatibility(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        if (field.getInputType() == DOMAIN_CALCULATED && field.getType() != STRING) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with InputType DOMAIN_CALCULATED must have type STRING", field.getName())
            ));
        }
    }

    @Override
    public int getOrder() {
        return 40;
    }
}
