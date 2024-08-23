CREATE TABLE hpudd_grbg_account (
    id int8,
    uuid VARCHAR(255),
    garbage_id int8 UNIQUE NOT NULL,
    property_id VARCHAR(255),
    type VARCHAR(50),
    name VARCHAR(255),
    mobile_number VARCHAR(20),
    gender VARCHAR(100),
    email_id VARCHAR(100),
    is_owner BOOLEAN,
    user_uuid VARCHAR(255),
    declaration_uuid VARCHAR(255),
    status VARCHAR(50),
    additional_detail JSONB,
    created_by VARCHAR(255),
    created_date int8,
    last_modified_by VARCHAR(255),
    last_modified_date int8
);

ALTER TABLE hpudd_grbg_account ADD CONSTRAINT pk_id_hpudd_grbg_account PRIMARY KEY (id);

CREATE sequence seq_id_hpudd_grbg_account start WITH 1 increment BY 1 no minvalue no maxvalue cache 1;
