package br.com.docrequest.query;

import br.com.docrequest.domain.enums.LogicalOperator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Internal representation of a parsed filter group.
 * Contains validated conditions with type information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedFilterGroup {
    
    /**
     * The logical operator to combine conditions.
     */
    private LogicalOperator operator;
    
    /**
     * List of parsed filter conditions.
     * May contain nested groups.
     */
    private List<ParsedFilterCondition> conditions;
}
