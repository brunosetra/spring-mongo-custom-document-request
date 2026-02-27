package br.com.docrequest.service;

import br.com.docrequest.domain.enums.DocRequestFieldType;
import br.com.docrequest.exception.FileStorageException;
import io.minio.*;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * Service for storing and retrieving files in MinIO.
 * Each tenant (partId) has its own bucket.
 * File path pattern: {docRequestUuid}/{fieldName}/{fileUuid}.{extension}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-prefix:docrequest}")
    private String bucketPrefix;

    /**
     * Processes all file fields in the resolved fields map.
     * Decodes Base64 content, uploads to MinIO, and replaces the field value with the file ID.
     *
     * @param docRequestUuid the UUID of the DocRequest being created
     * @param partId         the tenant identifier
     * @param fields         the resolved fields map (modified in-place)
     * @param fileFieldTypes map of fieldName -> DocRequestFieldType for file fields
     */
    public void processFileFields(String docRequestUuid, String partId,
                                   Map<String, Object> fields,
                                   Map<String, DocRequestFieldType> fileFieldTypes) {
        String bucketName = getBucketName(partId);
        ensureBucketExists(bucketName);

        for (Map.Entry<String, DocRequestFieldType> entry : fileFieldTypes.entrySet()) {
            String fieldName = entry.getKey();
            DocRequestFieldType fieldType = entry.getValue();
            Object value = fields.get(fieldName);

            if (value == null) {
                continue;
            }

            try {
                String fileId = uploadFile(bucketName, docRequestUuid, fieldName, fieldType, value.toString());
                fields.put(fieldName, fileId);
                log.debug("Uploaded file for field '{}' -> fileId: {}", fieldName, fileId);
            } catch (Exception e) {
                throw new FileStorageException("Failed to upload file for field '" + fieldName + "': " + e.getMessage(), e);
            }
        }
    }

    /**
     * Uploads a Base64-encoded file to MinIO and returns the object path (fileId).
     */
    private String uploadFile(String bucketName, String docRequestUuid, String fieldName,
                               DocRequestFieldType fieldType, String base64Content) throws Exception {
        // Strip data URI prefix if present
        String base64 = base64Content;
        if (base64.contains(",")) {
            base64 = base64.substring(base64.indexOf(',') + 1);
        }

        byte[] fileBytes = Base64.getDecoder().decode(base64);
        String extension = getExtension(fieldType);
        String fileUuid = UUID.randomUUID().toString();
        String objectPath = docRequestUuid + "/" + fieldName + "/" + fileUuid + "." + extension;
        String contentType = getContentType(fieldType);

        try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectPath)
                .stream(inputStream, fileBytes.length, -1)
                .contentType(contentType)
                .build());
        }

        return objectPath;
    }

    /**
     * Downloads a file from MinIO and returns its content as a byte array.
     */
    public byte[] downloadFile(String partId, String fileId) {
        String bucketName = getBucketName(partId);
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
            .bucket(bucketName)
            .object(fileId)
            .build())) {
            return stream.readAllBytes();
        } catch (MinioException e) {
            throw new FileStorageException("File not found: " + fileId, e);
        } catch (Exception e) {
            throw new FileStorageException("Failed to download file: " + fileId, e);
        }
    }

    /**
     * Gets the content type for a file based on its field type.
     */
    public String getContentType(DocRequestFieldType fieldType) {
        return switch (fieldType) {
            case FILE_IMG -> "image/jpeg";
            case FILE_WSQ -> "application/octet-stream";
            default -> "application/octet-stream";
        };
    }

    private String getExtension(DocRequestFieldType fieldType) {
        return switch (fieldType) {
            case FILE_IMG -> "jpg";
            case FILE_WSQ -> "wsq";
            default -> "bin";
        };
    }

    private String getBucketName(String partId) {
        return bucketPrefix + "-" + partId.toLowerCase().replaceAll("[^a-z0-9-]", "-");
    }

    private void ensureBucketExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
                log.info("Created MinIO bucket: {}", bucketName);
            }
        } catch (Exception e) {
            throw new FileStorageException("Failed to ensure bucket exists: " + bucketName, e);
        }
    }
}
