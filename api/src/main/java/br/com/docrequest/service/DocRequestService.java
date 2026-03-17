package br.com.docrequest.service;

import br.com.docrequest.domain.document.DocRequest;
import br.com.docrequest.domain.entity.DocRequestFieldMetadata;
import br.com.docrequest.domain.entity.DocRequestMetadata;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.dto.request.DocRequestCreateRequest;
import br.com.docrequest.dto.response.DocRequestResponse;
import br.com.docrequest.exception.ResourceNotFoundException;
import br.com.docrequest.repository.mongo.DocRequestRepository;
import br.com.docrequest.security.TenantContext;
import br.com.docrequest.util.DateFieldConverter;
import br.com.docrequest.validation.DocRequestValidationEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocRequestService {

    private static final Set<DocRequestFieldType> FILE_TYPES = EnumSet.of(
        DocRequestFieldType.FILE,
        DocRequestFieldType.FILE_IMG,
        DocRequestFieldType.FILE_WSQ
    );

    private final DocRequestRepository docRequestRepository;
    private final DocRequestMetadataService metadataService;
    private final DocRequestValidationEngine validationEngine;
    private final FileStorageService fileStorageService;
    private final ExternalValidationService externalValidationService;

    public DocRequestResponse create(DocRequestCreateRequest request) {
        String partId = TenantContext.getCurrentTenant();
        if (partId == null || partId.isBlank()) {
            throw new IllegalStateException("Tenant context (partId) is not set. Ensure JWT contains tenantId claim.");
        }

        // 1. Resolve active metadata template
        DocRequestMetadata metadata = metadataService.findActiveEntityByName(request.getMetadataName());

        if (!metadata.isEnabled()) {
            throw new IllegalStateException("DocRequestMetadata '" + request.getMetadataName() + "' is disabled");
        }

        // 2. Validate and resolve all fields (includes unique field validation)
        Map<String, Object> resolvedFields = validationEngine.validateAndResolve(request.getFields(), metadata);

        // 3. Optional external validation
        if (metadata.getValidationServiceUrl() != null && !metadata.getValidationServiceUrl().isBlank()) {
            externalValidationService.validate(metadata.getValidationServiceUrl(), resolvedFields, metadata);
        }

        // 4. Process file fields - upload to MinIO and replace with file IDs
        String docRequestUuid = UUID.randomUUID().toString();
        Map<String, DocRequestFieldType> fileFields = collectFileFields(metadata);
        if (!fileFields.isEmpty()) {
            fileStorageService.processFileFields(docRequestUuid, partId, resolvedFields, fileFields);
        }

        // 5. Convert date fields from template format to ISO format for MongoDB storage
        Map<String, Object> fieldsForStorage = DateFieldConverter.convertToIsoFormat(resolvedFields, metadata);

        // 6. Save DocRequest to MongoDB
        DocRequest docRequest = DocRequest.builder()
            .uuid(docRequestUuid)
            .partId(partId)
            .docRequestMetadataName(metadata.getName())
            .docRequestMetadataVersion(metadata.getVersion())
            .fields(fieldsForStorage)
            .build();

        DocRequest saved = docRequestRepository.save(docRequest);
        log.info("Created DocRequest: {} for tenant: {} using template: {} v{}",
            saved.getUuid(), partId, metadata.getName(), metadata.getVersion());

        return toResponse(saved, metadata);
    }

    public DocRequestResponse findByUuid(String uuid) {
        String partId = TenantContext.getCurrentTenant();
        return docRequestRepository.findByUuidAndPartId(uuid, partId)
            .map(this::toResponse)
            .orElseThrow(() -> ResourceNotFoundException.of("DocRequest", uuid));
    }

    public DocRequestResponse findByUuidWithFiles(String uuid) {
        String partId = TenantContext.getCurrentTenant();
        DocRequest docRequest = docRequestRepository.findByUuidAndPartId(uuid, partId)
            .orElseThrow(() -> ResourceNotFoundException.of("DocRequest", uuid));

        // Get the metadata to identify file fields - use the exact version used when creating the DocRequest
        DocRequestMetadata metadata = metadataService.findEntityByNameAndVersion(
            docRequest.getDocRequestMetadataName(),
            docRequest.getDocRequestMetadataVersion()
        );
        Map<String, DocRequestFieldType> fileFields = collectFileFields(metadata);

        // Create a copy of fields to avoid modifying the original
        Map<String, Object> fieldsWithFiles = new HashMap<>(docRequest.getFields());

        // Download and convert files to Base64 in parallel
        if (!fileFields.isEmpty()) {
            Map<String, CompletableFuture<String>> downloadFutures = new HashMap<>();

            // Submit all download tasks in parallel
            for (Map.Entry<String, DocRequestFieldType> entry : fileFields.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = fieldsWithFiles.get(fieldName);

                if (fieldValue != null) {
                    String fileId = fieldValue.toString();
                    downloadFutures.put(fieldName, CompletableFuture.supplyAsync(() -> {
                        try {
                            byte[] fileBytes = fileStorageService.downloadFile(partId, fileId);
                            return Base64.getEncoder().encodeToString(fileBytes);
                        } catch (Exception e) {
                            log.error("Failed to download file for field '{}': {}", fieldName, e.getMessage());
                            return null; // Return null on failure
                        }
                    }));
                }
            }

            // Wait for all downloads to complete and update fields
            downloadFutures.forEach((fieldName, future) -> {
                try {
                    String base64Content = future.get(); // This blocks until the future completes
                    if (base64Content != null) {
                        fieldsWithFiles.put(fieldName, base64Content);
                        log.debug("Converted file for field '{}' to Base64", fieldName);
                    }
                    // If base64Content is null, keep the original fileId (download failed)
                } catch (Exception e) {
                    log.error("Error processing future for field '{}': {}", fieldName, e.getMessage());
                    // Keep the original fileId if future processing fails
                }
            });
        }

        // Convert date fields from ISO format to template format
        Map<String, Object> fieldsForResponse = DateFieldConverter.convertToTemplateFormat(
            fieldsWithFiles, 
            metadata
        );

        return DocRequestResponse.builder()
            .uuid(docRequest.getUuid())
            .partId(docRequest.getPartId())
            .docRequestMetadataName(docRequest.getDocRequestMetadataName())
            .docRequestMetadataVersion(docRequest.getDocRequestMetadataVersion())
            .fields(fieldsForResponse)
            .createdAt(docRequest.getCreatedAt())
            .updatedAt(docRequest.getUpdatedAt())
            .build();
    }

    public Page<DocRequestResponse> findAll(Pageable pageable) {
        String partId = TenantContext.getCurrentTenant();
        return docRequestRepository.findByPartId(partId, pageable)
            .map(this::toResponse);
    }

    public Page<DocRequestResponse> findByMetadataName(String metadataName, Pageable pageable) {
        String partId = TenantContext.getCurrentTenant();
        return docRequestRepository.findByPartIdAndDocRequestMetadataName(partId, metadataName, pageable)
            .map(this::toResponse);
    }

    private Map<String, DocRequestFieldType> collectFileFields(DocRequestMetadata metadata) {
        Map<String, DocRequestFieldType> fileFields = new HashMap<>();
        for (DocRequestFieldMetadata field : metadata.getFields()) {
            if (FILE_TYPES.contains(field.getType())) {
                fileFields.put(field.getName(), field.getType());
            }
        }
        return fileFields;
    }

    

    private DocRequestResponse toResponse(DocRequest docRequest) {
        // For list methods, we need to fetch the metadata
        DocRequestMetadata metadata = metadataService.findEntityByNameAndVersion(
            docRequest.getDocRequestMetadataName(),
            docRequest.getDocRequestMetadataVersion()
        );
        return toResponse(docRequest, metadata);
    }

    private DocRequestResponse toResponse(DocRequest docRequest, DocRequestMetadata metadata) {
        // Convert date fields from ISO format to template format
        Map<String, Object> fieldsForResponse = DateFieldConverter.convertToTemplateFormat(
            docRequest.getFields(), 
            metadata
        );
        
        return DocRequestResponse.builder()
            .uuid(docRequest.getUuid())
            .partId(docRequest.getPartId())
            .docRequestMetadataName(docRequest.getDocRequestMetadataName())
            .docRequestMetadataVersion(docRequest.getDocRequestMetadataVersion())
            .fields(fieldsForResponse)
            .createdAt(docRequest.getCreatedAt())
            .updatedAt(docRequest.getUpdatedAt())
            .build();
    }
}
