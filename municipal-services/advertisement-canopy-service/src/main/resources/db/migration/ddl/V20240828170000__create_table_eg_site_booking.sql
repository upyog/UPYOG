CREATE TABLE eg_site_booking (
    uuid VARCHAR(255),
    application_no VARCHAR(100),
    site_uuid VARCHAR(225),
    applicant_name VARCHAR(255),
    applicant_father_name VARCHAR(255),
    gender VARCHAR(100),
    mobile_number VARCHAR(100),
    email_id VARCHAR(255),
    advertisement_type VARCHAR(100),
    from_date INT8,
    period_in_days INT,
    hoarding_type VARCHAR(225),
    structure VARCHAR(225),
    is_active BOOLEAN,
    additional_detail JSONB,
    created_by VARCHAR(255),
    created_date int8,
    last_modified_by VARCHAR(255),
    last_modified_date int8
);

ALTER TABLE eg_site_booking ADD CONSTRAINT eg_site_booking_pkey PRIMARY KEY (uuid);

