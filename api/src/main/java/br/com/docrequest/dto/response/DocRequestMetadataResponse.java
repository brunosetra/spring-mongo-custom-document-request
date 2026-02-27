package br.com.docrequest.dto.response;

import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DocRequestMetadataResponse {

    private String uuid;
    private String name;
    private String description;
    private boolean enabled;
    private int version;
    private String validationServiceUrl;
    private List<FieldMetadataResponse> fields;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class FieldMetadataResponse {
        private String uuid;
        private String name;
        private String description;
        private DocRequestFieldType type;
        private DocRequestFieldInputType inputType;
        private String defaultValue;
        private String format;
        private Integer min;
        private Integer max;
        private boolean required;
        private boolean editable;
        private String errorCodeReference;
        private int fieldOrder;
    }
}
