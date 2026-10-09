-- =============================================================================
-- Migration: V20260713050754__extractor_tables
-- Description: Consolidated schema script for dashboard data extractor pipeline.
--
-- Tables included:
--   1. ug_ingestion_module_detail      — ULB-module configuration
--   2. ug_ingestion_scheduler_detail   — Scheduler execution log and cron tracking
--   3. ug_ingestion_detail             — Daily ingestion detail records
--   4. ug_legacy_data_ingestion_detail — Legacy batch & historical detail records
--   5. ug_ingestion_module_summary     — Module ingestion status summary per tenant
--   6. ug_adapter_ingestion_error_log  — Ingestion error log
--   7. shedlock                        — Distributed scheduler locking
-- =============================================================================

-- =============================================================================
-- Table 1: ug_ingestion_module_detail
-- =============================================================================
CREATE TABLE IF NOT EXISTS ug_ingestion_module_detail (
    detail_id                   VARCHAR(64)     NOT NULL,
    tenant_id                   VARCHAR(64)     NOT NULL,
    module_name                 VARCHAR(64)     NOT NULL,
    is_active                   BOOLEAN         NOT NULL DEFAULT TRUE,
    created_by                  VARCHAR(256)    NOT NULL DEFAULT 'SYSTEM',
    created_time                BIGINT          NOT NULL DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    last_modified_by            VARCHAR(256)    NOT NULL DEFAULT 'SYSTEM',
    last_modified_time          BIGINT          NOT NULL DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    CONSTRAINT pk_ug_ingestion_module_detail PRIMARY KEY (detail_id),
    CONSTRAINT uk_ug_ingestion_module_detail_tenant_module UNIQUE (tenant_id, module_name)
);

-- Index for retrieving active tenants by module (e.g. SELECT_ACTIVE_TENANTS_BY_MODULE_QUERY)
CREATE INDEX IF NOT EXISTS idx_ug_ingestion_module_detail_active_module_tenant
    ON ug_ingestion_module_detail (module_name, tenant_id)
    WHERE is_active = TRUE;

-- Index for retrieving all active tenants/details (e.g. SELECT_ALL_ACTIVE_TENANTS_QUERY, SELECT_ALL_ACTIVE_MODULE_DETAILS_QUERY)
CREATE INDEX IF NOT EXISTS idx_ug_ingestion_module_detail_active
    ON ug_ingestion_module_detail (is_active, tenant_id, module_name);

COMMENT ON TABLE ug_ingestion_module_detail IS
    'Configuration metadata for each ULB-module ingestion stream.';
COMMENT ON COLUMN ug_ingestion_module_detail.detail_id IS
    'Primary key — deterministic UUID generated from tenant_id and module_name.';
COMMENT ON COLUMN ug_ingestion_module_detail.tenant_id IS
    'DIGIT tenant identifier for the ULB (e.g. pg.citya).';
COMMENT ON COLUMN ug_ingestion_module_detail.module_name IS
    'Short code identifying the DIGIT module (e.g. PT, TL, WS, PGR, CHB).';
COMMENT ON COLUMN ug_ingestion_module_detail.is_active IS
    'Whether this ULB-module combination is currently scheduled/active for ingestion.';


-- =============================================================================
-- Table 2: ug_ingestion_scheduler_detail
-- =============================================================================
CREATE TABLE IF NOT EXISTS ug_ingestion_scheduler_detail (
    scheduler_id                VARCHAR(64)     NOT NULL,
    scheduler_name              VARCHAR(128)    NOT NULL,
    cron_expression             VARCHAR(64),
    start_time                  BIGINT          NOT NULL,
    end_time                    BIGINT,
    duration_ms                 BIGINT,
    status                      VARCHAR(32)     NOT NULL,
    total_records_processed     INT             DEFAULT 0,
    success_records_count       INT             DEFAULT 0,
    failure_records_count       INT             DEFAULT 0,
    error_message               TEXT,
    created_by                  VARCHAR(256)    DEFAULT 'SYSTEM',
    created_time                BIGINT          NOT NULL DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    last_modified_by            VARCHAR(256)    DEFAULT 'SYSTEM',
    last_modified_time          BIGINT          NOT NULL DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    CONSTRAINT pk_ug_ingestion_scheduler_detail PRIMARY KEY (scheduler_id)
);

CREATE INDEX IF NOT EXISTS idx_ug_ingestion_scheduler_detail_name_start_time
    ON ug_ingestion_scheduler_detail (scheduler_name, start_time DESC);

CREATE INDEX IF NOT EXISTS idx_ug_ingestion_scheduler_detail_status
    ON ug_ingestion_scheduler_detail (status);

COMMENT ON TABLE ug_ingestion_scheduler_detail IS
    'Execution history log of scheduled cron jobs and manual ingestion runs.';
