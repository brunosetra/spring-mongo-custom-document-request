package br.com.docrequest.domain.entity;

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
 * Represents a domain reference table (e.g., Brazilian states, document types).
 * Contains a list of columns and rows with key-value data.
 */
@Entity
@Table(name = "domain_table",
    uniqueConstraints = @UniqueConstraint(name = "uk_domain_table_name", columnNames = "name"))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainTable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", updatable = false, nullable = false)
    private String uuid;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    /**
     * The column that serves as the unique key/identifier for each row.
     */
    @Column(name = "column_id", nullable = false, length = 100)
    private String columnId;

    /**
     * Ordered list of column names for this table.
     * Stored as a JSON array in PostgreSQL.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "domain_table_column",
        joinColumns = @JoinColumn(name = "domain_table_uuid"),
        foreignKey = @ForeignKey(name = "fk_domain_table_column_table"))
    @Column(name = "column_name", nullable = false, length = 100)
    @OrderColumn(name = "column_order")
    @Builder.Default
    private List<String> columns = new ArrayList<>();

    @OneToMany(mappedBy = "domainTable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DomainTableRow> rows = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
