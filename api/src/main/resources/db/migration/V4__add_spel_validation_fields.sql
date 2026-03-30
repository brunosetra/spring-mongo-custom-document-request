-- Add SpEL validation fields to doc_request_field_metadata
ALTER TABLE doc_request_field_metadata 
ADD COLUMN validation_expression VARCHAR(2000),
ADD COLUMN dependency_expression VARCHAR(2000),
ADD COLUMN enable_spel_validation BOOLEAN DEFAULT false;

-- Add index for better performance
CREATE INDEX idx_spel_validation_enabled ON doc_request_field_metadata(enable_spel_validation);