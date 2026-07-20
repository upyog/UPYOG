-- ============================================================
-- SEQUENCES
-- ============================================================

CREATE SEQUENCE seq_id_hpudd_grbg_account START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE seq_id_hpudd_grbg_bill START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE seq_eg_grbg_account_id START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE seq_eg_grbg_account_audit START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;


-- ============================================================
-- TABLE: eg_grbg_account  (was hpudd_grbg_account)
-- ============================================================

CREATE TABLE eg_grbg_account (
    id                  INT8,
    uuid                VARCHAR(255),
    garbage_id          INT8 UNIQUE NOT NULL,
    property_id         VARCHAR(255),
    type                VARCHAR(50),
    name                VARCHAR(255),
    mobile_number       VARCHAR(20),
    gender              VARCHAR(100),
    email_id            VARCHAR(100),
    is_owner            BOOLEAN,
    user_uuid           VARCHAR(255),
    declaration_uuid    VARCHAR(255),
    status              VARCHAR(50),
    additional_detail   JSONB,
    created_by          VARCHAR(255),
    created_date        INT8,
    last_modified_by    VARCHAR(255),
    last_modified_date  INT8,
    tenant_id           VARCHAR(100),
    parent_account      VARCHAR(255),
    is_active           BOOLEAN,
    sub_account_count   INT8 DEFAULT 0,
    business_service    VARCHAR(256),
    approval_date       INT8 DEFAULT NULL,
    channel             VARCHAR(256)
);

ALTER TABLE eg_grbg_account ADD CONSTRAINT pk_id_eg_grbg_account PRIMARY KEY (id);


-- ============================================================
-- TABLE: eg_grbg_bill  (was hpudd_grbg_bill)
-- ============================================================

CREATE TABLE eg_grbg_bill (
    id                              INT8,
    bill_ref_no                     VARCHAR(255),
    garbage_id                      INT8,
    bill_amount                     NUMERIC(10, 2),
    arrear_amount                   NUMERIC(10, 2),
    panelty_amount                  NUMERIC(10, 2),
    discount_amount                 NUMERIC(10, 2),
    total_bill_amount               NUMERIC(10, 2),
    total_bill_amount_after_due_date NUMERIC(10, 2),
    bill_generated_by               VARCHAR(255),
    bill_generated_date             INT8,
    bill_due_date                   INT8,
    bill_period                     VARCHAR(50),
    bank_discount_amount            NUMERIC(10, 2),
    payment_id                      VARCHAR(255),
    payment_status                  VARCHAR(50),
    bill_for                        VARCHAR(50),
    is_active                       BOOLEAN,
    created_by                      VARCHAR(255),
    created_date                    INT8,
    last_modified_by                VARCHAR(255),
    last_modified_date              INT8
);

ALTER TABLE eg_grbg_bill ADD CONSTRAINT pk_id_eg_grbg_bill PRIMARY KEY (id);
ALTER TABLE eg_grbg_bill ADD CONSTRAINT fk_garbage_id_eg_grbg_bill FOREIGN KEY (garbage_id) REFERENCES eg_grbg_account (garbage_id);


-- ============================================================
-- TABLE: eg_grbg_application  (was grbg_application)
-- ============================================================

CREATE TABLE eg_grbg_application (
    uuid            VARCHAR(225) PRIMARY KEY,
    application_no  VARCHAR(225),
    status          VARCHAR(50),
    garbage_id      INT8
);

ALTER TABLE eg_grbg_application
    ADD CONSTRAINT grbg_application_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES eg_grbg_account (garbage_id);


-- ============================================================
-- TABLE: eg_grbg_commercial_details  (was grbg_commercial_details)
-- ============================================================

CREATE TABLE eg_grbg_commercial_details (
    uuid            VARCHAR(225) PRIMARY KEY,
    garbage_id      INT8,
    business_name   VARCHAR(100),
    business_type   VARCHAR(100),
    owner_user_uuid VARCHAR(255)
);

ALTER TABLE eg_grbg_commercial_details
    ADD CONSTRAINT grbg_commercial_details_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES eg_grbg_account (garbage_id);


-- ============================================================
-- TABLE: eg_grbg_collection_unit  (was grbg_collection_unit)
-- ============================================================

