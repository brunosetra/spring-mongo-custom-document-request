package br.com.docrequest.query;

import br.com.docrequest.domain.enums.ComparisonOperator;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal representation of a parsed filter condition.
 * Contains validated field information with type metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedFilterCondition {
    
    /**
     * The field name to filter on.
     * Null if this is a nested group.
     */
    private String field;
    
    /**
     * The comparison operator.
     * Null if this is a nested group.
     */
    private ComparisonOperator operator;
    
    /**
     * The value to compare against.
     * Null if this is a nested group or for null-checking operators.
     */
    private Object value;
    
    /**
     * The field type from metadata.
     * Used for type-safe query building.
     */
    private DocRequestFieldType fieldType;
    
    /**
     * Nested filter group for complex logic.
     * If present, field, operator, value, and fieldType should be null.
     */
    private ParsedFilterGroup nestedGroup;
}
