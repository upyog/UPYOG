CREATE TABLE IF NOT EXISTS eg_bill_failure (
    id UUID,
    consumer_code VARCHAR(100),
    module_name VARCHAR(50),
    tenant_id VARCHAR(64) NULL,
    failure_reason TEXT NULL,
    "month" VARCHAR(256) NULL,
    "year" VARCHAR(256) NULL,
    from_date VARCHAR(256) NULL,
    to_date VARCHAR(256) NULL,
    request_payload JSONB,
    response_payload JSONB,
    error_json JSONB,
    status_code VARCHAR(10) NULL,
    created_time BIGINT NULL,
    last_modified_time BIGINT NULL,
    CONSTRAINT eg_bill_failure_pk PRIMARY KEY (id),
    CONSTRAINT unique_consumer_period UNIQUE (consumer_code, from_date, to_date)
);
