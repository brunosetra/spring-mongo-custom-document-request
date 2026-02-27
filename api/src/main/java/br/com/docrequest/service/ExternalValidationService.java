package br.com.docrequest.service;

import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.dto.response.FieldValidationError;
import br.com.docrequest.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Service for calling optional external validation endpoints.
 * The external service receives the resolved fields and can return validation errors.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalValidationService {

    private final RestTemplate restTemplate;

    @Value("${app.validation.external-timeout-seconds:10}")
    private int timeoutSeconds;

    /**
     * Calls the external validation service URL with the resolved fields.
     * The external service is expected to return a list of FieldValidationError objects
     * if validation fails, or an empty list / 2xx response if validation passes.
     *
     * @param validationServiceUrl the URL to call
     * @param resolvedFields       the resolved field values
     * @param metadata             the DocRequestMetadata for context
     * @throws ValidationException if the external service returns validation errors
     */
    @SuppressWarnings("unchecked")
    public void validate(String validationServiceUrl, Map<String, Object> resolvedFields,
                         DocRequestMetadata metadata) {
        log.debug("Calling external validation service: {} for template: {}",
            validationServiceUrl, metadata.getName());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of(
                "metadataName", metadata.getName(),
                "metadataVersion", metadata.getVersion(),
                "fields", resolvedFields
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(validationServiceUrl, request, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("errors")) {
                List<?> errors = (List<?>) response.getBody().get("errors");
                if (errors != null && !errors.isEmpty()) {
                    List<FieldValidationError> validationErrors = errors.stream()
                        .filter(e -> e instanceof Map)
                        .map(e -> {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> errorMap = (Map<String, Object>) e;
                            return FieldValidationError.of(
                                String.valueOf(errorMap.getOrDefault("field", "unknown")),
                                String.valueOf(errorMap.getOrDefault("errorCode", "ERR_EXTERNAL")),
                                String.valueOf(errorMap.getOrDefault("message", "External validation failed"))
                            );
                        })
                        .toList();
                    throw new ValidationException("External validation failed", validationErrors);
                }
            }

            log.debug("External validation passed for template: {}", metadata.getName());
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("External validation service call failed: {}", e.getMessage());
            throw new ValidationException("External validation service unavailable",
                List.of(FieldValidationError.of("_external", "ERR_EXTERNAL_SERVICE",
                    "External validation service call failed: " + e.getMessage())));
        }
    }
}
