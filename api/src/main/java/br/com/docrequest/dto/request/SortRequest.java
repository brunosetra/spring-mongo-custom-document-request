package br.com.docrequest.dto.request;

import br.com.docrequest.domain.enums.SortDirection;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sort configuration for a single field.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortRequest {
    
    /**
     * The field name to sort by.
     * Can be a dynamic field name or system fields like "createdAt" or "updatedAt".
     */
    @NotBlank(message = "Sort field is required")
    private String field;
    
    /**
     * Sort direction.
     * Default: ASC
     */
    @Builder.Default
    private SortDirection direction = SortDirection.ASC;
}
