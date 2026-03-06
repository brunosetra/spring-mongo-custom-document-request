package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.entity.DomainTable;
import br.com.docrequest.repository.jpa.DomainTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static br.com.docrequest.domain.enums.DocRequestFieldInputType.CALCULATED;
import static br.com.docrequest.domain.enums.DocRequestFieldInputType.DOMAIN;
import static br.com.docrequest.domain.enums.DocRequestFieldInputType.DOMAIN_CALCULATED;

/**
 * Helper class for validating field references in CALCULATED and DOMAIN_CALCULATED fields.
 * Extracts common logic to avoid code duplication.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReferenceValidationHelper {

    private final DomainTableRepository domainTableRepository;

    private static final Pattern FIELD_ENCLOSING_PATTERN = Pattern.compile("\\$\\{[\\w\\_\\-.]*\\}");
    private static final Pattern DOMAIN_FIELD_PATTERN = Pattern.compile("\\$\\{([\\w\\-_]+)(\\.)([\\w\\-_]+)\\}");
    private static final Pattern NON_DOMAIN_FIELD_PATTERN = Pattern.compile("\\$\\{[\\w\\-_]+\\}");

    /**
     * Validates field references in a CALCULATED field's format.
     */
    public boolean validateCalculatedReferences(DocRequestFieldMetadata field, DocRequestMetadata metadata) {
        if (field == null || metadata == null || field.getFormat() == null || 
            field.getFormat().isBlank() || field.getInputType() != CALCULATED) {
            return false;
        }

        return validateReferences(field.getFormat(), metadata, field.getName());
    }

    /**
     * Validates field references in a DOMAIN_CALCULATED field's format.
     */
    public boolean validateDomainCalculatedReferences(DocRequestFieldMetadata field, DocRequestMetadata metadata) {
        if (field == null || metadata == null || field.getFormat() == null || 
            field.getFormat().isBlank() || field.getInputType() != DOMAIN_CALCULATED) {
            return false;
        }

        return validateReferences(field.getFormat(), metadata, field.getName());
    }

    private boolean validateReferences(String expression, DocRequestMetadata metadata, String fieldName) {
        List<String> enclosingFields = extractMatches(FIELD_ENCLOSING_PATTERN, expression);
        List<String> domainFields = extractMatches(DOMAIN_FIELD_PATTERN, expression);
        List<String> nonDomainFields = extractMatches(NON_DOMAIN_FIELD_PATTERN, expression);

        // Verify all enclosing fields are either domain or non-domain fields
        if (enclosingFields.size() != domainFields.size() + nonDomainFields.size()) {
            log.debug("Invalid field reference pattern in field '{}'", fieldName);
            return false;
        }

        try {
            validateDomainFieldReferences(domainFields, metadata, fieldName);
            validateNonDomainFieldReferences(nonDomainFields, metadata, fieldName);
        } catch (ReferenceValidationException e) {
            log.debug("Reference validation failed for field '{}': {}", fieldName, e.getMessage());
            return false;
        }

        return true;
    }

    private List<String> extractMatches(Pattern pattern, String text) {
        return pattern.matcher(text)
                .results()
                .map(MatchResult::group)
                .collect(Collectors.toList());
    }

    private void validateDomainFieldReferences(List<String> domainFields, DocRequestMetadata metadata, String fieldName) {
        for (String item : domainFields) {
            String fieldNameRef = item.replace("${", "").replace("}", "");
            String[] fieldRefParts = fieldNameRef.split("\\.");

            if (fieldRefParts.length != 2) {
                throw new ReferenceValidationException(
                        String.format("Domain field reference in field '%s' must be in format 'fieldname.column'", fieldName)
                );
            }

            String domainFieldName = fieldRefParts[0];
            String columnName = fieldRefParts[1];

            // Find the domain field
            DocRequestFieldMetadata domainField = metadata.getFields().stream()
                    .filter(f -> f.getInputType() == DOMAIN && f.getName().equals(domainFieldName))
                    .findFirst()
                    .orElseThrow(() -> new ReferenceValidationException(
                            String.format("Domain field '%s' referenced by field '%s' not found", domainFieldName, fieldName)
                    ));

            // Validate domain table exists and has the column
            String domainTableName = domainField.getDefaultValue();
            DomainTable domainTable = domainTableRepository.findByNameWithRows(domainTableName)
                    .orElseThrow(() -> new ReferenceValidationException(
                            String.format("Domain table '%s' referenced by field '%s' not found", domainTableName, fieldName)
                    ));

            if (!domainTable.getColumns().contains(columnName)) {
                throw new ReferenceValidationException(
                        String.format("Column '%s' not found in domain table '%s' referenced by field '%s'",
                                columnName, domainTableName, fieldName)
                );
            }
        }
    }

    private void validateNonDomainFieldReferences(List<String> nonDomainFields, DocRequestMetadata metadata, String fieldName) {
        for (String item : nonDomainFields) {
            String fieldNameRef = item.replace("${", "").replace("}", "");

            boolean fieldExists = metadata.getFields().stream()
                    .filter(f -> f.getInputType() != DOMAIN && f.getName().equals(fieldNameRef))
                    .findFirst()
                    .isPresent();

            if (!fieldExists) {
                throw new ReferenceValidationException(
                        String.format("Field '%s' referenced by field '%s' not found", fieldNameRef, fieldName)
                );
            }
        }
    }

    private static class ReferenceValidationException extends RuntimeException {
        public ReferenceValidationException(String message) {
            super(message);
        }
    }
}
