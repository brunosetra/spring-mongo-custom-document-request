package br.com.docrequest.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
public class DocRequestResponse {

    private String uuid;
    private String partId;
    private String docRequestMetadataName;
    private int docRequestMetadataVersion;
    private Map<String, Object> fields;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
