package br.com.docrequest.controller;

import br.com.docrequest.dto.request.DocRequestCreateRequest;
import br.com.docrequest.dto.request.QueryRequest;
import br.com.docrequest.dto.response.DocRequestResponse;
import br.com.docrequest.dto.response.QueryResponse;
import br.com.docrequest.service.DocRequestQueryService;
import br.com.docrequest.service.DocRequestService;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doc-requests")
@RequiredArgsConstructor
@Tag(name = "DocRequests", description = "Submit and retrieve document requests")
public class DocRequestController {

    private final DocRequestService docRequestService;
    private final DocRequestQueryService docRequestQueryService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_DOC_REQUEST_CREATE')")
    @Operation(summary = "Submit a new document request")
    public ResponseEntity<DocRequestResponse> create(@Valid @RequestBody DocRequestCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(docRequestService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_DOC_REQUEST_READ')")
    @Operation(summary = "List document requests for the current tenant")
    public ResponseEntity<Page<DocRequestResponse>> findAll(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(docRequestService.findAll(pageable));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasRole('ROLE_DOC_REQUEST_READ')")
    @Operation(summary = "Get a specific document request by UUID")
    public ResponseEntity<DocRequestResponse> findByUuid(@PathVariable String uuid) {
        return ResponseEntity.ok(docRequestService.findByUuid(uuid));
    }

    @GetMapping("/{uuid}/with-files")
    @PreAuthorize("hasRole('ROLE_DOC_REQUEST_READ')")
    @Operation(summary = "Get a specific document request by UUID with files converted to Base64")
    public ResponseEntity<DocRequestResponse> findByUuidWithFiles(@PathVariable String uuid) {
        return ResponseEntity.ok(docRequestService.findByUuidWithFiles(uuid));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_DOC_REQUEST_READ')")
    @Operation(summary = "Search document requests by metadata template name")
    public ResponseEntity<Page<DocRequestResponse>> findByMetadataName(
            @RequestParam String metadataName,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(docRequestService.findByMetadataName(metadataName, pageable));
    }
    
    @PostMapping("/querys")
    @PreAuthorize("hasRole('ROLE_DOC_REQUEST_READ')")
    @Operation(summary = "Execute advanced query on document requests")
    public ResponseEntity<QueryResponse> query(@Valid @RequestBody QueryRequest request) {
        return ResponseEntity.ok(docRequestQueryService.query(request));
    }
}
