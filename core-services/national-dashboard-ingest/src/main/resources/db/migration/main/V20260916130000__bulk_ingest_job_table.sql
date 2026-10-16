CREATE TABLE IF NOT EXISTS ug_bulk_ingest_job (
    id VARCHAR(128) PRIMARY KEY,
    file_name VARCHAR(512) NOT NULL,
    state_code VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    total_rows_processed INTEGER DEFAULT 0,
    failed_rows_count INTEGER DEFAULT 0,
    created_time BIGINT NOT NULL,
    last_modified_time BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ug_bulk_ingest_job_file_name ON ug_bulk_ingest_job (file_name);
CREATE INDEX IF NOT EXISTS idx_ug_bulk_ingest_job_status ON ug_bulk_ingest_job (status);
