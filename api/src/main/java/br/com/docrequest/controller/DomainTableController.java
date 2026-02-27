package br.com.docrequest.controller;

import br.com.docrequest.dto.request.DomainTableCreateRequest;
import br.com.docrequest.dto.response.DomainTableResponse;
import br.com.docrequest.service.DomainTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/domain-tables")
@RequiredArgsConstructor
@Tag(name = "Domain Tables", description = "Manage reference domain tables")
public class DomainTableController {

    private final DomainTableService domainTableService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_DOMAIN_TABLE_CREATE')")
    @Operation(summary = "Create a new domain table")
    public ResponseEntity<DomainTableResponse> create(@Valid @RequestBody DomainTableCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(domainTableService.create(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_DOMAIN_TABLE_READ')")
    @Operation(summary = "List all domain tables")
    public ResponseEntity<List<DomainTableResponse>> findAll() {
        return ResponseEntity.ok(domainTableService.findAll());
    }

    @GetMapping("/{name}")
    @PreAuthorize("hasRole('ROLE_DOMAIN_TABLE_READ')")
    @Operation(summary = "Get a domain table by name")
    public ResponseEntity<DomainTableResponse> findByName(@PathVariable String name) {
        return ResponseEntity.ok(domainTableService.findByNameAsResponse(name));
    }

    @PutMapping("/{name}")
    @PreAuthorize("hasRole('ROLE_DOMAIN_TABLE_UPDATE')")
    @Operation(summary = "Update a domain table")
    public ResponseEntity<DomainTableResponse> update(@PathVariable String name,
                                                       @Valid @RequestBody DomainTableCreateRequest request) {
        return ResponseEntity.ok(domainTableService.update(name, request));
    }

    @DeleteMapping("/{name}")
    @PreAuthorize("hasRole('ROLE_DOMAIN_TABLE_DELETE')")
    @Operation(summary = "Delete a domain table")
    public ResponseEntity<Void> delete(@PathVariable String name) {
        domainTableService.delete(name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{name}/rows")
    @PreAuthorize("hasRole('ROLE_DOMAIN_TABLE_UPDATE')")
    @Operation(summary = "Add rows to a domain table")
    public ResponseEntity<DomainTableResponse> addRows(@PathVariable String name,
                                                        @RequestBody List<Map<String, String>> rows) {
        return ResponseEntity.ok(domainTableService.addRows(name, rows));
    }
}
