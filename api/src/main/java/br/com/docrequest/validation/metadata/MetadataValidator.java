package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.response.MetadataValidationError;

import java.util.List;

/**
 * Strategy interface for DocRequestMetadata validation.
 * Each implementation handles a specific validation concern.
 * Follows the Strategy pattern for flexible validation composition.
 */
public interface MetadataValidator {

    /**
     * Validates the DocRequestMetadata and returns a list of validation errors.
     * Returns an empty list if validation passes.
     *
     * @param metadata the DocRequestMetadata to validate
     * @return list of validation errors, empty if valid
     */
    List<MetadataValidationError> validate(DocRequestMetadata metadata);

    /**
     * Returns the order in which this validator should be executed.
     * Lower values execute first.
     *
     * @return the execution order
     */
    default int getOrder() {
        return 100;
    }
}