CREATE TABLE eg_grbg_collection_unit (
    uuid                    VARCHAR(225) PRIMARY KEY,
    garbage_id              INT8,
    unit_name               VARCHAR(225),
    unit_type               VARCHAR(100),
    category                VARCHAR(100),
    sub_category            VARCHAR(100),
    sub_category_type       VARCHAR(100),
    unit_ward               VARCHAR(225),
    ulb_name                VARCHAR(225),
    type_of_ulb             VARCHAR(225),
    is_active               BOOLEAN,
    isbplunit               BOOLEAN DEFAULT FALSE,
    isvariablecalculation   BOOLEAN DEFAULT FALSE,
    isbulkgeneration        BOOLEAN DEFAULT FALSE,
    no_of_units             INTEGER DEFAULT 0,
    ismonthlybilling        BOOLEAN DEFAULT TRUE,
    owner_type              VARCHAR(60),
    is_inheritance          BOOLEAN,
    special_category        VARCHAR(100)
);


-- ============================================================
-- TABLE: eg_grbg_collection_staff  (was grbg_collection_staff)
-- ============================================================

CREATE TABLE eg_grbg_collection_staff (
    uuid                        VARCHAR(225) PRIMARY KEY,
    grbg_collection_unit_uuid   VARCHAR(225),
    employee_id                 VARCHAR(225),
    role                        VARCHAR(50),
    is_active                   BOOLEAN
);

ALTER TABLE eg_grbg_collection_staff
    ADD CONSTRAINT grbg_collection_staff_unit_uuid_fk FOREIGN KEY (grbg_collection_unit_uuid) REFERENCES eg_grbg_collection_unit (uuid);


-- ============================================================
-- TABLE: eg_grbg_document  (was grbg_document)
-- ============================================================

CREATE TABLE eg_grbg_document (
    uuid            VARCHAR(225) PRIMARY KEY,
    garbage_id      INT8,
    doc_ref_id      VARCHAR(225),
    doc_name        VARCHAR(100),
    doc_type        VARCHAR(100),
    doc_category    VARCHAR(100),
    tbl_ref_uuid    VARCHAR(225),
    file_store_id   VARCHAR(225),
    document_uid    VARCHAR(225),
    document_type   VARCHAR(225)
);


-- ============================================================
-- TABLE: eg_grbg_charge  (was grbg_charge)
-- ============================================================

CREATE TABLE eg_grbg_charge (
    uuid            VARCHAR(225) PRIMARY KEY,
    category        VARCHAR(100),
    type            VARCHAR(225),
    amount_per_day  DECIMAL(10, 2),
    amount_pm       DECIMAL(10, 2),
    is_active       BOOLEAN
);


-- ============================================================
-- TABLE: eg_grbg_collection  (was grbg_collection)
-- ============================================================

CREATE TABLE eg_grbg_collection (
    uuid            VARCHAR(225) PRIMARY KEY,
    garbage_id      INT8,
    staff_uuid      VARCHAR(225),
    collec_type     VARCHAR(100),
    start_date      INT8,
    end_date        INT8,
    is_active       BOOLEAN,
    createdby       VARCHAR(225),
    createddate     INT8,
    lastmodifiedby  VARCHAR(225),
    lastmodifieddate INT8
);

ALTER TABLE eg_grbg_collection
    ADD CONSTRAINT grbg_collection_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES eg_grbg_account (garbage_id),
    ADD CONSTRAINT grbg_collection_staff_uuid_fk FOREIGN KEY (staff_uuid) REFERENCES eg_grbg_collection_staff (uuid);


-- ============================================================
-- TABLE: eg_grbg_address  (was grbg_address)
-- ============================================================

CREATE TABLE eg_grbg_address (
    uuid            VARCHAR(225) PRIMARY KEY,
    garbage_id      INT8,
    address_type    VARCHAR(100),
    address1        VARCHAR(255),
    address2        VARCHAR(255),
    city            VARCHAR(225),
    state           VARCHAR(225),
    pincode         VARCHAR(100),
    zone            VARCHAR(100),
    ulb_name        VARCHAR(100),
    ulb_type        VARCHAR(100),
    ward_name       VARCHAR(100),
    additional_detail JSONB,
    is_active       BOOLEAN
);


-- ============================================================
-- TABLE: eg_grbg_scheduled_requests  (was grbg_scheduled_requests)
-- ============================================================

CREATE TABLE eg_grbg_scheduled_requests (
    uuid        VARCHAR(225) PRIMARY KEY,
    garbage_id  INT8,
    type        VARCHAR(225),
    start_date  INT8,
    end_date    INT8,
    is_active   BOOLEAN
);

