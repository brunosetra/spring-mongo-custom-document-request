package br.com.docrequest.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single row in a DomainTable.
 * Values are stored as a JSON map of column name -> value.
 */
@Entity
@Table(name = "domain_table_row")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomainTableRow implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "uuid", updatable = false, nullable = false)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domain_table_uuid", nullable = false,
        foreignKey = @ForeignKey(name = "fk_domain_table_row_table"))
    private DomainTable domainTable;

    /**
     * Map of column name -> value for this row.
     * Stored as JSONB in PostgreSQL for flexible schema.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "values", nullable = false, columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> values = new HashMap<>();
}
