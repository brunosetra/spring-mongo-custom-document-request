package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.response.MetadataValidationError;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.com.docrequest.domain.enums.DocRequestFieldType.CPF;
import static br.com.docrequest.domain.enums.DocRequestFieldType.EMAIL;
import static br.com.docrequest.domain.enums.DocRequestFieldType.EXPIRATION_DATE;

/**
 * Validates that certain field types appear at most once in the metadata.
 * CPF, EMAIL, and EXPIRATION_DATE fields must be unique.
 */
@Component
@Order(90)
public class UniqueFieldTypeValidator implements MetadataValidator {

    @Override
    public List<MetadataValidationError> validate(DocRequestMetadata metadata) {
        List<MetadataValidationError> errors = new ArrayList<>();

        validateUniqueFieldType(metadata, CPF, "CPF", errors);
        validateUniqueFieldType(metadata, EMAIL, "EMAIL", errors);
        validateUniqueFieldType(metadata, EXPIRATION_DATE, "EXPIRATION_DATE", errors);

        return errors;
    }

    private void validateUniqueFieldType(DocRequestMetadata metadata, DocRequestFieldType type, 
                                     String typeName, List<MetadataValidationError> errors) {
        long count = metadata.getFields().stream()
                .filter(field -> field.getType() == type)
                .count();

        if (count > 1) {
            errors.add(MetadataValidationError.of(
                    String.format("DocRequestMetadata must not have more than 1 %s Field Type", typeName)
            ));
        }
    }

    @Override
    public int getOrder() {
        return 90;
    }
}
