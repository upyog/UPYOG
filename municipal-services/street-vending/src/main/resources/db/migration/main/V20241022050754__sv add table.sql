CREATE TABLE eg_sv_street_vending_detail
(
    application_id character varying(64)  NOT NULL,
    tenant_id character varying(64)  NOT NULL,
    application_no character varying(64)  NOT NULL,
    application_date bigint NOT NULL,
    certificate_no character varying(64) ,
    approval_date bigint,
    application_status character varying(50)  NOT NULL,
    trade_license_no character varying(64) ,
    vending_activity character varying(100)  NOT NULL,
    vending_zone character varying(100)  NOT NULL,
    cart_latitude numeric(10,6) NOT NULL,
    cart_longitude numeric(10,6) NOT NULL,
    vending_area integer NOT NULL,
    vending_license_certificate_id character varying(64) ,
    local_authority_name character varying(100)  NOT NULL,
    disability_status character varying(50) ,
    beneficiary_of_social_schemes character varying(100) ,
    terms_and_condition character(1)  DEFAULT 'Y'::bpchar,
    createdby character varying(64)  NOT NULL,
    lastmodifiedby character varying(64) ,
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint,
    enrollment_id character varying(100) ,
    payment_receipt_id character varying(64) ,
    vending_license_id character varying(64) ,
    CONSTRAINT eg_sv_street_vending_detail_pkey PRIMARY KEY (application_id),
    CONSTRAINT eg_sv_street_vending_detail_application_no_key UNIQUE (application_no),
    CONSTRAINT eg_sv_street_vending_detail_certificate_no_key UNIQUE (certificate_no)
);


