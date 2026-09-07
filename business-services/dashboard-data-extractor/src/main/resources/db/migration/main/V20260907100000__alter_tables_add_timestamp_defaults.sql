-- =============================================================================
-- Migration: V20260907100000__alter_tables_add_timestamp_defaults
-- Description: Sets default epoch millisecond timestamps for created_time and 
--              last_modified_time across all dashboard ingestion tables.
-- =============================================================================

-- Table 1: ingestion_module_detail
ALTER TABLE ingestion_module_detail
    ALTER COLUMN created_time SET DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    ALTER COLUMN last_modified_time SET DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT;

-- Table 2: ingestion_detail
ALTER TABLE ingestion_detail
    ALTER COLUMN created_time SET DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    ALTER COLUMN last_modified_time SET DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT;

-- Table 3: legacy_data_ingestion_detail
ALTER TABLE legacy_data_ingestion_detail
    ALTER COLUMN created_time SET DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    ALTER COLUMN last_modified_time SET DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT;

-- Table 4: ingestion_module_summary
ALTER TABLE ingestion_module_summary
    ALTER COLUMN created_time SET DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    ALTER COLUMN last_modified_time SET DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT;
