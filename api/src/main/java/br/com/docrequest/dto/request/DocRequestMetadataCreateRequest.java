package br.com.docrequest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DocRequestMetadataCreateRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @Size(max = 500, message = "Validation service URL must not exceed 500 characters")
    private String validationServiceUrl;

    @NotEmpty(message = "At least one field is required")
    @Valid
    private List<DocRequestFieldMetadataRequest> fields = new ArrayList<>();
}
