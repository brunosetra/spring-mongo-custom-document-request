package br.com.docrequest.dto.request;

import br.com.docrequest.domain.enums.SortDirection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Pagination and sorting configuration for query results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {
    
    /**
     * Zero-based page number.
     * Default: 0
     */
    @Min(value = 0, message = "Page must be >= 0")
    @Builder.Default
    private int page = 0;
    
    /**
     * Number of results per page.
     * Default: 20, Maximum: 100
     */
    @Min(value = 1, message = "Size must be >= 1")
    @Min(value = 100, message = "Size must be <= 100")
    @Builder.Default
    private int size = 20;
    
    /**
     * Sort configuration.
     * Multiple sort fields are applied in order.
     * Default: No sorting (uses natural order)
     */
    @Valid
    private List<SortRequest> sort;
}
