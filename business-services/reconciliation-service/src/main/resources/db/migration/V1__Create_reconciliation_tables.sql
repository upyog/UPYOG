CREATE TABLE ug_recon_configuration (
    id BIGSERIAL PRIMARY KEY,
    client_name VARCHAR(100) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    module_name VARCHAR(100) NOT NULL,
    last_extraction_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ug_recon_extraction_details (
    id BIGSERIAL PRIMARY KEY,
    config_id BIGINT NOT NULL REFERENCES ug_recon_configuration(id),
    extraction_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    acknowledged BOOLEAN DEFAULT FALSE,
    data_payload JSONB,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
