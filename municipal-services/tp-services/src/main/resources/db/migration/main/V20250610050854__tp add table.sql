CREATE TABLE IF NOT EXISTS upyog_rs_tree_pruning_booking_detail (
    booking_id VARCHAR(64) PRIMARY KEY,
    booking_no VARCHAR(30) UNIQUE,
    tenant_id VARCHAR(64) NOT NULL,
    applicant_uuid VARCHAR(64),
    reason_for_pruning VARCHAR(255) NOT NULL,
    latitude NUMERIC(10, 8), -- e.g., 12.971598
    longitude NUMERIC(11, 8), -- e.g., 77.594566
    payment_date BIGINT,
    application_date BIGINT,
    booking_createdby VARCHAR(64),
    booking_status VARCHAR(64) NOT NULL,
    address_detail_id VARCHAR(64),
    payment_receipt_filestore_id VARCHAR(64),
    mobile_number VARCHAR(20),
    locality_code VARCHAR(30),
    createdby VARCHAR(64),
    lastmodifiedby VARCHAR(64),
    createdtime BIGINT NOT NULL,
    lastmodifiedtime BIGINT
);

CREATE TABLE IF NOT EXISTS upyog_rs_tree_pruning_booking_detail_audit (
    booking_id VARCHAR(64),
    booking_no VARCHAR(30),
    tenant_id VARCHAR(64) NOT NULL,
    applicant_uuid VARCHAR(64),
    reason_for_pruning VARCHAR(255) NOT NULL,
    latitude NUMERIC(10, 8), -- e.g., 12.971598
    longitude NUMERIC(11, 8), -- e.g., 77.594566
    payment_date BIGINT,
    application_date BIGINT,
    booking_createdby VARCHAR(64),
    booking_status VARCHAR(64) NOT NULL,
    address_detail_id VARCHAR(64),
    payment_receipt_filestore_id VARCHAR(64),
    mobile_number VARCHAR(20),
    locality_code VARCHAR(30),
    createdby VARCHAR(64),
    lastmodifiedby VARCHAR(64),
    createdtime BIGINT NOT NULL,
    lastmodifiedtime BIGINT,
    audit_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- to record when audit was made
);

CREATE TABLE IF NOT EXISTS upyog_rs_tree_pruning_document_detail (
    document_detail_id VARCHAR(64) PRIMARY KEY,
    booking_id VARCHAR(64) NOT NULL,  -- References booking_id in booking detail table
    document_type VARCHAR(64) NOT NULL,
    filestore_id VARCHAR(64) NOT NULL,
    createdby VARCHAR(64) NOT NULL,
    lastmodifiedby VARCHAR(64),
    createdtime BIGINT NOT NULL,
    lastmodifiedtime BIGINT,
    CONSTRAINT fk_document_application FOREIGN KEY (booking_id)
        REFERENCES upyog_rs_tree_pruning_booking_detail (booking_id)
        ON DELETE CASCADE
        ON UPDATE NO ACTION
);

-- Indexes for booking detail table
CREATE INDEX IF NOT EXISTS idx_upyog_rs_booking_no ON upyog_rs_tree_pruning_booking_detail(booking_no);
CREATE INDEX IF NOT EXISTS idx_upyog_rs_createdby ON upyog_rs_tree_pruning_booking_detail(createdby);
CREATE INDEX IF NOT EXISTS idx_upyog_rs_tenant_id ON upyog_rs_tree_pruning_booking_detail(tenant_id);

-- Indexes for audit table
CREATE INDEX IF NOT EXISTS idx_upyog_rs_booking_audit_booking_id ON upyog_rs_tree_pruning_booking_detail_audit(booking_id);
CREATE INDEX IF NOT EXISTS idx_upyog_rs_booking_audit_audit_timestamp ON upyog_rs_tree_pruning_booking_detail_audit(audit_timestamp);

-- Indexes for document detail table
CREATE INDEX IF NOT EXISTS idx_upyog_rs_document_booking_id ON upyog_rs_tree_pruning_document_detail(booking_id);
CREATE INDEX IF NOT EXISTS idx_upyog_rs_document_createdby ON upyog_rs_tree_pruning_document_detail(createdby);

CREATE SEQUENCE IF NOT EXISTS seq_tree_pruning_booking_id
