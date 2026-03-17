package br.com.docrequest.domain.entity;

import br.com.docrequest.domain.enums.DocRequestFieldInputType;
import br.com.docrequest.domain.enums.DocRequestFieldType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Defines a single field within a DocRequestMetadata template.
 * Contains type, input source, validation constraints, and error codes.
 */
@Entity
@Table(name = "doc_request_field_metadata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocRequestFieldMetadata implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", updatable = false, nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_request_metadata_uuid", nullable = false,
        foreignKey = @ForeignKey(name = "fk_field_metadata_doc_request_metadata"))
    @JsonBackReference
    private DocRequestMetadata docRequestMetadata;

    /**
     * The field name/key used in the DocRequest fields map.
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    /**
     * The data type of this field. Determines which validator is used.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private DocRequestFieldType type;

    /**
     * How the value is sourced (from input, fixed, calculated, domain table, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "input_type", nullable = false, length = 50)
    private DocRequestFieldInputType inputType;

    /**
     * Default value or reference depending on inputType:
     * - DEFAULT: the fallback value if not provided
     * - FIXED: the always-used value
     * - DOMAIN / DOMAIN_CALCULATED: the DomainTable name to reference
     */
    @Column(name = "default_value", length = 500)
    private String defaultValue;

    /**
     * Expression template for CALCULATED and DOMAIN_CALCULATED types.
     * Example: "${firstName} ${lastName}" or "${nome}-${regiao}"
     */
    @Column(name = "format", length = 500)
    private String format;

    /**
     * Minimum value (for numbers) or minimum length (for strings/lists).
     */
    @Column(name = "min_value")
    private Integer min;

    /**
     * Maximum value (for numbers) or maximum length (for strings/lists).
     */
    @Column(name = "max_value")
    private Integer max;

    @Column(name = "required", nullable = false)
    @Builder.Default
    private boolean required = false;

    /**
     * Whether the caller can modify this field after initial submission.
     */
    @Column(name = "editable", nullable = false)
    @Builder.Default
    private boolean editable = true;

    /**
     * Custom error code reference to return in validation error responses.
     */
    @Column(name = "error_code_reference", length = 100)
    private String errorCodeReference;

    /**
     * Display order of this field within the template.
     */
    @Column(name = "field_order", nullable = false)
    @Builder.Default
    private int fieldOrder = 0;

    /**
     * Whether this field must have a unique value across all documents
     * using the same template within the same tenant.
     */
    @Column(name = "is_unique", nullable = false)
    @Builder.Default
    private boolean unique = false;
}
