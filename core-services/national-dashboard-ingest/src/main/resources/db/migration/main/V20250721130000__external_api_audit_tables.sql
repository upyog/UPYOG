CREATE TABLE ug_external_api_message_detail (
    id UUID PRIMARY KEY,
    correlation_id VARCHAR(128) NOT NULL UNIQUE,
    tenant_id VARCHAR(256) NOT NULL,
    external_api_name VARCHAR(256) NOT NULL,
    direction VARCHAR(32) NOT NULL,
    request_time BIGINT NOT NULL,
    response_time BIGINT,
    duration_ms BIGINT,
    status VARCHAR(32) NOT NULL,
    http_status_code INTEGER,
    retry_count INTEGER NOT NULL DEFAULT 0,
    created_time BIGINT NOT NULL,
    last_modified_time BIGINT NOT NULL
);

CREATE INDEX idx_ug_external_api_message_detail_correlation_id ON ug_external_api_message_detail (correlation_id);
CREATE INDEX idx_ug_external_api_message_detail_tenant_id ON ug_external_api_message_detail (tenant_id);
CREATE INDEX idx_ug_external_api_message_detail_status ON ug_external_api_message_detail (status);
CREATE INDEX idx_ug_external_api_message_detail_created_time ON ug_external_api_message_detail (created_time);

CREATE TABLE ug_external_api_message_raw_detail (
    id UUID PRIMARY KEY,
    correlation_id VARCHAR(128) NOT NULL,
    request_payload JSONB,
    response_payload JSONB,
    payload_size_bytes BIGINT,
    created_time BIGINT NOT NULL,
    last_modified_time BIGINT NOT NULL,
    CONSTRAINT fk_ug_external_api_message_raw_detail_correlation
        FOREIGN KEY (correlation_id) REFERENCES ug_external_api_message_detail (correlation_id)
);

CREATE INDEX idx_ug_external_api_message_raw_detail_correlation_id ON ug_external_api_message_raw_detail (correlation_id);
CREATE INDEX idx_ug_external_api_message_raw_detail_created_time ON ug_external_api_message_raw_detail (created_time);

CREATE TABLE ug_external_api_error_detail (
    id UUID PRIMARY KEY,
    correlation_id VARCHAR(128) NOT NULL,
    error_code VARCHAR(256) NOT NULL,
    error_type VARCHAR(32) NOT NULL,
    error_message TEXT NOT NULL,
    created_time BIGINT NOT NULL,
    CONSTRAINT fk_ug_external_api_error_detail_correlation
        FOREIGN KEY (correlation_id) REFERENCES ug_external_api_message_detail (correlation_id)
);

CREATE INDEX idx_ug_external_api_error_detail_correlation_id ON ug_external_api_error_detail (correlation_id);
CREATE INDEX idx_ug_external_api_error_detail_created_time ON ug_external_api_error_detail (created_time);
