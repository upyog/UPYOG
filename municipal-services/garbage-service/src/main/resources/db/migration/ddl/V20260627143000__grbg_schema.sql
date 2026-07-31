-- ============================================================
-- SEQUENCES
-- ============================================================

CREATE SEQUENCE seq_id_udd_grbg_account START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE seq_id_udd_grbg_bill START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE seq_ug_grbg_account_id START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE seq_ug_grbg_account_audit START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;


-- ============================================================
-- TABLE: ug_grbg_account  (was udd_grbg_account)
-- ============================================================

CREATE TABLE ug_grbg_account (
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
    channel             VARCHAR(256),
    due_date            DATE
);

ALTER TABLE ug_grbg_account ADD CONSTRAINT pk_id_ug_grbg_account PRIMARY KEY (id);


-- ============================================================
-- TABLE: ug_grbg_application  (was grbg_application)
-- ============================================================

CREATE TABLE ug_grbg_application (
    uuid            VARCHAR(225) PRIMARY KEY,
    application_no  VARCHAR(225),
    status          VARCHAR(50),
    garbage_id      INT8
);

ALTER TABLE ug_grbg_application
    ADD CONSTRAINT grbg_application_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES ug_grbg_account (garbage_id);


-- ============================================================
-- TABLE: ug_grbg_collection_unit  (was grbg_collection_unit)
-- ============================================================

CREATE TABLE ug_grbg_collection_unit (
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
-- TABLE: ug_grbg_document  (was grbg_document)
-- ============================================================

CREATE TABLE ug_grbg_document (
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
-- TABLE: ug_grbg_address  (was grbg_address)
-- ============================================================

CREATE TABLE ug_grbg_address (
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
-- TABLE: ug_grbg_old_details  (was grbg_old_details)
-- ============================================================

CREATE TABLE ug_grbg_old_details (
    uuid            VARCHAR(225) PRIMARY KEY,
    garbage_id      INT8,
    old_garbage_id  VARCHAR(225)
);

ALTER TABLE ug_grbg_old_details
    ADD CONSTRAINT grbg_old_details_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES ug_grbg_account (garbage_id);


-- ============================================================
-- TABLE: ug_grbg_account_audit
-- ============================================================

CREATE TABLE ug_grbg_account_audit (
    auditid                 VARCHAR(128) NOT NULL,
    grbg_application_no     VARCHAR(225),
    status                  VARCHAR(50),
    type                    VARCHAR(50),
    grbg_account_details    JSONB,
    auditcreatedtime        INT8 NOT NULL,
    CONSTRAINT pk_ug_grbg_account_audit PRIMARY KEY (auditid)
);

CREATE INDEX index_ug_grbg_account_audit_grbg_application_no ON ug_grbg_account_audit USING btree (grbg_application_no);
CREATE INDEX index_ug_grbg_account_audit_grbg_application_no_status ON ug_grbg_account_audit USING btree (grbg_application_no, status);
CREATE INDEX index_ug_grbg_account_audit_grbg_application_no_type ON ug_grbg_account_audit USING btree (grbg_application_no, type);
CREATE INDEX index_ug_grbg_account_audit_grbg_application_no_status_type ON ug_grbg_account_audit USING btree (grbg_application_no, status, type);