package br.com.docrequest.domain.enums;

/**
 * Defines the data type of a document request field.
 * Each type is associated with a specific validator implementation.
 */
public enum DocRequestFieldType {

    STRING("Texto"),
    INTEGER("Inteiro"),
    DOUBLE("Decimal"),
    DATE("Data"),
    DATETIME("Data/Hora"),
    EXPIRATION_DATE("Data de validade"),
    BOOLEAN("Lógico"),
    CPF("CPF"),
    EMAIL("E-mail"),
    EMAIL_ALTERNATIVE("E-mail Alternativo"),
    LIST_STRING("Lista de Texto"),
    LIST_INT("Lista de Inteiro"),
    LIST_DOUBLE("Lista de Decimal"),
    FILE("Base64"),
    FILE_IMG("Imagem Base64"),
    FILE_WSQ("Digital Base64"),
    NAME("Nome"),
    PROFILES("Profiles");

    public final String description;

    DocRequestFieldType(String description) {
        this.description = description;
    }
}
