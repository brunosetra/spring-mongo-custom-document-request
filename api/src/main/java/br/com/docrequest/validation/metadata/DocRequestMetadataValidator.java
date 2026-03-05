package br.com.docrequest.validation.metadata;

import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.response.MetadataValidationError;
import br.com.docrequest.exception.InvalidDocRequestMetadataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Main validator orchestrator for DocRequestMetadata.
 * Coordinates all metadata validators and aggregates validation errors.
 * 
 * Follows the Chain of Responsibility pattern with ordered validators.
 * Each validator is executed in order, and all errors are collected before throwing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DocRequestMetadataValidator {

    private final List<MetadataValidator> validators;

    /**
     * Validates the DocRequestMetadata using all registered validators.
     * Throws InvalidDocRequestMetadataException if any validation fails.
     *
     * @param metadata the DocRequestMetadata to validate
     * @throws InvalidDocRequestMetadataException if validation fails
     */
    public void validate(DocRequestMetadata metadata) {
        if (metadata == null) {
            throw new InvalidDocRequestMetadataException("DocRequestMetadata must not be null");
        }

        List<MetadataValidationError> allErrors = new ArrayList<>();

        // Sort validators by order and execute each
        validators.stream()
                .sorted(Comparator.comparingInt(MetadataValidator::getOrder))
                .forEach(validator -> {
                    try {
                        List<MetadataValidationError> errors = validator.validate(metadata);
                        allErrors.addAll(errors);
                    } catch (Exception e) {
                        log.error("Unexpected error during validation: {}", e.getMessage(), e);
                        allErrors.add(MetadataValidationError.of(
                                "Unexpected validation error: " + e.getMessage()
                        ));
                    }
                });

        // Throw exception if any errors were found
        if (!allErrors.isEmpty()) {
            log.debug("DocRequestMetadata validation failed with {} error(s)", allErrors.size());
            throw new InvalidDocRequestMetadataException(allErrors);
        }

        log.debug("DocRequestMetadata validation passed successfully");
    }
}
