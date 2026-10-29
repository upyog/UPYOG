-- Table: ug_em_asset_details
CREATE TABLE IF NOT EXISTS ug_em_asset_details (
    asset_id VARCHAR(64) PRIMARY KEY,
    estate_no VARCHAR(16) NOT NULL UNIQUE,
    tenant_id VARCHAR(100) NOT NULL,
    building_name VARCHAR(200),
    building_no VARCHAR(100),
    ref_asset_no VARCHAR(64),
    floor INT,
    locality_code VARCHAR(100),
    locality VARCHAR(100),
    total_floor_area NUMERIC,
    dimension_length NUMERIC,
    dimension_width NUMERIC,
    rent_rate NUMERIC,
    name VARCHAR(256) NOT NULL,
    description TEXT,
    classification VARCHAR(256),
    parent_category VARCHAR(256),
    category VARCHAR(256),
    subcategory VARCHAR(256),
    department VARCHAR(256),
    asset_type VARCHAR(100),
    asset_status VARCHAR(10) CHECK (asset_status IN ('active', 'inactive')),
    asset_allotment_type VARCHAR(50),
    asset_allotment_status VARCHAR(50),
    additional_details JSONB,
    createdby VARCHAR(64),
    lastmodifiedby VARCHAR(64),
    createdtime BIGINT,
    lastmodifiedtime BIGINT
);

-- Table: ug_em_allotment_details
CREATE TABLE IF NOT EXISTS ug_em_allotment_details (
    allotment_id VARCHAR(64) PRIMARY KEY,
    estate_no VARCHAR(64) NOT NULL,
    allotee_name VARCHAR(200),
    tenant_id VARCHAR(100),
    user_uuid VARCHAR(64),
    mobile_number VARCHAR(15),
    alternate_contact_no VARCHAR(15),
    email_id VARCHAR(100),

    asset_reference_no VARCHAR(100),
    property_type VARCHAR(100),
    citizen_request_letter VARCHAR(100),
    allotment_letter VARCHAR(100),
    signed_deed VARCHAR(100),

    agreement_start_date DATE,
    agreement_end_date DATE,
    duration INT,
    rate NUMERIC,
    monthly_rent NUMERIC,
    advance_payment NUMERIC,
    allotment_date DATE,
    advance_payment_date DATE,

    billing_cycle VARCHAR(20) DEFAULT 'MONTHLY'
        CHECK (billing_cycle IN ('MONTHLY', 'QUARTERLY', 'YEARLY')),

    eoffice_file_no VARCHAR(100),
    additional_details JSONB,
    createdby VARCHAR(64),
    lastmodifiedby VARCHAR(64),
    createdtime BIGINT,
    lastmodifiedtime BIGINT,

    CONSTRAINT fk_allotment_estate FOREIGN KEY (estate_no)
        REFERENCES ug_em_asset_details (estate_no)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: ug_em_document_details
CREATE TABLE IF NOT EXISTS ug_em_document_details (
    document_id VARCHAR(64) PRIMARY KEY,
    estate_no VARCHAR(16) NOT NULL UNIQUE,
    document_type VARCHAR(64),
    filestore_id VARCHAR(64) NOT NULL,
    createdby VARCHAR(64),
    lastmodifiedby VARCHAR(64),
    createdtime BIGINT,
    lastmodifiedtime BIGINT,
    CONSTRAINT fk_document_estate FOREIGN KEY (estate_no)
        REFERENCES ug_em_asset_details (estate_no)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Table: ug_em_monthly_rent_payment
CREATE TABLE IF NOT EXISTS ug_em_monthly_rent_payment (
    id VARCHAR(64) PRIMARY KEY,
    allotment_id VARCHAR(64) NOT NULL,
    rent NUMERIC,
    payment_type VARCHAR(20),
    penalty_amount NUMERIC,
    previous_month DATE,
    payment_date DATE,
    last_date_of_payment DATE,
    due_payment_date DATE,
    payment_status VARCHAR(20),
    due_payment NUMERIC,
    validity_days INT,
    createdby VARCHAR(64),
    lastmodifiedby VARCHAR(64),
    createdtime BIGINT,
    lastmodifiedtime BIGINT,
    CONSTRAINT fk_payment_allotment FOREIGN KEY (allotment_id)
        REFERENCES ug_em_allotment_details (allotment_id)
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

-- Sequence: Asset ID
CREATE SEQUENCE IF NOT EXISTS seq_estate_management_asset_id;