package br.com.docrequest.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class DocRequestCreateRequest {

    @NotBlank(message = "Metadata name is required")
    private String metadataName;

    /**
     * Dynamic field map. Keys are field names defined in the DocRequestMetadata template.
     * Values can be strings, numbers, booleans, lists, or base64-encoded file content.
     */
    private Map<String, Object> fields = new HashMap<>();
}