CREATE TABLE eg_sv_vendor_detail
(
    id character varying(64)  NOT NULL,
    application_id character varying(64)  NOT NULL,
    vendor_id character varying(64) ,
    name character varying(100)  NOT NULL,
    father_name character varying(100)  NOT NULL,
    date_of_birth date NOT NULL,
    email_id character varying(200) ,
    mobile_no character varying(100)  NOT NULL,
    gender character(1) ,
    relationship_type character varying(30)  NOT NULL,
    createdby character varying(64)  NOT NULL,
    lastmodifiedby character varying(64) ,
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint,
    user_category character varying(100) ,
    special_category character varying(100) ,
    is_involved boolean,
    CONSTRAINT eg_sv_vendor_detail_pkey PRIMARY KEY (id),
    CONSTRAINT eg_sv_vendor_detail_application_id_fk FOREIGN KEY (application_id)
        REFERENCES eg_sv_street_vending_detail (application_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT eg_sv_vendor_detail_vendor_id_fk FOREIGN KEY (vendor_id)
        REFERENCES eg_sv_vendor_detail (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);


CREATE TABLE eg_sv_address_detail
(
    address_id character varying(64)  NOT NULL,
    address_type character varying(64)  NOT NULL,
    vendor_id character varying(64)  NOT NULL,
    house_no character varying(100)  NOT NULL,
    address_line_1 character varying(150)  NOT NULL,
    address_line_2 character varying(150)  NOT NULL,
    landmark character varying(150) ,
    city character varying(100)  NOT NULL,
    city_code character varying(10)  NOT NULL,
    locality character varying(100)  NOT NULL,
    locality_code character varying(100)  NOT NULL,
    pincode character varying(12)  NOT NULL,
    CONSTRAINT eg_sv_address_detail_id_pk PRIMARY KEY (address_id),
    CONSTRAINT eg_sv_address_detail_vendor_id_fk FOREIGN KEY (vendor_id)
        REFERENCES eg_sv_vendor_detail (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);


CREATE TABLE eg_sv_bank_detail
(
    bank_detail_id character varying(64)  NOT NULL,
    application_id character varying(64)  NOT NULL,
    account_no character varying(64) ,
    ifsc_code character varying(64) ,
    bank_name character varying(300) ,
    bank_branch_name character varying(300) ,
    account_holder_name character varying(300) ,
    createdby character varying(64)  NOT NULL,
    lastmodifiedby character varying(64) ,
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint,
    CONSTRAINT eg_sv_bank_detail_pkey PRIMARY KEY (bank_detail_id),
    CONSTRAINT eg_sv_bank_detail_application_id_fk FOREIGN KEY (application_id)
        REFERENCES eg_sv_street_vending_detail (application_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE eg_sv_document_detail
(
    document_detail_id character varying(64)  NOT NULL,
    application_id character varying(64)  NOT NULL,
    document_type character varying(64) ,
    filestore_id character varying(64)  NOT NULL,
    createdby character varying(64)  NOT NULL,
    lastmodifiedby character varying(64) ,
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint,
    CONSTRAINT eg_sv_document_detail_id_pk PRIMARY KEY (document_detail_id),
    CONSTRAINT eg_sv_document_detail_application_id_fk FOREIGN KEY (application_id)
        REFERENCES eg_sv_street_vending_detail (application_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE eg_sv_operation_time_detail
(
    id character varying(64)  NOT NULL,
    application_id character varying(64)  NOT NULL,
    day_of_week character varying(20)  NOT NULL,
    from_time time without time zone NOT NULL,
    to_time time without time zone NOT NULL,
    createdby character varying(64)  NOT NULL,
    lastmodifiedby character varying(64) ,
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint,
    CONSTRAINT eg_sv_operation_time_detail_pkey PRIMARY KEY (id),
    CONSTRAINT eg_sv_operation_time_detail_application_id_fk FOREIGN KEY (application_id)
        REFERENCES eg_sv_street_vending_detail (application_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

CREATE TABLE eg_sv_street_vending_detail_auditdetails
(
    application_id character varying(64)  NOT NULL,
    tenant_id character varying(64)  NOT NULL,
    application_no character varying(64)  NOT NULL,
    application_date bigint NOT NULL,
    certificate_no character varying(64) ,
    approval_date bigint,
    application_status character varying(50)  NOT NULL,
    trade_license_no character varying(64) ,
    vending_activity character varying(100)  NOT NULL,
    vending_zone character varying(100)  NOT NULL,
    cart_latitude numeric(10,6) NOT NULL,
    cart_longitude numeric(10,6) NOT NULL,
    vending_area integer NOT NULL,
    vending_license_certificate_id character varying(64) ,
    local_authority_name character varying(100)  NOT NULL,
    disability_status character varying(50) ,
    beneficiary_of_social_schemes character varying(100) ,
    terms_and_condition character(1)  DEFAULT 'Y'::bpchar,
    createdby character varying(64)  NOT NULL,
    lastmodifiedby character varying(64) ,
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint,
    enrollment_id character varying(100) ,
    payment_receipt_id character varying(64) ,
    vending_license_id character varying(64) 
);


CREATE TABLE eg_sv_street_vending_draft_detail
(
    draft_id character varying(64)  NOT NULL,
    tenant_id character varying(64)  NOT NULL,
    user_uuid character varying(64)  NOT NULL,
    draft_application_data jsonb NOT NULL,
    createdby character varying(64)  NOT NULL,
    lastmodifiedby character varying(64) ,
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint,
    CONSTRAINT eg_sv_street_vending_draft_detail_pkey PRIMARY KEY (draft_id)
);

CREATE TABLE eg_sv_vendor_detail_auditdetails
(
    id character varying(64)  NOT NULL,
    application_id character varying(64)  NOT NULL,
    vendor_id character varying(64) ,
    name character varying(100)  NOT NULL,
    father_name character varying(100)  NOT NULL,
    date_of_birth date NOT NULL,
    email_id character varying(200) ,
    mobile_no character varying(100)  NOT NULL,
    gender character(1) ,
    relationship_type character varying(30)  NOT NULL,
    createdby character varying(64)  NOT NULL,
    lastmodifiedby character varying(64) ,
    createdtime bigint NOT NULL,
    lastmodifiedtime bigint,
    user_category character varying(100) ,
    special_category character varying(100) ,
    is_involved boolean
);