package br.com.docrequest.query;

import br.com.docrequest.domain.enums.SortDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Internal representation of parsed pagination configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedPagination {
    
    /**
     * Zero-based page number.
     */
    private int page;
    
    /**
     * Number of results per page.
     */
    private int size;
    
    /**
     * Sort configuration.
     */
    private List<SortConfig> sort;
    
    /**
     * Internal sort configuration.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SortConfig {
        private String field;
        private SortDirection direction;
    }
}
