package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.dto.response.MetadataValidationError;
import br.com.docrequest.repository.jpa.DomainTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static br.com.docrequest.domain.enums.DocRequestFieldInputType.DOMAIN;
import static br.com.docrequest.domain.enums.DocRequestFieldInputType.DOMAIN_CALCULATED;

/**
 * Validates that DOMAIN and DOMAIN_CALCULATED fields reference valid domain tables.
 */
@Component
@Order(60)
@RequiredArgsConstructor
@Slf4j
public class DomainTableValidator implements MetadataValidator {

    private final DomainTableRepository domainTableRepository;

    @Override
    public List<MetadataValidationError> validate(DocRequestMetadata metadata) {
        List<MetadataValidationError> errors = new ArrayList<>();

        for (DocRequestFieldMetadata field : metadata.getFields()) {
            if (requiresDomainTable(field.getInputType())) {
                validateDomainTableReference(field, errors);
            }
        }

        return errors;
    }

    private boolean requiresDomainTable(DocRequestFieldInputType inputType) {
        return inputType == DOMAIN || inputType == DOMAIN_CALCULATED;
    }

    private void validateDomainTableReference(DocRequestFieldMetadata field, List<MetadataValidationError> errors) {
        String domainTableName = field.getDefaultValue();
        if (domainTableName == null || domainTableName.isBlank()) {
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Field '%s' with InputType %s must have a valid domain table name in defaultValue",
                            field.getName(), field.getInputType())
            ));
            return;
        }

        if (domainTableRepository.findByNameWithRows(domainTableName).isEmpty()) {
            log.debug("Domain table '{}' not found for field '{}'", domainTableName, field.getName());
            errors.add(MetadataValidationError.of(
                    field.getName(),
                    String.format("Domain table '%s' referenced by field '%s' does not exist",
                            domainTableName, field.getName())
            ));
        }
    }

    @Override
    public int getOrder() {
        return 60;
    }
}
