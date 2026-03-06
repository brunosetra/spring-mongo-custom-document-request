package br.com.docrequest.dto.response;

import br.com.docrequest.dto.response.DocRequestResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for query results.
 * Contains the paginated results and execution metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {
    
    /**
     * The query results for the current page.
     */
    private List<DocRequestResponse> results;
    
    /**
     * Pagination information.
     */
    private QueryPagination pagination;
    
    /**
     * Query execution time in milliseconds.
     * Useful for performance monitoring.
     */
    private long executionTimeMs;
}
