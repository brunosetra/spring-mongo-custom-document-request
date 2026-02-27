package br.com.docrequest.controller;

import br.com.docrequest.dto.request.DocRequestMetadataCreateRequest;
import br.com.docrequest.dto.response.DocRequestMetadataResponse;
import br.com.docrequest.service.DocRequestMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doc-request-metadata")
@RequiredArgsConstructor
@Tag(name = "DocRequest Metadata", description = "Manage document request template metadata")
public class DocRequestMetadataController {

    private final DocRequestMetadataService metadataService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_METADATA_CREATE')")
    @Operation(summary = "Create a new DocRequest metadata template")
    public ResponseEntity<DocRequestMetadataResponse> create(
            @Valid @RequestBody DocRequestMetadataCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(metadataService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_METADATA_READ')")
    @Operation(summary = "List all active metadata templates")
    public ResponseEntity<List<DocRequestMetadataResponse>> findAllActive() {
        return ResponseEntity.ok(metadataService.findAllActive());
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasRole('ROLE_METADATA_READ')")
    @Operation(summary = "Get the active version of a metadata template by name")
    public ResponseEntity<DocRequestMetadataResponse> findActiveByName(@PathVariable String name) {
        return ResponseEntity.ok(metadataService.findActiveByName(name));
    }

    @GetMapping("/{name}/versions")
    @PreAuthorize("hasRole('ROLE_METADATA_READ')")
    @Operation(summary = "List all versions of a metadata template")
    public ResponseEntity<List<DocRequestMetadataResponse>> findAllVersions(@PathVariable String name) {
        return ResponseEntity.ok(metadataService.findAllVersionsByName(name));
    }

    @GetMapping("/{name}/versions/{version}")
    @PreAuthorize("hasRole('ROLE_METADATA_READ')")
    @Operation(summary = "Get a specific version of a metadata template")
    public ResponseEntity<DocRequestMetadataResponse> findByNameAndVersion(
            @PathVariable String name, @PathVariable int version) {
        return ResponseEntity.ok(metadataService.findByNameAndVersion(name, version));
    }

    @PutMapping("/{name}")
    @PreAuthorize("hasRole('ROLE_METADATA_UPDATE')")
    @Operation(summary = "Update a metadata template - creates a new version and disables the current one")
    public ResponseEntity<DocRequestMetadataResponse> update(
            @PathVariable String name,
            @Valid @RequestBody DocRequestMetadataCreateRequest request) {
        return ResponseEntity.ok(metadataService.update(name, request));
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ROLE_METADATA_DELETE')")
    @Operation(summary = "Disable all versions of a metadata template")
    public ResponseEntity<Void> disable(@PathVariable String name) {
        metadataService.disable(name);
        return ResponseEntity.noContent().build();
    }
}
