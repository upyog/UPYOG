-- Main Booking Table
CREATE TABLE IF NOT EXISTS upyog_rs_mobile_toilet_booking_details (
    booking_id character varying(64),
    booking_no character varying(30),
    tenant_id character varying(64),
    no_of_mobile_toilet integer,
    address_detail_id character varying(64),
    mobile_number character varying(12),
    locality_code character varying(30),
    payment_receipt_filestore_id character varying(64),
    description character varying(100),
    applicant_uuid character varying(64),
    delivery_from_date date NOT NULL,
    delivery_to_date date NOT NULL,
    delivery_from_time TIME WITHOUT TIME ZONE NOT NULL,
    delivery_to_time TIME WITHOUT TIME ZONE NOT NULL,
    vendor_id character varying(64),
    vehicle_id character varying(64),
    driver_id character varying(64),
    vehicle_type character varying(64),
    vehicle_capacity character varying(64),
    booking_createdby character varying(64),
    booking_status character varying(64),
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint,
    CONSTRAINT upyog_rs_mobile_toilet_booking_details_pkey PRIMARY KEY (booking_id),
    CONSTRAINT upyog_rs_mobile_toilet_booking_details_booking_no_uk UNIQUE (booking_no)
);

-- Applicant Table
CREATE TABLE IF NOT EXISTS upyog_rs_mobile_toilet_applicant_details (
    applicant_id         VARCHAR(64) PRIMARY KEY,
    booking_id           VARCHAR(64),
    name                 VARCHAR(100) NOT NULL,
    mobile_number        VARCHAR(12) NOT NULL,
    email_id             VARCHAR(200),
    alternate_number     VARCHAR(100),
    createdby            VARCHAR(64),
    lastmodifiedby       VARCHAR(64),
    createdtime          BIGINT NOT NULL,
    lastmodifiedtime     BIGINT
);

-- Address Table
CREATE TABLE IF NOT EXISTS upyog_rs_mobile_toilet_address_details (
    address_id           VARCHAR(64),
    applicant_id         VARCHAR(64),
    house_no             VARCHAR(100) NOT NULL,
    address_line_1       VARCHAR(100) NOT NULL,
    address_line_2       VARCHAR(100),
    street_name          VARCHAR(100),
    landmark             VARCHAR(100),
    city                 VARCHAR(100) NOT NULL,
    city_code            VARCHAR(10),
    locality             VARCHAR(100) NOT NULL,
    locality_code        VARCHAR(30),
    pincode              VARCHAR(12) NOT NULL,
    CONSTRAINT fk_mobile_toilet_applicant_id
        FOREIGN KEY (applicant_id)
        REFERENCES public.upyog_rs_mobile_toilet_applicant_details(applicant_id)
        ON DELETE CASCADE
);

-- Audit Table
CREATE TABLE IF NOT EXISTS upyog_rs_mobile_toilet_booking_details_auditdetails (
    booking_id character varying(64),
    booking_no character varying(30),
    tenant_id character varying(64),
    no_of_mobile_toilet integer,
    address_detail_id character varying(64),
    mobile_number character varying(12),
    locality_code character varying(30),
    payment_receipt_filestore_id character varying(64),
    description character varying(100),
    applicant_uuid character varying(64),
    delivery_from_date date NOT NULL,
    delivery_to_date date NOT NULL,
    delivery_from_time TIME WITHOUT TIME ZONE NOT NULL,
    delivery_to_time TIME WITHOUT TIME ZONE NOT NULL,
    vendor_id character varying(64),
    vehicle_id character varying(64),
    driver_id character varying(64),
    vehicle_type character varying(64),
    vehicle_capacity character varying(64),
    booking_createdby character varying(64),
    booking_status character varying(64),
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint
);

-- Indexes for Booking Table
CREATE INDEX IF NOT EXISTS idx_upyog_rs_mobile_toilet_booking_details_booking_no
    ON upyog_rs_mobile_toilet_booking_details(booking_no);

CREATE INDEX IF NOT EXISTS idx_upyog_rs_mobile_toilet_booking_details_createdby
    ON upyog_rs_mobile_toilet_booking_details(createdby);

CREATE INDEX IF NOT EXISTS idx_upyog_rs_mobile_toilet_booking_details_tenant_id
    ON upyog_rs_mobile_toilet_booking_details(tenant_id);

-- Indexes for Applicant Table
CREATE INDEX IF NOT EXISTS idx_upyog_rs_mobile_toilet_applicant_details_booking_id
    ON upyog_rs_mobile_toilet_applicant_details(booking_id);

CREATE INDEX IF NOT EXISTS idx_upyog_rs_mobile_toilet_applicant_details_mobile_number
    ON upyog_rs_mobile_toilet_applicant_details(mobile_number);

-- Index for Address Table
CREATE INDEX IF NOT EXISTS idx_upyog_rs_mobile_toilet_address_details_applicant_id
    ON upyog_rs_mobile_toilet_address_details(applicant_id);

-- Sequence
CREATE SEQUENCE IF NOT EXISTS seq_mobile_toilet_booking_id;
