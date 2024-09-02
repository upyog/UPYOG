CREATE TABLE eg_site_application (
    id int8,
    uuid VARCHAR(255),
    site_id VARCHAR(200) UNIQUE NOT NULL,
    site_name VARCHAR(255),
    site_description VARCHAR(200),
    gps_location VARCHAR(50),
    site_address VARCHAR(255),
    site_cost VARCHAR(20),
    site_photograph VARCHAR(100),
    structure VARCHAR(100),
    size_length int8,
    size_width int8,
    led_selection VARCHAR(100),
    security_amount int8,
    powered VARCHAR(255),
    others VARCHAR(255),
    district_name VARCHAR(100),
    ulb_name VARCHAR(100),
    ulb_type VARCHAR(100),
    ward_number VARCHAR(100),
    pincode VARCHAR(100),
    additional_detail JSONB,
    created_by VARCHAR(255),
    created_date int8,
    last_modified_by VARCHAR(255),
    last_modified_date int8,
    site_type VARCHAR(200),
    account_id VARCHAR(200),
    status VARCHAR(100),
    is_active boolean,
    tenant_id VARCHAR(50)
);

ALTER TABLE eg_site_application ADD CONSTRAINT pk_id_eg_site_application PRIMARY KEY (id);

CREATE sequence seq_id_eg_site_application start WITH 1 increment BY 1 no minvalue no maxvalue cache 1;
