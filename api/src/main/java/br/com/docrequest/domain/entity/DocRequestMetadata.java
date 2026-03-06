package br.com.docrequest.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Template that defines the structure and validation rules for a DocRequest.
 * Versioned: each update creates a new version; the old version is disabled.
 * The logical identifier is the 'name' field.
 */
@Entity
@Table(name = "doc_request_metadata",
    uniqueConstraints = @UniqueConstraint(name = "uk_doc_request_metadata_name_version",
        columnNames = {"name", "version"}))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocRequestMetadata implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", updatable = false, nullable = false)
    private String uuid;

    /**
     * Logical identifier for the template. Shared across all versions.
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    /**
     * Whether this version is the active/current version.
     * Only one version per name should have enabled=true at any time.
     */
    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    /**
     * Version number. Starts at 1 and increments on each update.
     */
    @Column(name = "version", nullable = false)
    @Builder.Default
    private int version = 1;

    /**
     * Optional URL of an external validation service to call after internal validation.
     */
    @Column(name = "validation_service_url", length = 500)
    private String validationServiceUrl;

    @OneToMany(mappedBy = "docRequestMetadata", cascade = CascadeType.ALL,
        orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("fieldOrder ASC")
    @JsonManagedReference
    @Builder.Default
    private List<DocRequestFieldMetadata> fields = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
