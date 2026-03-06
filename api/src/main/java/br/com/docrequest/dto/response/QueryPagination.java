package br.com.docrequest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Pagination information for query results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryPagination {
    
    /**
     * Current page number (zero-based).
     */
    private int page;
    
    /**
     * Number of results per page.
     */
    private int size;
    
    /**
     * Total number of elements across all pages.
     */
    private long totalElements;
    
    /**
     * Total number of pages available.
     */
    private int totalPages;
}
