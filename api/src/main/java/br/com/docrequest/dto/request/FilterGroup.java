package br.com.docrequest.dto.request;

import br.com.docrequest.domain.enums.LogicalOperator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a group of filter conditions combined with a logical operator.
 * Supports nested groups for complex query logic.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterGroup {
    
    /**
     * The logical operator to combine conditions.
     * AND: All conditions must be true
     * OR: At least one condition must be true
     * NOT: Negates the condition(s)
     */
    @NotNull(message = "Operator is required")
    private LogicalOperator operator;
    
    /**
     * List of filter conditions or nested filter groups.
     * Must contain at least one condition.
     */
    @NotEmpty(message = "Conditions cannot be empty")
    @Valid
    private List<FilterCondition> conditions;
}