ALTER TABLE eg_grbg_scheduled_requests
    ADD CONSTRAINT grbg_scheduled_requests_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES eg_grbg_account (garbage_id);


-- ============================================================
-- TABLE: eg_grbg_old_details  (was grbg_old_details)
-- ============================================================

CREATE TABLE eg_grbg_old_details (
    uuid            VARCHAR(225) PRIMARY KEY,
    garbage_id      INT8,
    old_garbage_id  VARCHAR(225)
);

ALTER TABLE eg_grbg_old_details
    ADD CONSTRAINT grbg_old_details_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES eg_grbg_account (garbage_id);


-- ============================================================
-- TABLE: eg_grbg_declaration  (was grbg_declaration)
-- ============================================================

CREATE TABLE eg_grbg_declaration (
    uuid        VARCHAR(225) PRIMARY KEY,
    statement   TEXT,
    is_active   BOOLEAN
);


-- ============================================================
-- TABLE: eg_grbg_bill_tracker
-- ============================================================

CREATE TABLE eg_grbg_bill_tracker (
    uuid                        VARCHAR(256) NOT NULL,
    grbg_application_id         VARCHAR(256),
    tenant_id                   VARCHAR(256),
    month                       VARCHAR(256),
    year                        VARCHAR(256),
    from_date                   VARCHAR(256),
    to_date                     VARCHAR(256),
    grbg_bill_amount            NUMERIC(10, 2),
    created_by                  VARCHAR(128) NOT NULL,
    created_time                INT8 NOT NULL,
    last_modified_by            VARCHAR(128),
    last_modified_time          INT8,
    ward                        VARCHAR(256) DEFAULT NULL,
    bill_id                     VARCHAR(256) DEFAULT NULL,
    penalty_amount              NUMERIC(12, 2),
    grbg_bill_without_penalty   NUMERIC(12, 2),
    type                        VARCHAR(100) DEFAULT 'MONTHLY',
    additionaldetail            JSONB DEFAULT NULL,
    status                      VARCHAR(20) DEFAULT 'ACTIVE',
    rebate_amount               NUMERIC(10, 2),
    garbage_bill_without_rebate NUMERIC(10, 2),
    demand_id                   VARCHAR(256) DEFAULT NULL,
    CONSTRAINT eg_grbg_bill_tracker_pk PRIMARY KEY (uuid)
);


-- ============================================================
-- TABLE: eg_grbg_account_audit
-- ============================================================

CREATE TABLE eg_grbg_account_audit (
    auditid                 VARCHAR(128) NOT NULL,
    grbg_application_no     VARCHAR(225),
    status                  VARCHAR(50),
    type                    VARCHAR(50),
    grbg_account_details    JSONB,
    auditcreatedtime        INT8 NOT NULL,
    CONSTRAINT pk_eg_grbg_account_audit PRIMARY KEY (auditid)
);

CREATE INDEX index_eg_grbg_account_audit_grbg_application_no ON eg_grbg_account_audit USING btree (grbg_application_no);
CREATE INDEX index_eg_grbg_account_audit_grbg_application_no_status ON eg_grbg_account_audit USING btree (grbg_application_no, status);
CREATE INDEX index_eg_grbg_account_audit_grbg_application_no_type ON eg_grbg_account_audit USING btree (grbg_application_no, type);
CREATE INDEX index_eg_grbg_account_audit_grbg_application_no_status_type ON eg_grbg_account_audit USING btree (grbg_application_no, status, type);


-- ============================================================
-- TABLE: eg_bill_failure
-- ============================================================

CREATE TABLE IF NOT EXISTS eg_bill_failure (
    id                  UUID,
    consumer_code       VARCHAR(100),
    module_name         VARCHAR(50),
    tenant_id           VARCHAR(64),
    failure_reason      TEXT,
    month               VARCHAR(256),
    year                VARCHAR(256),
    from_date           VARCHAR(256),
    to_date             VARCHAR(256),
    request_payload     JSONB,
    response_payload    JSONB,
    error_json          JSONB,
    status_code         VARCHAR(10),
    created_time        BIGINT,
    last_modified_time  BIGINT,
    CONSTRAINT eg_bill_failure_pk PRIMARY KEY (id),
    CONSTRAINT unique_consumer_period UNIQUE (consumer_code, from_date, to_date)
);
