package br.com.docrequest.dto.request;

import br.com.docrequest.domain.enums.LogicalOperator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for executing advanced queries on DocRequest entities.
 * Supports complex filtering with nested logical operators.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {
    
    /**
     * The name of the DocRequestMetadata template to query.
     * Must be an existing, enabled template.
     */
    @NotBlank(message = "Template name is required")
    private String templateName;
    
    /**
     * The filter conditions to apply to the query.
     * Supports nested logical operators (AND, OR, NOT).
     */
    @NotNull(message = "Filters are required")
    @Valid
    private FilterGroup filters;
    
    /**
     * Pagination and sorting configuration.
     * Optional - defaults to page 0, size 20, no sorting.
     */
    @Valid
    private PaginationRequest pagination;
}
