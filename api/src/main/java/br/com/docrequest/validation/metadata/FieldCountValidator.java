package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.response.MetadataValidationError;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Validates that DocRequestMetadata has at least one field.
 * Executes first (order 10) as it's a basic structural requirement.
 */
@Component
@Order(10)
public class FieldCountValidator implements MetadataValidator {

    private static final String ERROR_MESSAGE = "DocRequestMetadata.fields must not be empty";

    @Override
    public List<MetadataValidationError> validate(DocRequestMetadata metadata) {
        if (metadata.getFields() == null || metadata.getFields().isEmpty()) {
            return Collections.singletonList(MetadataValidationError.of(ERROR_MESSAGE));
        }
        return Collections.emptyList();
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
