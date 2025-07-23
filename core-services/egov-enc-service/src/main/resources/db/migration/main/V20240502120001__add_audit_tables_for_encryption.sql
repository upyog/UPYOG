-- Create audit table for symmetric keys
-- tenant_id is VARCHAR(128) to efficiently support tenant code lookups and indexing
CREATE TABLE IF NOT EXISTS eg_enc_symmetric_keys_audit (
    audit_id SERIAL PRIMARY KEY,
    operation VARCHAR(10) NOT NULL,
    tenant_id VARCHAR(128),
    changed_at TIMESTAMP DEFAULT now(),
    old_row JSONB,
    new_row JSONB
);

-- Create audit table for asymmetric keys
-- tenant_id is VARCHAR(128) to efficiently support tenant code lookups and indexing
CREATE TABLE IF NOT EXISTS eg_enc_asymmetric_keys_audit (
    audit_id SERIAL PRIMARY KEY,
    operation VARCHAR(10) NOT NULL,
    tenant_id VARCHAR(128),
    changed_at TIMESTAMP DEFAULT now(),
    old_row JSONB,
    new_row JSONB
);

-- Add createdtime and lastmodifiedtime columns to symmetric keys table
ALTER TABLE eg_enc_symmetric_keys
    ADD COLUMN IF NOT EXISTS createdtime BIGINT,
    ADD COLUMN IF NOT EXISTS lastmodifiedtime BIGINT;

-- Add createdtime and lastmodifiedtime columns to asymmetric keys table
ALTER TABLE eg_enc_asymmetric_keys
    ADD COLUMN IF NOT EXISTS createdtime BIGINT,
    ADD COLUMN IF NOT EXISTS lastmodifiedtime BIGINT; 