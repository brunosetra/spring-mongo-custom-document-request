-- ============================================================
-- V1: Create domain_table and domain_table_row tables
-- ============================================================

CREATE TABLE IF NOT EXISTS domain_table (
    uuid        VARCHAR(36)  NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    column_id   VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_domain_table PRIMARY KEY (uuid),
    CONSTRAINT uk_domain_table_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS domain_table_column (
    domain_table_uuid VARCHAR(36)  NOT NULL,
    column_name       VARCHAR(100) NOT NULL,
    column_order      INTEGER      NOT NULL DEFAULT 0,
    CONSTRAINT pk_domain_table_column PRIMARY KEY (domain_table_uuid, column_order),
    CONSTRAINT fk_domain_table_column_table
        FOREIGN KEY (domain_table_uuid) REFERENCES domain_table (uuid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS domain_table_row (
    uuid              VARCHAR(36) NOT NULL,
    domain_table_uuid VARCHAR(36) NOT NULL,
    values            JSONB       NOT NULL DEFAULT '{}',
    CONSTRAINT pk_domain_table_row PRIMARY KEY (uuid),
    CONSTRAINT fk_domain_table_row_table
        FOREIGN KEY (domain_table_uuid) REFERENCES domain_table (uuid) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_domain_table_row_table_uuid ON domain_table_row (domain_table_uuid);
CREATE INDEX IF NOT EXISTS idx_domain_table_row_values ON domain_table_row USING GIN (values);
