CREATE TABLE IF NOT EXISTS eg_est_scheduler_log (
    id                  VARCHAR(64) PRIMARY KEY,
    allotment_id        VARCHAR(64) NOT NULL,
    tenant_id           VARCHAR(100) NOT NULL,
    billing_date        DATE NOT NULL,
    billing_period_from BIGINT NOT NULL,
    billing_period_to   BIGINT NOT NULL,
    amount              NUMERIC(12,2) NOT NULL,
    penalty_amount      NUMERIC(12,2) NOT NULL DEFAULT 0,
    payment_type        VARCHAR(20) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_by          VARCHAR(64),
    created_time        BIGINT,
    last_modified_by    VARCHAR(64),
    last_modified_time  BIGINT
);

CREATE INDEX IF NOT EXISTS idx_est_scheduler_allotment
ON eg_est_scheduler_log(allotment_id);

CREATE INDEX IF NOT EXISTS idx_est_scheduler_period
ON eg_est_scheduler_log(billing_period_from, billing_period_to);