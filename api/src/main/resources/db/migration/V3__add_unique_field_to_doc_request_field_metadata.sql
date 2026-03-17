-- ============================================================
-- V3: Add is_unique field to doc_request_field_metadata table
-- ============================================================

-- Add the is_unique column with default value for existing records
ALTER TABLE doc_request_field_metadata 
ADD COLUMN is_unique BOOLEAN NOT NULL DEFAULT FALSE;

-- Add comment for the new column
COMMENT ON COLUMN doc_request_field_metadata.is_unique IS 'Whether this field must have a unique value across all documents using the same template within the same tenant.';

-- Create index for better query performance on unique fields
CREATE INDEX IF NOT EXISTS idx_field_metadata_is_unique ON doc_request_field_metadata (is_unique);

-- Create index for composite query (is_unique + metadata uuid) for better performance
CREATE INDEX IF NOT EXISTS idx_field_metadata_is_unique_metadata_uuid ON doc_request_field_metadata (is_unique, doc_request_metadata_uuid);