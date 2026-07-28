CREATE TABLE IF NOT EXISTS eg_grbg_scheduler_log (
    id                  VARCHAR(64) PRIMARY KEY,
    garbage_account_id  VARCHAR(64) NOT NULL,
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

CREATE INDEX IF NOT EXISTS idx_grbg_scheduler_account
ON eg_grbg_scheduler_log(garbage_account_id);

CREATE INDEX IF NOT EXISTS idx_grbg_scheduler_period
ON eg_grbg_scheduler_log(billing_period_from, billing_period_to);