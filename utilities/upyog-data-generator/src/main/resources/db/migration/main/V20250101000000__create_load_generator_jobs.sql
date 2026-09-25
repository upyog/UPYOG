CREATE TABLE IF NOT EXISTS eg_load_generator_jobs (
    job_id               VARCHAR(64)    PRIMARY KEY,
    module               VARCHAR(50)    NOT NULL,
    tenant_id            VARCHAR(100)   NOT NULL,
    total_records        INTEGER        NOT NULL DEFAULT 0,
    success_count        INTEGER        NOT NULL DEFAULT 0,
    failure_count        INTEGER        NOT NULL DEFAULT 0,
    status               VARCHAR(20)    NOT NULL DEFAULT 'ACCEPTED',
    start_time_ms        BIGINT,
    end_time_ms          BIGINT,
    throughput_per_sec   DOUBLE PRECISION DEFAULT 0,
    avg_response_time_ms DOUBLE PRECISION DEFAULT 0,
    error_summary        TEXT,
    created_time         BIGINT         DEFAULT EXTRACT(EPOCH FROM NOW()) * 1000
);

CREATE INDEX IF NOT EXISTS idx_load_jobs_module_tenant ON eg_load_generator_jobs (module, tenant_id);
CREATE INDEX IF NOT EXISTS idx_load_jobs_status ON eg_load_generator_jobs (status);
