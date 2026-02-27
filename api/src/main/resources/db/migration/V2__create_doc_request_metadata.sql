-- ============================================================
-- V2: Create doc_request_metadata and doc_request_field_metadata tables
-- ============================================================

CREATE TABLE IF NOT EXISTS doc_request_metadata (
    uuid                   VARCHAR(36)  NOT NULL,
    name                   VARCHAR(100) NOT NULL,
    description            VARCHAR(500),
    enabled                BOOLEAN      NOT NULL DEFAULT TRUE,
    version                INTEGER      NOT NULL DEFAULT 1,
    validation_service_url VARCHAR(500),
    created_at             TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_doc_request_metadata PRIMARY KEY (uuid),
    CONSTRAINT uk_doc_request_metadata_name_version UNIQUE (name, version)
);

CREATE INDEX IF NOT EXISTS idx_doc_request_metadata_name ON doc_request_metadata (name);
CREATE INDEX IF NOT EXISTS idx_doc_request_metadata_name_enabled ON doc_request_metadata (name, enabled);

CREATE TABLE IF NOT EXISTS doc_request_field_metadata (
    uuid                      VARCHAR(36)  NOT NULL,
    doc_request_metadata_uuid VARCHAR(36)  NOT NULL,
    name                      VARCHAR(100) NOT NULL,
    description               VARCHAR(500),
    type                      VARCHAR(50)  NOT NULL,
    input_type                VARCHAR(50)  NOT NULL,
    default_value             VARCHAR(500),
    format                    VARCHAR(500),
    min_value                 INTEGER,
    max_value                 INTEGER,
    required                  BOOLEAN      NOT NULL DEFAULT FALSE,
    editable                  BOOLEAN      NOT NULL DEFAULT TRUE,
    error_code_reference      VARCHAR(100),
    field_order               INTEGER      NOT NULL DEFAULT 0,
    CONSTRAINT pk_doc_request_field_metadata PRIMARY KEY (uuid),
    CONSTRAINT fk_field_metadata_doc_request_metadata
        FOREIGN KEY (doc_request_metadata_uuid) REFERENCES doc_request_metadata (uuid) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_field_metadata_metadata_uuid ON doc_request_field_metadata (doc_request_metadata_uuid);
