package br.com.docrequest.domain.enums;

/**
 * Defines how the value of a document request field is sourced and processed.
 */
public enum DocRequestFieldInputType {

    /**
     * Value must be provided by the caller in the request payload.
     */
    IN("Entrada"),

    /**
     * Value is always the configured defaultValue. Input from the caller is ignored.
     */
    FIXED("Fixo"),

    /**
     * Value is computed from other field values using the format expression.
     * Example: format = "${firstName} ${lastName}"
     */
    CALCULATED("Calculado"),

    /**
     * If the caller does not provide a value, the defaultValue is used.
     * If the caller provides a value, it is validated and used.
     */
    DEFAULT("Padrão"),

    /**
     * Value must exist as a key in the referenced DomainTable.
     * The defaultValue field holds the DomainTable name.
     */
    DOMAIN("Tabela de domínio"),

    /**
     * Looks up a row in the DomainTable by key, then computes the final value
     * using the format expression against the row's columns.
     * defaultValue -> DomainTable name
     * format -> expression like "${nome}-${regiao}"
     */
    DOMAIN_CALCULATED("Calculado de tabela de domínio"),

    /**
     * Value is set internally by the system. Not expected in the request payload.
     */
    INTERNAL("Interno"),

    /**
     * Automatically generates incrementing integer values.
     * The field must have type INTEGER.
     * Values are unique within the same tenant and metadata template.
     */
    AUTO_INC("Auto Incremento");

    public final String description;

    DocRequestFieldInputType(String description) {
        this.description = description;
    }
}
