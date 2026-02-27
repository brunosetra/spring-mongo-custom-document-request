package br.com.docrequest.domain.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * MongoDB document representing a submitted document request.
 * Fields are dynamic and validated against the associated DocRequestMetadata template.
 * Multi-tenant: partId is extracted from the JWT token and used for query isolation.
 */
@Document(collection = "doc_requests")
@CompoundIndexes({
    @CompoundIndex(name = "idx_part_id_metadata_name", def = "{'partId': 1, 'docRequestMetadataName': 1}"),
    @CompoundIndex(name = "idx_part_id_created_at", def = "{'partId': 1, 'createdAt': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocRequest {

    @Id
    private String id;

    @Indexed
    @Field("uuid")
    private String uuid;

    /**
     * Tenant identifier extracted from the JWT token claim.
     * Used for multi-tenant query isolation.
     */
    @Indexed
    @Field("partId")
    private String partId;

    /**
     * The logical name of the DocRequestMetadata template used.
     */
    @Field("docRequestMetadataName")
    private String docRequestMetadataName;

    /**
     * The specific version of the DocRequestMetadata template used at submission time.
     */
    @Field("docRequestMetadataVersion")
    private int docRequestMetadataVersion;

    /**
     * Dynamic map of field name -> value.
     * Structure varies per template. File fields contain MinIO object IDs.
     */
    @Field("fields")
    @Builder.Default
    private Map<String, Object> fields = new HashMap<>();

    @CreatedDate
    @Field("createdAt")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updatedAt")
    private LocalDateTime updatedAt;
}
