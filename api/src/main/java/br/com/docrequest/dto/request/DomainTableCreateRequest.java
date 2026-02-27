package br.com.docrequest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class DomainTableCreateRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Column ID is required")
    @Size(max = 100, message = "Column ID must not exceed 100 characters")
    private String columnId;

    @NotEmpty(message = "At least one column is required")
    private List<String> columns = new ArrayList<>();

    @Valid
    private List<Map<String, String>> rows = new ArrayList<>();
}
