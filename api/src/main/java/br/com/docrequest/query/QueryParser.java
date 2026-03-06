package br.com.docrequest.query;

import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.ComparisonOperator;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.request.FilterCondition;
import br.com.docrequest.dto.request.FilterGroup;
import br.com.docrequest.dto.request.PaginationRequest;
import br.com.docrequest.dto.request.QueryRequest;
import br.com.docrequest.dto.request.SortRequest;
import br.com.docrequest.exception.InvalidQueryException;
import br.com.docrequest.service.MetadataCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

/**
 * Parses and validates query requests.
 * Converts query DSL into internal representation with type safety.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QueryParser {
    
    private static final int MAX_QUERY_DEPTH = 5;
    private static final int MAX_CONDITIONS = 50;
    private static final int MAX_SEARCH_TERM_LENGTH = 100;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private final MetadataCacheService metadataCacheService;
    
    /**
     * Parse a query request into internal representation.
     * Validates template existence, field types, and operator compatibility.
     */
    public ParsedQuery parse(QueryRequest request) {
        // Validate template exists
        DocRequestMetadata metadata = metadataCacheService.getMetadataEntity(request.getTemplateName())
            .orElseThrow(() -> InvalidQueryException.templateNotFound(request.getTemplateName()));
        
        if (!metadata.isEnabled()) {
            throw new InvalidQueryException(
                "Template is disabled: " + request.getTemplateName(),
                "TEMPLATE_DISABLED"
            );
        }
        
        // Parse filters
        ParsedFilterGroup parsedFilters = parseFilterGroup(
            request.getFilters(), 
            metadata,
            0
        );
        
        // Validate query complexity
        validateQueryComplexity(parsedFilters);
        
        // Parse pagination
        ParsedPagination parsedPagination = parsePagination(
            request.getPagination(),
            metadata
        );
        
        return ParsedQuery.builder()
            .templateName(request.getTemplateName())
            .metadata(metadata)
            .filters(parsedFilters)
            .pagination(parsedPagination)
            .build();
    }
    
    /**
     * Parse a filter group recursively.
     */
    private ParsedFilterGroup parseFilterGroup(
        FilterGroup group,
        DocRequestMetadata metadata,
        int depth
    ) {
        if (depth > MAX_QUERY_DEPTH) {
            throw InvalidQueryException.tooComplex();
        }
        
        List<ParsedFilterCondition> conditions = group.getConditions().stream()
            .map(condition -> parseFilterCondition(condition, metadata, depth + 1))
            .toList();
        
        return ParsedFilterGroup.builder()
            .operator(group.getOperator())
            .conditions(conditions)
            .build();
    }
    
    /**
     * Parse a single filter condition.
     */
    private ParsedFilterCondition parseFilterCondition(
        FilterCondition condition,
        DocRequestMetadata metadata,
        int depth
    ) {
        // Check for nested group
        if (condition.getNestedGroup() != null) {
            if (condition.getField() != null || condition.getOperator() != null || condition.getValue() != null) {
                throw new InvalidQueryException(
                    "Nested group cannot have field, operator, or value",
                    "INVALID_NESTED_GROUP"
                );
            }
            return ParsedFilterCondition.builder()
                .nestedGroup(parseFilterGroup(condition.getNestedGroup(), metadata, depth))
                .build();
        }
        
        // Validate leaf condition
        if (condition.getField() == null || condition.getOperator() == null) {
            throw new InvalidQueryException(
                "Field and operator are required for leaf conditions",
                "MISSING_FIELD_OR_OPERATOR"
            );
        }
        
        // Validate field exists in template
        DocRequestFieldMetadata fieldMetadata = metadata.getFields().stream()
            .filter(f -> f.getName().equals(condition.getField()))
            .findFirst()
            .orElseThrow(() -> InvalidQueryException.of(condition.getField()));
        
        // Validate operator compatibility with field type
        validateOperatorCompatibility(
            condition.getOperator(),
            fieldMetadata.getType()
        );
        
        // Validate value type
        validateValueType(
            condition.getValue(),
            fieldMetadata.getType(),
            condition.getOperator()
        );
        
        return ParsedFilterCondition.builder()
            .field(condition.getField())
            .operator(condition.getOperator())
            .value(condition.getValue())
            .fieldType(fieldMetadata.getType())
            .build();
    }
    
    /**
     * Validate that the operator is compatible with the field type.
     */
    private void validateOperatorCompatibility(
        ComparisonOperator operator,
        DocRequestFieldType fieldType
    ) {
        Set<ComparisonOperator> validOperators = getValidOperators(fieldType);
        
        if (!validOperators.contains(operator)) {
            throw InvalidQueryException.operatorInvalid(
                operator.name(),
                fieldType.name()
            );
        }
    }
    
    /**
     * Get valid operators for a given field type.
     */
    private Set<ComparisonOperator> getValidOperators(DocRequestFieldType fieldType) {
        return switch (fieldType) {
            case STRING, NAME, CPF, EMAIL, EMAIL_ALTERNATIVE -> Set.of(
                ComparisonOperator.EQ, ComparisonOperator.NE,
                ComparisonOperator.CONTAINS, ComparisonOperator.STARTS_WITH,
                ComparisonOperator.ENDS_WITH, ComparisonOperator.IN,
                ComparisonOperator.NOT_IN, ComparisonOperator.IS_NULL,
                ComparisonOperator.IS_NOT_NULL, ComparisonOperator.IS_EMPTY,
                ComparisonOperator.IS_NOT_EMPTY
            );
            case INTEGER, DOUBLE -> Set.of(
                ComparisonOperator.EQ, ComparisonOperator.NE,
                ComparisonOperator.GT, ComparisonOperator.GTE,
                ComparisonOperator.LT, ComparisonOperator.LTE,
                ComparisonOperator.IN, ComparisonOperator.NOT_IN,
                ComparisonOperator.IS_NULL, ComparisonOperator.IS_NOT_NULL
            );
            case DATE, DATETIME, EXPIRATION_DATE -> Set.of(
                ComparisonOperator.EQ, ComparisonOperator.NE,
                ComparisonOperator.GT, ComparisonOperator.GTE,
                ComparisonOperator.LT, ComparisonOperator.LTE,
                ComparisonOperator.IN, ComparisonOperator.NOT_IN,
                ComparisonOperator.IS_NULL, ComparisonOperator.IS_NOT_NULL
            );
            case BOOLEAN -> Set.of(
                ComparisonOperator.EQ, ComparisonOperator.NE,
                ComparisonOperator.IS_NULL, ComparisonOperator.IS_NOT_NULL
            );
            case LIST_STRING, LIST_INT, LIST_DOUBLE, PROFILES -> Set.of(
                ComparisonOperator.CONTAINS, ComparisonOperator.SIZE_EQ,
                ComparisonOperator.SIZE_GT, ComparisonOperator.SIZE_LT,
                ComparisonOperator.IS_EMPTY, ComparisonOperator.IS_NOT_EMPTY
            );
            case FILE, FILE_IMG, FILE_WSQ -> Set.of(
                ComparisonOperator.EQ, ComparisonOperator.NE,
                ComparisonOperator.IS_NULL, ComparisonOperator.IS_NOT_NULL
            );
        };
    }
    
    /**
     * Validate that the value type matches the field type.
     */
    private void validateValueType(
        Object value,
        DocRequestFieldType fieldType,
        ComparisonOperator operator
    ) {
        // Skip validation for null-checking operators
        if (operator == ComparisonOperator.IS_NULL || 
            operator == ComparisonOperator.IS_NOT_NULL ||
            operator == ComparisonOperator.IS_EMPTY ||
            operator == ComparisonOperator.IS_NOT_EMPTY) {
            return;
        }
        
        if (value == null) {
            throw new InvalidQueryException(
                "Value cannot be null for operator: " + operator,
                "VALUE_REQUIRED"
            );
        }
        
        boolean isValid = switch (fieldType) {
            case STRING, NAME, CPF, EMAIL, EMAIL_ALTERNATIVE -> 
                value instanceof String && validateStringLength((String) value);
            case INTEGER -> 
                value instanceof Integer || value instanceof Long;
            case DOUBLE -> 
                value instanceof Double || value instanceof Float;
            case DATE -> 
                value instanceof String && isValidDateString((String) value);
            case DATETIME, EXPIRATION_DATE -> 
                value instanceof String && isValidDateTimeString((String) value);
            case BOOLEAN -> 
                value instanceof Boolean;
            case LIST_STRING -> 
                value instanceof List && ((List<?>) value).stream()
                    .allMatch(v -> v instanceof String);
            case LIST_INT -> 
                value instanceof List && ((List<?>) value).stream()
                    .allMatch(v -> v instanceof Integer || v instanceof Long);
            case LIST_DOUBLE -> 
                value instanceof List && ((List<?>) value).stream()
                    .allMatch(v -> v instanceof Double || v instanceof Float);
            case PROFILES -> 
                value instanceof List;
            case FILE, FILE_IMG, FILE_WSQ -> 
                value instanceof String;
        };
        
        if (!isValid) {
            throw InvalidQueryException.valueInvalid(value, fieldType.name());
        }
    }
    
    /**
     * Validate string length for search operations.
     */
    private boolean validateStringLength(String value) {
        if (value.length() > MAX_SEARCH_TERM_LENGTH) {
            throw new InvalidQueryException(
                "Search term too long. Maximum length is " + MAX_SEARCH_TERM_LENGTH,
                "SEARCH_TERM_TOO_LONG"
            );
        }
        return true;
    }
    
    /**
     * Validate date string format.
     */
    private boolean isValidDateString(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            throw new InvalidQueryException(
                "Invalid date format. Expected ISO 8601 format (YYYY-MM-DD): " + dateStr,
                "INVALID_DATE_FORMAT"
            );
        }
    }
    
    /**
     * Validate datetime string format.
     */
    private boolean isValidDateTimeString(String dateTimeStr) {
        try {
            LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            throw new InvalidQueryException(
                "Invalid datetime format. Expected ISO 8601 format: " + dateTimeStr,
                "INVALID_DATETIME_FORMAT"
            );
        }
    }
    
    /**
     * Parse pagination configuration.
     */
    private ParsedPagination parsePagination(
        PaginationRequest pagination,
        DocRequestMetadata metadata
    ) {
        if (pagination == null) {
            return ParsedPagination.builder()
                .page(0)
                .size(20)
                .sort(List.of())
                .build();
        }
        
        // Validate sort fields
        if (pagination.getSort() != null) {
            for (SortRequest sort : pagination.getSort()) {
                validateSortField(sort.getField(), metadata);
            }
        }
        
        List<ParsedPagination.SortConfig> sortConfigs = pagination.getSort() != null
            ? pagination.getSort().stream()
                .map(sort -> new ParsedPagination.SortConfig(
                    sort.getField(),
                    sort.getDirection()
                ))
                .toList()
            : List.of();
        
        return ParsedPagination.builder()
            .page(pagination.getPage())
            .size(pagination.getSize())
            .sort(sortConfigs)
            .build();
    }
    
    /**
     * Validate that sort field exists in template or is a system field.
     */
    private void validateSortField(String field, DocRequestMetadata metadata) {
        boolean fieldExists = metadata.getFields().stream()
            .anyMatch(f -> f.getName().equals(field));
        
        boolean isSystemField = field.equals("createdAt") || field.equals("updatedAt");
        
        if (!fieldExists && !isSystemField) {
            throw InvalidQueryException.of(field);
        }
    }
    
    /**
     * Validate overall query complexity.
     */
    private void validateQueryComplexity(ParsedFilterGroup group) {
        int conditionCount = countConditions(group);
        
        if (conditionCount > MAX_CONDITIONS) {
            throw InvalidQueryException.tooComplex();
        }
    }
    
    /**
     * Count total number of conditions in a filter group.
     */
    private int countConditions(ParsedFilterGroup group) {
        int count = 0;
        for (ParsedFilterCondition condition : group.getConditions()) {
            if (condition.getNestedGroup() != null) {
                count += countConditions(condition.getNestedGroup());
            } else {
                count++;
            }
        }
        return count;
    }
}
