CREATE TABLE IF NOT EXISTS upyog_rs_tanker_booking_details (
    booking_id character varying(64),
    booking_no character varying(30),
    tenant_id character varying(64),
    tanker_type character varying(30) NOT NULL,
    tanker_quantity integer,
    water_quantity integer,
    description character varying(100),
    delivery_date date NOT NULL,
    delivery_time TIME WITHOUT TIME ZONE NOT NULL,
    extra_charge character(1) NOT NULL,
    vendor_id character varying(64),
    vehicle_id character varying(64),
    driver_id character varying(64),
    booking_createdby character varying(64), -- created by Citizen or Employee
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint,
    CONSTRAINT upyog_rs_tanker_booking_details_pkey PRIMARY KEY (booking_id),
    CONSTRAINT upyog_rs_tanker_booking_details_vehicle_id_uk UNIQUE (vehicle_id),
    CONSTRAINT upyog_rs_tanker_booking_details_driver_id_uk UNIQUE (driver_id),
    CONSTRAINT upyog_rs_tanker_booking_details_booking_no_uk UNIQUE (booking_no)
);

CREATE TABLE IF NOT EXISTS upyog_rs_applicant_details (
    applicant_id character varying(64),
    booking_id character varying(64),
    name character varying(100) NOT NULL,
    mobile_number character varying(12) NOT NULL,
    gender character(1),
    date_of_birth date,
    email_id character varying(200),
    alternate_number character varying(100),
    guardian_name character varying(100),
    relationship character varying(30),
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint,
    CONSTRAINT upyog_rs_applicant_details_pkey PRIMARY KEY (applicant_id),
    CONSTRAINT upyog_rs_applicant_details_booking_id_fk FOREIGN KEY (booking_id)
        REFERENCES upyog_rs_tanker_booking_details(booking_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS upyog_rs_address_details (
    address_id character varying(64),
    applicant_id character varying(64),
    house_no character varying(100) NOT NULL,
    address_line_1 character varying(100) NOT NULL,
    address_line_2 character varying(100),
    street_name character varying(100),
    landmark character varying(100),
    city character varying(100) NOT NULL,
    city_code character varying(10),
    locality character varying(100) NOT NULL,
    locality_code character varying(30),
    pincode character varying(12) NOT NULL,
    CONSTRAINT upyog_rs_address_details_pkey PRIMARY KEY (address_id),
    CONSTRAINT upyog_rs_address_details_applicant_id_fk FOREIGN KEY (applicant_id)
        REFERENCES upyog_rs_applicant_details(applicant_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS upyog_rs_tanker_booking_details_auditdetails (
    booking_id character varying(64),
    booking_no character varying(30),
    tenant_id character varying(64),
    tanker_type character varying(30) NOT NULL,
    tanker_quantity integer,
    water_quantity integer,
    description character varying(100),
    delivery_date date NOT NULL,
    delivery_time bigint NOT NULL,
    extra_charge character(1) NOT NULL,
    vendor_id character varying(64),
    vehicle_id character varying(64),
    driver_id character varying(64),
    booking_createdby character varying(64),
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint
);

CREATE INDEX IF NOT EXISTS idx_upyog_rs_tanker_booking_details_booking_no ON upyog_rs_tanker_booking_details(booking_no);
CREATE INDEX IF NOT EXISTS idx_upyog_rs_tanker_booking_details_createdby ON upyog_rs_tanker_booking_details(createdby);
CREATE INDEX IF NOT EXISTS idx_upyog_rs_tanker_booking_details_tenant_id ON upyog_rs_tanker_booking_details(tenant_id);

CREATE INDEX IF NOT EXISTS idx_upyog_rs_applicant_details_booking_id ON upyog_rs_applicant_details(booking_id);
CREATE INDEX IF NOT EXISTS idx_upyog_rs_applicant_details_mobile_number ON upyog_rs_applicant_details(mobile_number);

CREATE INDEX IF NOT EXISTS idx_upyog_rs_address_details_applicant_id ON upyog_rs_address_details(applicant_id);

CREATE SEQUENCE IF NOT EXISTS seq_water_tanker_booking_id;

