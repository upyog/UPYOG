-- ============================================================
-- Refund Master Table
-- ============================================================

CREATE TABLE IF NOT EXISTS eg_refund
(
    id                    UUID PRIMARY KEY,
    refund_no             VARCHAR(100) NOT NULL UNIQUE,
    tenant_id             VARCHAR(256) NOT NULL,
    module_name           VARCHAR(100) NOT NULL,
    business_service      VARCHAR(100) NOT NULL,
    consumer_code         VARCHAR(128) NOT NULL,
    payment_id            VARCHAR(128),
    applicant_name        VARCHAR(256),
    mobile_number         VARCHAR(20),
    refund_category       VARCHAR(50),
    refund_reason         TEXT,
    payment_mode_original VARCHAR(32),
    amount_paid           NUMERIC(14,2) NOT NULL DEFAULT 0,
    refund_amount         NUMERIC(14,2) NOT NULL DEFAULT 0,
    refund_mode           VARCHAR(32),
    status                VARCHAR(64) NOT NULL,
    sanction_ref          VARCHAR(128),
    finance_approval_date TIMESTAMP,
    gateway_refund_id     VARCHAR(128),
    beneficiary_details   JSONB,
    additional_details    JSONB,
    created_by            VARCHAR(64),
    created_time          BIGINT NOT NULL,
    last_modified_by      VARCHAR(64),
    last_modified_time    BIGINT,
    file_store_id         VARCHAR(128)
);

-- ============================================================
-- eg_refund Indexes
-- ============================================================

-- refund_no already has a unique index through the UNIQUE constraint.

CREATE INDEX idx_eg_refund_tenant_id
    ON eg_refund (tenant_id);

CREATE INDEX idx_eg_refund_tenant_module_service
    ON eg_refund (tenant_id, module_name, business_service);

CREATE INDEX idx_eg_refund_consumer_code
    ON eg_refund (consumer_code);

CREATE INDEX idx_eg_refund_payment_id
    ON eg_refund (payment_id);

CREATE INDEX idx_eg_refund_status
    ON eg_refund (status);

CREATE INDEX idx_eg_refund_sanction_ref
    ON eg_refund (sanction_ref);

CREATE INDEX idx_eg_refund_gateway_refund_id
    ON eg_refund (gateway_refund_id);

CREATE INDEX idx_eg_refund_created_time
    ON eg_refund (created_time);

CREATE INDEX idx_eg_refund_last_modified_time
    ON eg_refund (last_modified_time);

-- Useful for tenant-wise status based searches.
CREATE INDEX idx_eg_refund_tenant_status
    ON eg_refund (tenant_id, status);

-- Useful for tenant-wise consumer searches.
CREATE INDEX idx_eg_refund_tenant_consumer
    ON eg_refund (tenant_id, consumer_code);

-- Useful for payment based lookups within a tenant.
CREATE INDEX idx_eg_refund_tenant_payment
    ON eg_refund (tenant_id, payment_id);


-- ============================================================
-- Refund Audit Table
-- ============================================================

CREATE TABLE IF NOT EXISTS eg_refund_audit
(
    id                    UUID PRIMARY KEY,
    refund_id             UUID NOT NULL,
    refund_no             VARCHAR(100),

    tenant_id             VARCHAR(100) NOT NULL,
    module_name           VARCHAR(100),
    business_service      VARCHAR(100),

    consumer_code         VARCHAR(100),
    payment_id            VARCHAR(100),

    applicant_name        VARCHAR(200),
    mobile_number         VARCHAR(20),

    refund_category       VARCHAR(100),
    refund_reason         TEXT,

    payment_mode_original VARCHAR(50),

    amount_paid           NUMERIC(18, 2),
    refund_amount         NUMERIC(18, 2),

    refund_mode           VARCHAR(50),
    status                VARCHAR(50),

    sanction_ref          VARCHAR(100),
    finance_approval_date TIMESTAMP,

    gateway_refund_id     VARCHAR(100),
    file_store_id         VARCHAR(100),

    beneficiary_details   JSONB,
    additional_details    JSONB,

    created_by            VARCHAR(100),
    created_time          BIGINT,
    last_modified_by      VARCHAR(100),
    last_modified_time    BIGINT,

    audit_created_time    BIGINT,

    workflow_action       VARCHAR(100)
);

-- ============================================================
-- eg_refund_audit Indexes
-- ============================================================

CREATE INDEX idx_eg_refund_audit_refund_id
    ON eg_refund_audit (refund_id);

CREATE INDEX idx_eg_refund_audit_refund_no
    ON eg_refund_audit (refund_no);

CREATE INDEX idx_eg_refund_audit_tenant_id
    ON eg_refund_audit (tenant_id);

CREATE INDEX idx_eg_refund_audit_tenant_module_service
    ON eg_refund_audit (tenant_id, module_name, business_service);

CREATE INDEX idx_eg_refund_audit_consumer_code
    ON eg_refund_audit (consumer_code);

CREATE INDEX idx_eg_refund_audit_payment_id
    ON eg_refund_audit (payment_id);

CREATE INDEX idx_eg_refund_audit_status
    ON eg_refund_audit (status);

CREATE INDEX idx_eg_refund_audit_workflow_action
    ON eg_refund_audit (workflow_action);

CREATE INDEX idx_eg_refund_audit_created_time
    ON eg_refund_audit (created_time);

CREATE INDEX idx_eg_refund_audit_audit_created_time
    ON eg_refund_audit (audit_created_time);

-- Most common audit-history query:
-- Fetch all audit records for a refund ordered by audit time.
CREATE INDEX idx_eg_refund_audit_refund_id_audit_time
    ON eg_refund_audit (refund_id, audit_created_time DESC);

-- Tenant + refund status + audit time filtering.
CREATE INDEX idx_eg_refund_audit_tenant_status_time
    ON eg_refund_audit (tenant_id, status, audit_created_time DESC);

-- Tenant + consumer lookup.
CREATE INDEX idx_eg_refund_audit_tenant_consumer
    ON eg_refund_audit (tenant_id, consumer_code);

-- Tenant + payment lookup.
CREATE INDEX idx_eg_refund_audit_tenant_payment
    ON eg_refund_audit (tenant_id, payment_id);