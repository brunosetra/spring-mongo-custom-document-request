package br.com.docrequest.dto.request;

import br.com.docrequest.domain.enums.ComparisonOperator;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single filter condition or a nested filter group.
 * Supports both leaf conditions (field + operator + value) and nested groups.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterCondition {
    
    /**
     * The field name to filter on.
     * Must correspond to a field in the DocRequestMetadata template.
     * Null if this is a nested group.
     */
    private String field;
    
    /**
     * The comparison operator to apply.
     * Must be compatible with the field type.
     * Null if this is a nested group.
     */
    private ComparisonOperator operator;
    
    /**
     * The value to compare against.
     * Type must match the field type.
     * Null if this is a nested group or for null-checking operators.
     */
    private Object value;
    
    /**
     * Nested filter group for complex logic.
     * If present, field, operator, and value should be null.
     */
    @Valid
    private FilterGroup nestedGroup;
}
