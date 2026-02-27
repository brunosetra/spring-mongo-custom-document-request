package br.com.docrequest.dto.request;

import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocRequestFieldMetadataRequest {

    @NotBlank(message = "Field name is required")
    @Size(max = 100, message = "Field name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Field type is required")
    private DocRequestFieldType type;

    @NotNull(message = "Input type is required")
    private DocRequestFieldInputType inputType;

    @Size(max = 500, message = "Default value must not exceed 500 characters")
    private String defaultValue;

    @Size(max = 500, message = "Format must not exceed 500 characters")
    private String format;

    @Min(value = 0, message = "Min must be non-negative")
    private Integer min;

    @Min(value = 0, message = "Max must be non-negative")
    private Integer max;

    private boolean required = false;
    private boolean editable = true;

    @Size(max = 100, message = "Error code reference must not exceed 100 characters")
    private String errorCodeReference;

    @Min(value = 0, message = "Field order must be non-negative")
    private int fieldOrder = 0;
}
