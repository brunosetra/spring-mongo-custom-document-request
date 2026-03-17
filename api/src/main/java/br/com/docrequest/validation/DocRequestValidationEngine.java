package br.com.docrequest.validation;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.entity.DomainTable;
import br.com.docrequest.domain.entity.DomainTableRow;
import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.exception.UniqueFieldViolationException;
import br.com.docrequest.exception.ValidationException;
import br.com.docrequest.repository.mongo.DocRequestRepository;
import br.com.docrequest.service.DomainTableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Core validation engine that processes a DocRequest against its DocRequestMetadata template.
 * Handles all InputType processing logic and delegates type validation to FieldValidatorFactory.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocRequestValidationEngine {

    private static final Pattern FORMAT_EXPRESSION_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    private final FieldValidatorFactory validatorFactory;
    private final DomainTableService domainTableService;
    private final DocRequestRepository docRequestRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Validates and resolves all fields in the request against the metadata template.
     * Returns a resolved fields map with all values processed according to their inputType.
     *
     * @param requestFields the raw fields from the API request
     * @param metadata      the DocRequestMetadata template
     * @return resolved fields map ready for storage
     * @throws ValidationException if any field fails validation
     */
    public Map<String, Object> validateAndResolve(Map<String, Object> requestFields,
                                                   DocRequestMetadata metadata) {
        List<FieldValidationError> errors = new ArrayList<>();
        Map<String, Object> resolvedFields = new LinkedHashMap<>();

        for (DocRequestFieldMetadata fieldMeta : metadata.getFields()) {
            try {
                Object resolvedValue = resolveFieldValue(fieldMeta, requestFields, resolvedFields);
                Optional<FieldValidationError> validationError = validateField(fieldMeta, resolvedValue);
                validationError.ifPresent(errors::add);

                if (validationError.isEmpty() && resolvedValue != null) {
                    resolvedFields.put(fieldMeta.getName(), resolvedValue);
                }
            } catch (Exception e) {
                log.error("Error processing field '{}': {}", fieldMeta.getName(), e.getMessage());
                errors.add(FieldValidationError.of(fieldMeta.getName(), "ERR_FIELD_PROCESSING",
                    "Error processing field '" + fieldMeta.getName() + "': " + e.getMessage()));
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }

        return resolvedFields;
    }

    /**
     * Resolves the value for a field based on its inputType.
     */
    private Object resolveFieldValue(DocRequestFieldMetadata fieldMeta,
                                      Map<String, Object> requestFields,
                                      Map<String, Object> resolvedFields) {
        DocRequestFieldInputType inputType = fieldMeta.getInputType();
        String fieldName = fieldMeta.getName();

        return switch (inputType) {
            case IN -> requestFields.get(fieldName);

            case FIXED -> fieldMeta.getDefaultValue();

            case DEFAULT -> {
                Object provided = requestFields.get(fieldName);
                yield (provided != null) ? provided : fieldMeta.getDefaultValue();
            }

            case CALCULATED -> resolveFormatExpression(fieldMeta.getFormat(), resolvedFields);

            case DOMAIN -> {
                Object provided = requestFields.get(fieldName);
                if (provided != null) {
                    validateDomainValue(fieldMeta, provided.toString());
                }
                yield provided;
            }

            case DOMAIN_CALCULATED -> resolveDomainCalculated(fieldMeta, requestFields, resolvedFields);

            case INTERNAL -> null; // Set by system, not from request
        };
    }

    /**
     * Validates that a value exists as a key in the referenced DomainTable.
     */
    private void validateDomainValue(DocRequestFieldMetadata fieldMeta, String value) {
        String domainTableName = fieldMeta.getDefaultValue();
        if (domainTableName == null || domainTableName.isBlank()) {
            throw new IllegalStateException("Field '" + fieldMeta.getName()
                + "' has DOMAIN inputType but no DomainTable name in defaultValue");
        }

        DomainTable domainTable = domainTableService.findByName(domainTableName);
        boolean keyExists = domainTable.getRows().stream()
            .anyMatch(row -> value.equals(row.getValues().get(domainTable.getColumnId())));

        if (!keyExists) {
            throw new ValidationException(List.of(
                FieldValidationError.of(fieldMeta.getName(),
                    fieldMeta.getErrorCodeReference() != null ? fieldMeta.getErrorCodeReference() : "ERR_DOMAIN_VALUE_NOT_FOUND",
                    "Value '" + value + "' not found in domain table '" + domainTableName + "'",
                    value)
            ));
        }
    }

    /**
     * Resolves a DOMAIN_CALCULATED field:
     * 1. Extracts the domain field name from the format expression
     * 2. Gets the key value from the referenced domain field
     * 3. Looks up the row in the DomainTable
     * 4. Applies the format expression against the row's columns
     */
    private Object resolveDomainCalculated(DocRequestFieldMetadata fieldMeta,
                                            Map<String, Object> requestFields,
                                            Map<String, Object> resolvedFields) {
        String domainTableName = fieldMeta.getDefaultValue();
        if (domainTableName == null || domainTableName.isBlank()) {
            throw new IllegalStateException("Field '" + fieldMeta.getName()
                + "' has DOMAIN_CALCULATED inputType but no DomainTable name in defaultValue");
        }

        // Extract the domain field name from the format expression
        String domainFieldName = extractDomainFieldNameFromFormat(fieldMeta.getFormat());
        if (domainFieldName == null) {
            throw new IllegalStateException("Field '" + fieldMeta.getName()
                + "' has DOMAIN_CALCULATED inputType but format does not contain a valid domain field reference");
        }

        // Get the key value from the referenced domain field
        Object keyValue = requestFields.get(domainFieldName);
        if (keyValue == null) {
            return null;
        }

        DomainTable domainTable = domainTableService.findByName(domainTableName);
        Optional<DomainTableRow> matchingRow = domainTable.getRows().stream()
            .filter(row -> keyValue.toString().equals(row.getValues().get(domainTable.getColumnId())))
            .findFirst();

        if (matchingRow.isEmpty()) {
            throw new ValidationException(List.of(
                FieldValidationError.of(fieldMeta.getName(),
                    fieldMeta.getErrorCodeReference() != null ? fieldMeta.getErrorCodeReference() : "ERR_DOMAIN_KEY_NOT_FOUND",
                    "Key '" + keyValue + "' not found in domain table '" + domainTableName + "'",
                    keyValue)
            ));
        }

        // Apply format expression by replacing domain field references with column values
        return resolveDomainCalculatedFormat(fieldMeta.getFormat(), matchingRow.get().getValues());
    }

    /**
     * Resolves a format expression like "${firstName} ${lastName}" using the provided context map.
     */
    private String resolveFormatExpression(String format, Map<String, Object> context) {
        if (format == null || format.isBlank()) {
            return null;
        }

        Matcher matcher = FORMAT_EXPRESSION_PATTERN.matcher(format);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = context.get(variableName);
            String replacement = (value != null) ? value.toString() : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * Extracts the domain field name from a format expression.
     * Example: "${state_abbreviation.name} - ${state_abbreviation.region}" -> "state_abbreviation"
     */
    private String extractDomainFieldNameFromFormat(String format) {
        if (format == null || format.isBlank()) {
            return null;
        }

        Matcher matcher = FORMAT_EXPRESSION_PATTERN.matcher(format);
        if (matcher.find()) {
            String variableName = matcher.group(1);
            // The variable name is in format "fieldname.column", extract just the fieldname
            int dotIndex = variableName.indexOf('.');
            if (dotIndex > 0) {
                return variableName.substring(0, dotIndex);
            }
            return variableName;
        }

        return null;
    }

    /**
     * Resolves a DOMAIN_CALCULATED format expression by replacing domain field references
     * with column values from the domain table row.
     * Example: "${state_abbreviation.name} - ${state_abbreviation.region}" with row values
     * {"name": "Rio de Janeiro", "region": "Sudeste"} -> "Rio de Janeiro - Sudeste"
     */
    private String resolveDomainCalculatedFormat(String format, Map<String, String> rowValues) {
        if (format == null || format.isBlank()) {
            return null;
        }

        String result = format;
        Matcher matcher = FORMAT_EXPRESSION_PATTERN.matcher(format);

        while (matcher.find()) {
            String variableName = matcher.group(1);
            // The variable name is in format "fieldname.column", extract just the column name
            int dotIndex = variableName.indexOf('.');
            String columnName = (dotIndex > 0) ? variableName.substring(dotIndex + 1) : variableName;

            // Get the column value from the row
            String columnValue = rowValues.get(columnName);
            String replacement = (columnValue != null) ? columnValue : "";

            // Replace all occurrences of this pattern
            result = result.replace(matcher.group(), replacement);
        }

        return result;
    }

    /**
     * Validates a resolved field value using the appropriate type validator.
     */
    private Optional<FieldValidationError> validateField(DocRequestFieldMetadata fieldMeta, Object value) {
        if (value == null) {
            if (fieldMeta.isRequired()) {
                String errorCode = fieldMeta.getErrorCodeReference() != null
                    ? fieldMeta.getErrorCodeReference() : "ERR_REQUIRED";
                return Optional.of(FieldValidationError.of(fieldMeta.getName(), errorCode,
                    "Field '" + fieldMeta.getName() + "' is required"));
            }
            return Optional.empty();
        }

        // Check unique field constraints
        Optional<FieldValidationError> uniqueError = validateUniqueField(fieldMeta, value);
        if (uniqueError.isPresent()) {
            return uniqueError;
        }

        if (validatorFactory.hasValidator(fieldMeta.getType())) {
            return validatorFactory.getValidator(fieldMeta.getType())
                .validate(fieldMeta.getName(), value, fieldMeta);
        }

        return Optional.empty();
    }

    /**
     * Validates unique field constraints by checking the database for existing values.
     * This method is called during field validation to ensure uniqueness.
     *
     * @param fieldMeta the field metadata
     * @param value the field value to validate
     * @return Optional<FieldValidationError> if validation fails, empty otherwise
     */
    private Optional<FieldValidationError> validateUniqueField(DocRequestFieldMetadata fieldMeta, Object value) {
        if (!fieldMeta.isUnique()) {
            return Optional.empty();
        }

        try {
            String partId = br.com.docrequest.security.TenantContext.getCurrentTenant();
            String metadataName = fieldMeta.getDocRequestMetadata().getName();
            
            // Check if this value already exists for this template and tenant
            boolean exists = checkUniqueFieldInDatabase(partId, metadataName, fieldMeta.getName(), value);
            
            if (exists) {
                return Optional.of(FieldValidationError.of(
                    fieldMeta.getName(),
                    "ERR_UNIQUE_FIELD_VIOLATION",
                    "Field '" + fieldMeta.getName() + "' must have a unique value. The value '" + value + "' already exists.",
                    value
                ));
            }
            
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error checking unique field validation for field '{}': {}", fieldMeta.getName(), e.getMessage());
            return Optional.of(FieldValidationError.of(
                fieldMeta.getName(),
                "ERR_UNIQUE_FIELD_VALIDATION_FAILED",
                "Error validating unique field '" + fieldMeta.getName() + "': " + e.getMessage()
            ));
        }
    }

    /**
     * Checks if a unique field value exists in the database using MongoDB query.
     * This method uses MongoDB query to search for documents containing the specified field value.
     */
    private boolean checkUniqueFieldInDatabase(String partId, String metadataName, String fieldName, Object value) {
        // try {
            Query query = new Query(Criteria.where("partId").is(partId)
                .and("docRequestMetadataName").is(metadataName)
                .and("fields." + fieldName).is(value));
            
            return mongoTemplate.exists(query, "doc_requests");
        // } catch (Exception e) {
        //     log.warn("MongoDB query failed, falling back to repository method for field '{}': {}", fieldName, e.getMessage());
        //     // Fallback to repository method if MongoDB query fails
        //     return docRequestRepository.existsByPartIdAndDocRequestMetadataNameAndFieldsContainingValue(
        //         partId, metadataName, fieldName, value
        //     );
        // }
    }
}
