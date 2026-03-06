package br.com.docrequest.query;

import br.com.docrequest.domain.entity.DocRequestMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Internal representation of a parsed query.
 * Contains validated and enriched query information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedQuery {
    
    /**
     * The template name being queried.
     */
    private String templateName;
    
    /**
     * The metadata template definition.
     * Used for field type validation and lookup.
     */
    private DocRequestMetadata metadata;
    
    /**
     * Parsed filter conditions.
     */
    private ParsedFilterGroup filters;
    
    /**
     * Parsed pagination configuration.
     */
    private ParsedPagination pagination;
}