COMMENT ON COLUMN ug_ingestion_scheduler_detail.scheduler_id IS
    'Primary key — UUID generated at the start of each scheduler run.';
COMMENT ON COLUMN ug_ingestion_scheduler_detail.scheduler_name IS
    'Name or type of the scheduler (e.g. DAILY_INGESTION_SCHEDULER, LEGACY_POPULATE_SCHEDULER).';
COMMENT ON COLUMN ug_ingestion_scheduler_detail.cron_expression IS
    'Cron expression that triggered the execution, or NULL for manual triggers.';
COMMENT ON COLUMN ug_ingestion_scheduler_detail.start_time IS
    'Unix epoch timestamp (milliseconds) when scheduler execution began.';
COMMENT ON COLUMN ug_ingestion_scheduler_detail.end_time IS
    'Unix epoch timestamp (milliseconds) when scheduler execution completed or failed.';
COMMENT ON COLUMN ug_ingestion_scheduler_detail.duration_ms IS
    'Total execution duration in milliseconds.';
COMMENT ON COLUMN ug_ingestion_scheduler_detail.status IS
    'Execution status: RUNNING, COMPLETED, FAILED.';


-- =============================================================================
-- Table 3: ug_ingestion_detail
-- =============================================================================
CREATE TABLE IF NOT EXISTS ug_ingestion_detail (
    module_ingestion_id         VARCHAR(64)     NOT NULL,
    module_detail_id            VARCHAR(64),
    scheduler_id                VARCHAR(64),
    tenant_id                   VARCHAR(64),
    module_name                 VARCHAR(64),
    push_date                   DATE,
    request_data                JSONB,
    response_data               JSONB,
    ingestion_status            VARCHAR(32),
    exception_code              VARCHAR(128),
    created_by                  VARCHAR(256)    DEFAULT 'SYSTEM',
    created_time                BIGINT          NOT NULL DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    last_modified_by            VARCHAR(256)    DEFAULT 'SYSTEM',
    last_modified_time          BIGINT          NOT NULL DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    CONSTRAINT pk_ug_ingestion_detail PRIMARY KEY (module_ingestion_id),
    CONSTRAINT fk_ug_ingestion_detail_module_detail
        FOREIGN KEY (module_detail_id)
        REFERENCES ug_ingestion_module_detail (detail_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_ug_ingestion_detail_scheduler
        FOREIGN KEY (scheduler_id)
        REFERENCES ug_ingestion_scheduler_detail (scheduler_id)
        ON DELETE SET NULL
);

-- Index for tenant and module date range queries (e.g. SELECT_SUCCESSFUL_DATES_IN_RANGE_QUERY)
CREATE INDEX IF NOT EXISTS idx_ug_ingestion_detail_tenant_module_date_status
    ON ug_ingestion_detail (tenant_id, module_name, push_date DESC, ingestion_status);

-- Index for target date completion lookups across all tenants (e.g. SELECT_TENANTS_SUCCESSFULLY_INGESTED_FOR_DATE_QUERY)
CREATE INDEX IF NOT EXISTS idx_ug_ingestion_detail_module_date_status
    ON ug_ingestion_detail (module_name, push_date, ingestion_status);

-- Filtered index for pending/failed ingestion attempts
CREATE INDEX IF NOT EXISTS idx_ug_ingestion_detail_status
    ON ug_ingestion_detail (ingestion_status)
    WHERE ingestion_status IN ('FAILURE', 'NOT_STARTED');

-- Foreign key lookup index for join performance
CREATE INDEX IF NOT EXISTS idx_ug_ingestion_detail_module_detail_id
    ON ug_ingestion_detail (module_detail_id);

-- Foreign key lookup index for scheduler run joins
CREATE INDEX IF NOT EXISTS idx_ug_ingestion_detail_scheduler_id
    ON ug_ingestion_detail (scheduler_id);

COMMENT ON TABLE ug_ingestion_detail IS
    'Detail log of every daily ingestion push attempt to the National Dashboard.';
COMMENT ON COLUMN ug_ingestion_detail.module_ingestion_id IS
    'Primary key — UUID generated by the ingestion client before persistence.';
COMMENT ON COLUMN ug_ingestion_detail.module_detail_id IS
    'Foreign key referencing ug_ingestion_module_detail.detail_id.';
COMMENT ON COLUMN ug_ingestion_detail.scheduler_id IS
    'Foreign key referencing ug_ingestion_scheduler_detail.scheduler_id.';
COMMENT ON COLUMN ug_ingestion_detail.tenant_id IS
    'DIGIT tenant identifier for the ULB whose data was pushed.';
COMMENT ON COLUMN ug_ingestion_detail.module_name IS
    'Short code identifying the DIGIT module (e.g. PT, TL, WS, PGR, CHB).';
COMMENT ON COLUMN ug_ingestion_detail.push_date IS
    'Calendar date for which the module data was pushed.';
COMMENT ON COLUMN ug_ingestion_detail.request_data IS
    'Full JSON of the NationalDashboardIngestRequest sent to the dashboard.';
COMMENT ON COLUMN ug_ingestion_detail.response_data IS
    'HTTP response body (success) or exception message (failure).';
COMMENT ON COLUMN ug_ingestion_detail.ingestion_status IS
    'Outcome: SUCCESS, FAILURE, SUCCESS_ZERO_METRICS, etc.';
COMMENT ON COLUMN ug_ingestion_detail.exception_code IS
    'Standardized exception error code.';


-- =============================================================================
-- Table 4: ug_legacy_data_ingestion_detail
-- =============================================================================
CREATE TABLE IF NOT EXISTS ug_legacy_data_ingestion_detail (
    module_ingestion_id         VARCHAR(64)     NOT NULL,
    module_detail_id            VARCHAR(64),
    scheduler_id                VARCHAR(64),
    tenant_id                   VARCHAR(64),
    ulb_name                    VARCHAR(256),
    module_name                 VARCHAR(64),
    push_date                   DATE,
    start_date                  DATE,
    end_date                    DATE,
    user_id                     VARCHAR(64),
    request_data                JSONB,
    response_data               JSONB,
    ingestion_status            VARCHAR(32)     NOT NULL DEFAULT 'NOT_STARTED',
    exception_code              VARCHAR(128),
    created_by                  VARCHAR(256)    DEFAULT 'SYSTEM',
    created_time                BIGINT          NOT NULL DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    last_modified_by            VARCHAR(256)    DEFAULT 'SYSTEM',
    last_modified_time          BIGINT          NOT NULL DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    CONSTRAINT pk_ug_legacy_ingestion_detail PRIMARY KEY (module_ingestion_id),
    CONSTRAINT fk_ug_legacy_ingestion_detail_module_detail
        FOREIGN KEY (module_detail_id)
        REFERENCES ug_ingestion_module_detail (detail_id)
        ON DELETE SET NULL,
    CONSTRAINT fk_ug_legacy_ingestion_detail_scheduler
        FOREIGN KEY (scheduler_id)
        REFERENCES ug_ingestion_scheduler_detail (scheduler_id)
        ON DELETE SET NULL
);

-- Index for tenant, module, push_date and status queries (e.g. SELECT_SUCCESSFUL_DATES_IN_RANGE_QUERY, SELECT_LEGACY_JOB_DATES_QUERY)
CREATE INDEX IF NOT EXISTS idx_ug_legacy_ingestion_detail_tenant_module_date_status
    ON ug_legacy_data_ingestion_detail (tenant_id, module_name, push_date DESC, ingestion_status);

-- Composite index for overlapping legacy jobs lookups (SELECT_OVERLAPPING_SUCCESSFUL_LEGACY_JOBS_QUERY)
CREATE INDEX IF NOT EXISTS idx_ug_legacy_ingestion_detail_overlap
    ON ug_legacy_data_ingestion_detail (tenant_id, module_name, ingestion_status, start_date, end_date);

-- Index for target date completion lookups across all tenants (SELECT_TENANTS_SUCCESSFULLY_INGESTED_FOR_DATE_QUERY)
CREATE INDEX IF NOT EXISTS idx_ug_legacy_ingestion_detail_module_date_status
    ON ug_legacy_data_ingestion_detail (module_name, push_date, ingestion_status);

-- Filtered index for retrieving pending or failed legacy jobs ordered by push_date ASC (SELECT_PENDING_OR_FAILED_LEGACY_JOBS_QUERY)
CREATE INDEX IF NOT EXISTS idx_ug_legacy_ingestion_detail_pending_failed
    ON ug_legacy_data_ingestion_detail (tenant_id, module_name, push_date ASC)
    WHERE ingestion_status IN ('NOT_STARTED', 'FAILURE');

-- Foreign key lookup index for join performance
CREATE INDEX IF NOT EXISTS idx_ug_legacy_ingestion_detail_module_detail_id
    ON ug_legacy_data_ingestion_detail (module_detail_id);

-- Foreign key lookup index for scheduler run joins
CREATE INDEX IF NOT EXISTS idx_ug_legacy_ingestion_detail_scheduler_id
    ON ug_legacy_data_ingestion_detail (scheduler_id);

COMMENT ON TABLE ug_legacy_data_ingestion_detail IS
    'Historical log of every legacy (historical batch / per-date) ingestion push attempt.';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.module_ingestion_id IS
    'Primary key — UUID / Job ID generated by the legacy ingestion orchestrator.';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.module_detail_id IS
    'Foreign key referencing ug_ingestion_module_detail.detail_id.';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.tenant_id IS
    'DIGIT tenant identifier for the ULB whose data was pushed.';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.ulb_name IS
    'Name of the Urban Local Body whose legacy data was ingested.';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.module_name IS
    'Short code identifying the DIGIT module (e.g. PT, TL, WS, PGR, CHB).';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.push_date IS
    'Calendar date on which legacy data was pushed.';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.start_date IS
    'Inclusive start date of the legacy ingestion range.';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.end_date IS
    'Inclusive end date of the legacy ingestion range.';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.user_id IS
    'Identifier of the user or system that triggered the legacy ingestion.';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.request_data IS
    'Full JSON of the NationalDashboardIngestRequest sent to the dashboard.';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.response_data IS
    'HTTP response body (success) or exception message (failure).';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.ingestion_status IS
    'Outcome: NOT_STARTED (default), SUCCESS, FAILURE, etc.';
COMMENT ON COLUMN ug_legacy_data_ingestion_detail.exception_code IS
    'Standardized exception error code.';


-- =============================================================================
-- Table 5: ug_ingestion_module_summary
-- =============================================================================
CREATE TABLE IF NOT EXISTS ug_ingestion_module_summary (
    id                          VARCHAR(64)     NOT NULL,
    tenant_id                   VARCHAR(64)     NOT NULL,
    module_name                 VARCHAR(64)     NOT NULL,
    last_successful_date        DATE            NOT NULL,
    last_attempted_date         DATE,
    created_by                  VARCHAR(256)    NOT NULL DEFAULT 'SYSTEM',
    created_time                BIGINT          NOT NULL DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    last_modified_by            VARCHAR(256)    NOT NULL DEFAULT 'SYSTEM',
    last_modified_time          BIGINT          NOT NULL DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    CONSTRAINT pk_ug_ingestion_module_summary PRIMARY KEY (id),
    CONSTRAINT uk_ug_ingestion_module_summary_tenant_module UNIQUE (tenant_id, module_name)
);

-- Index for module-wide bulk queries (SELECT_ALL_LAST_SUCCESSFUL_DATES_FOR_MODULE_QUERY, SELECT_TENANTS_SUCCESSFULLY_INGESTED_FOR_DATE_QUERY)
CREATE INDEX IF NOT EXISTS idx_ug_ingestion_module_summary_module_last_successful
    ON ug_ingestion_module_summary (module_name, last_successful_date);

COMMENT ON TABLE ug_ingestion_module_summary IS
    'Tracks the latest successful and attempted ingestion dates per tenant and module.';
COMMENT ON COLUMN ug_ingestion_module_summary.id IS
    'Primary key — UUID generated by the application layer.';
COMMENT ON COLUMN ug_ingestion_module_summary.tenant_id IS
    'DIGIT tenant identifier for the ULB (e.g. pg.citya or state code pg).';
COMMENT ON COLUMN ug_ingestion_module_summary.module_name IS
    'Short code identifying the DIGIT module (e.g. PT, TL, WS, PGR, CHB).';
COMMENT ON COLUMN ug_ingestion_module_summary.last_successful_date IS
    'The most recent calendar date for which metrics were successfully ingested.';
COMMENT ON COLUMN ug_ingestion_module_summary.last_attempted_date IS
    'The most recent calendar date for which metrics ingestion was attempted.';


-- =============================================================================
-- Table 6: ug_adapter_ingestion_error_log
-- =============================================================================
CREATE TABLE IF NOT EXISTS ug_adapter_ingestion_error_log (
    id                  VARCHAR(64)     NOT NULL,
    tenant_id           VARCHAR(64)     NOT NULL,
    module_name         VARCHAR(64)     NOT NULL,
    error_date          VARCHAR(64)     NOT NULL,
    issue_description   TEXT,
    created_time        BIGINT          NOT NULL DEFAULT (ROUND(EXTRACT(EPOCH FROM NOW()) * 1000))::BIGINT,
    created_by          VARCHAR(64)     NOT NULL DEFAULT 'SYSTEM',
    CONSTRAINT pk_ug_adapter_ingestion_error_log PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_ug_adapter_error_log_tenant_module_date
    ON ug_adapter_ingestion_error_log (tenant_id, module_name, error_date);

COMMENT ON TABLE ug_adapter_ingestion_error_log IS
    'Error log table capturing adapter ingestion failures and error descriptions.';


-- =============================================================================
-- Table 7: shedlock
-- =============================================================================
CREATE TABLE IF NOT EXISTS shedlock (
    name                VARCHAR(64)     NOT NULL,
    lock_until          TIMESTAMP       NOT NULL,
    locked_at           TIMESTAMP       NOT NULL,
    locked_by           VARCHAR(255)    NOT NULL,
    PRIMARY KEY (name)
);

COMMENT ON TABLE shedlock IS
    'Distributed scheduler locking table used by ShedLock across application nodes.';
