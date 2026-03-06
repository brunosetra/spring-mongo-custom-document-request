package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.response.MetadataValidationError;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.com.docrequest.domain.enums.DocRequestFieldInputType.CALCULATED;
import static br.com.docrequest.domain.enums.DocRequestFieldInputType.DOMAIN_CALCULATED;

/**
 * Validates field references in CALCULATED and DOMAIN_CALCULATED fields.
 * Ensures referenced fields exist and domain table columns are valid.
 */
@Component
@Order(80)
@RequiredArgsConstructor
public class ReferenceValidator implements MetadataValidator {

    private final ReferenceValidationHelper referenceValidationHelper;

    @Override
    public List<MetadataValidationError> validate(DocRequestMetadata metadata) {
        List<MetadataValidationError> errors = new ArrayList<>();

        for (DocRequestFieldMetadata field : metadata.getFields()) {
            if (field.getInputType() == CALCULATED) {
                validateCalculatedField(field, metadata, errors);
            } else if (field.getInputType() == DOMAIN_CALCULATED) {
                validateDomainCalculatedField(field, metadata, errors);
            }
        }

        return errors;
    }

    private void validateCalculatedField(DocRequestFieldMetadata field, DocRequestMetadata metadata, 
                                      List<MetadataValidationError> errors) {
        if (!referenceValidationHelper.validateCalculatedReferences(field, metadata)) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with InputType CALCULATED has invalid field references in format",
                            field.getName())
            ));
        }
    }

    private void validateDomainCalculatedField(DocRequestFieldMetadata field, DocRequestMetadata metadata,
                                            List<MetadataValidationError> errors) {
        if (!referenceValidationHelper.validateDomainCalculatedReferences(field, metadata)) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with InputType DOMAIN_CALCULATED has invalid field references in format",
                            field.getName())
            ));
        }
    }

    @Override
    public int getOrder() {
        return 80;
    }
}
