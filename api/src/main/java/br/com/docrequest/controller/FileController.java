package br.com.docrequest.controller;

import br.com.docrequest.service.FileStorageService;
import br.com.docrequest.security.TenantContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "Download files stored in MinIO")
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/**")
    @PreAuthorize("hasRole('ROLE_FILE_DOWNLOAD')")
    @Operation(summary = "Download a file by its MinIO path (fileId)")
    public ResponseEntity<byte[]> downloadFile(
            @RequestParam(required = false) String fileId,
            @RequestHeader(value = "X-File-Id", required = false) String headerFileId) {

        String resolvedFileId = fileId != null ? fileId : headerFileId;
        if (resolvedFileId == null || resolvedFileId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String partId = TenantContext.getCurrentTenant();
        byte[] fileContent = fileStorageService.downloadFile(partId, resolvedFileId);

        // Determine content type from file extension
        String contentType = determineContentType(resolvedFileId);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + extractFileName(resolvedFileId) + "\"")
            .contentType(MediaType.parseMediaType(contentType))
            .body(fileContent);
    }

    private String determineContentType(String fileId) {
        if (fileId.endsWith(".jpg") || fileId.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileId.endsWith(".wsq")) {
            return "application/octet-stream";
        }
        return "application/octet-stream";
    }

    private String extractFileName(String fileId) {
        int lastSlash = fileId.lastIndexOf('/');
        return lastSlash >= 0 ? fileId.substring(lastSlash + 1) : fileId;
    }
}
