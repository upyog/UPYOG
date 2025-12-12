CREATE TABLE IF NOT EXISTS tobacco_license (
    id SERIAL PRIMARY KEY,
    license_number VARCHAR(50) NOT NULL,
    license_type VARCHAR(50),
    tenant_id VARCHAR(50),
    status VARCHAR(20),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
