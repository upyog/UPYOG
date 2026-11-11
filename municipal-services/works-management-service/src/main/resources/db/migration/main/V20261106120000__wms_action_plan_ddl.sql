-- =====================================================================
-- UPYOG Works Management Service (WMS) - Phase 1 : Action Plan  (rev 4)
-- Flyway migration : src/main/resources/db/migration/main/
-- Conventions (DIGIT):
--   * id           -> UUID as VARCHAR(64)
--   * dates/times  -> BIGINT epoch milliseconds
--   * money        -> NUMERIC(15,2)
--   * master codes -> MDMS codes only
-- Workflow : egov-workflow-v2 (BusinessService "WMS_ACTION_PLAN")
-- IDs      : egov-idgen      Files : egov-filestore
--
-- Versioning : every revision = new row, same projectid / actionplannumber,
--              version + 1. Previous version = (tenantid, projectid, version - 1).
-- 1:1 child tables : FK lives in the child; 1:1 enforced only by a partial
--              UNIQUE index (uk_..._one). Drop the index to allow 1:many.
-- =====================================================================


-- ---------------------------------------------------------------------
-- 1. ACTION PLAN (header) - one row per VERSION
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eg_wms_action_plan (
    id                      VARCHAR(64)   NOT NULL,
    tenantid                VARCHAR(64)   NOT NULL,             -- ULB

    applicationnumber       VARCHAR(64)   NOT NULL,             -- idgen, first save (new per version)
    actionplannumber        VARCHAR(64),                        -- idgen, on approval (same for all versions)
    version                 INTEGER       NOT NULL DEFAULT 1,
    islatestversion         BOOLEAN       NOT NULL DEFAULT TRUE,

    financialyear           VARCHAR(16)   NOT NULL,             -- MDMS
    projectid               VARCHAR(12)   NOT NULL,
    projectname             VARCHAR(256)  NOT NULL,

    -- scheme (Section 2)
    schemecode              VARCHAR(64),                        -- MDMS
    subschemecode           VARCHAR(64),                        -- MDMS

    -- offline approval & sanction (Sections 1, 2)
    offlineapprovaldate     BIGINT,                             -- "Date of Approval"
    approvingauthority      VARCHAR(256),                       -- "Approved By" (competent authority)
    adminsanction           BOOLEAN       NOT NULL DEFAULT FALSE,
    technicalsanction       BOOLEAN,                            -- Yes / No

    -- financial
    approvedamount          NUMERIC(15,2),                      -- frozen total (baseline)
    fundsource              VARCHAR(32),                        -- GOVT_GRANT / OSR / PARTIAL
    totalcomponents         INTEGER,

    declarationaccepted     BOOLEAN       NOT NULL DEFAULT FALSE,

    -- system workflow lifecycle
    status                  VARCHAR(64)   NOT NULL,             -- mirrors workflow state
    submitteddate           BIGINT,                             -- set on SUBMIT (latest submit)
    approveddate            BIGINT,                             -- set on final APPROVE
    approvedby              VARCHAR(64),                        -- user uuid of final approver

    isactive                BOOLEAN       NOT NULL DEFAULT TRUE,
    additionaldetails       JSONB,
    createdby               VARCHAR(64)   NOT NULL,
    createdtime             BIGINT        NOT NULL,
    lastmodifiedby          VARCHAR(64),
    lastmodifiedtime        BIGINT,

    CONSTRAINT pk_eg_wms_action_plan PRIMARY KEY (id),
    CONSTRAINT uk_eg_wms_ap_appno UNIQUE (tenantid, applicationnumber),
    CONSTRAINT uk_eg_wms_ap_project_version UNIQUE (tenantid, projectid, version),
    CONSTRAINT chk_eg_wms_ap_amount CHECK (approvedamount IS NULL OR approvedamount >= 0)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_eg_wms_ap_apno_latest
    ON eg_wms_action_plan (tenantid, actionplannumber)
    WHERE actionplannumber IS NOT NULL AND islatestversion = TRUE;

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_tenant_status ON eg_wms_action_plan (tenantid, status);
CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_fy            ON eg_wms_action_plan (tenantid, financialyear);
CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_scheme        ON eg_wms_action_plan (tenantid, schemecode);
CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_apno          ON eg_wms_action_plan (actionplannumber);
CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_approveddate  ON eg_wms_action_plan (tenantid, approveddate);


-- ---------------------------------------------------------------------
-- 2. PROJECT DETAIL - purpose, time range, type/category of work
--    1:1 now
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eg_wms_ap_project_detail (
    id                      VARCHAR(64)   NOT NULL,
    tenantid                VARCHAR(64)   NOT NULL,
    actionplanid            VARCHAR(64)   NOT NULL,
    purposecode             VARCHAR(64),                        -- MDMS
    purposeother            VARCHAR(256),                       -- when purpose = OTHERS
    startdate               BIGINT,
    enddate                 BIGINT,
    typeofwork              VARCHAR(64),                        -- MDMS
    workcategory            VARCHAR(64),                        -- MDMS
    isactive                BOOLEAN       NOT NULL DEFAULT TRUE,
    additionaldetails       JSONB,                              -- e.g. future work-category attributes
    createdby               VARCHAR(64)   NOT NULL,
    createdtime             BIGINT        NOT NULL,
    lastmodifiedby          VARCHAR(64),
    lastmodifiedtime        BIGINT,

    CONSTRAINT pk_eg_wms_ap_project_detail PRIMARY KEY (id),
    CONSTRAINT fk_eg_wms_ap_pd_ap FOREIGN KEY (actionplanid) REFERENCES eg_wms_action_plan (id),
    CONSTRAINT chk_eg_wms_ap_pd_dates CHECK (startdate IS NULL OR enddate IS NULL OR enddate >= startdate)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_eg_wms_ap_pd_one
    ON eg_wms_ap_project_detail (actionplanid) WHERE isactive = TRUE;


-- ---------------------------------------------------------------------
-- 3. RECOMMENDATION - 1:many (e.g. MP and MLA both recommend)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eg_wms_ap_recommendation (
    id                      VARCHAR(64)   NOT NULL,
    tenantid                VARCHAR(64)   NOT NULL,
    actionplanid            VARCHAR(64)   NOT NULL,
    recommendedby           VARCHAR(64)   NOT NULL,             -- MDMS: ULB_INITIATIVE / MP / MLA / OTHERS
    recommendedbyname       VARCHAR(256),                       -- when OTHERS
    isactive                BOOLEAN       NOT NULL DEFAULT TRUE,
    additionaldetails       JSONB,
    createdby               VARCHAR(64)   NOT NULL,
    createdtime             BIGINT        NOT NULL,
    lastmodifiedby          VARCHAR(64),
    lastmodifiedtime        BIGINT,

    CONSTRAINT pk_eg_wms_ap_recommendation PRIMARY KEY (id),
    CONSTRAINT fk_eg_wms_ap_rec_ap FOREIGN KEY (actionplanid) REFERENCES eg_wms_action_plan (id)
);

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_rec_ap ON eg_wms_ap_recommendation (actionplanid);


-- ---------------------------------------------------------------------
-- 4. LOCATION - state, district, constituencies
--    1:1 now
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eg_wms_ap_location (
    id                      VARCHAR(64)   NOT NULL,
    tenantid                VARCHAR(64)   NOT NULL,
    actionplanid            VARCHAR(64)   NOT NULL,
    statecode               VARCHAR(64)   NOT NULL,             -- MDMS
    districtcode            VARCHAR(64)   NOT NULL,             -- MDMS
    mlaconstituency         VARCHAR(256),
    mpconstituency          VARCHAR(256),
    isactive                BOOLEAN       NOT NULL DEFAULT TRUE,
    additionaldetails       JSONB,
    createdby               VARCHAR(64)   NOT NULL,
    createdtime             BIGINT        NOT NULL,
    lastmodifiedby          VARCHAR(64),
    lastmodifiedtime        BIGINT,

    CONSTRAINT pk_eg_wms_ap_location PRIMARY KEY (id),
    CONSTRAINT fk_eg_wms_ap_loc_ap FOREIGN KEY (actionplanid) REFERENCES eg_wms_action_plan (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_eg_wms_ap_loc_one
    ON eg_wms_ap_location (actionplanid) WHERE isactive = TRUE;
CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_loc_district
    ON eg_wms_ap_location (tenantid, districtcode);


-- ---------------------------------------------------------------------
-- 5. COMPONENT - constituent works (Section 3); anchor for Mode of Execution
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eg_wms_ap_component (
    id                      VARCHAR(64)   NOT NULL,             -- row id (per version)
    tenantid                VARCHAR(64)   NOT NULL,
    actionplanid            VARCHAR(64)   NOT NULL,
    componentnumber         VARCHAR(64)   NOT NULL,             -- STABLE id across versions
    componentname           VARCHAR(256)  NOT NULL,
    category                VARCHAR(64),                        -- MDMS
    subcategory             VARCHAR(64),
    fundsource              VARCHAR(32),                        -- GOVT_GRANT / OSR / PARTIAL
    approvedamount          NUMERIC(15,2),
    status                  VARCHAR(32)   NOT NULL DEFAULT 'ACTIVE',  -- ACTIVE / INACTIVE
    isactive                BOOLEAN       NOT NULL DEFAULT TRUE,
    additionaldetails       JSONB,
    createdby               VARCHAR(64)   NOT NULL,
    createdtime             BIGINT        NOT NULL,
    lastmodifiedby          VARCHAR(64),
    lastmodifiedtime        BIGINT,

    CONSTRAINT pk_eg_wms_ap_component PRIMARY KEY (id),
    CONSTRAINT fk_eg_wms_ap_component_ap FOREIGN KEY (actionplanid) REFERENCES eg_wms_action_plan (id),
    CONSTRAINT uk_eg_wms_ap_component_no UNIQUE (actionplanid, componentnumber),
    CONSTRAINT chk_eg_wms_ap_component_amount CHECK (approvedamount IS NULL OR approvedamount >= 0)
);

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_component_ap ON eg_wms_ap_component (actionplanid);
CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_component_no ON eg_wms_ap_component (tenantid, componentnumber);


-- ---------------------------------------------------------------------
-- 6. FUND DETAIL - componentid NULL = plan level, else component level
--    PARTIAL = two rows (GOVT_GRANT + OSR)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eg_wms_ap_fund_detail (
    id                      VARCHAR(64)   NOT NULL,
    tenantid                VARCHAR(64)   NOT NULL,
    actionplanid            VARCHAR(64)   NOT NULL,
    componentid             VARCHAR(64),
    fundtype                VARCHAR(32)   NOT NULL,             -- GOVT_GRANT / OSR
    sourcecode              VARCHAR(64),                        -- MDMS
    fundname                VARCHAR(256),
    amount                  NUMERIC(15,2) NOT NULL,
    isactive                BOOLEAN       NOT NULL DEFAULT TRUE,
    additionaldetails       JSONB,
    createdby               VARCHAR(64)   NOT NULL,
    createdtime             BIGINT        NOT NULL,
    lastmodifiedby          VARCHAR(64),
    lastmodifiedtime        BIGINT,

    CONSTRAINT pk_eg_wms_ap_fund_detail PRIMARY KEY (id),
    CONSTRAINT fk_eg_wms_ap_fund_ap   FOREIGN KEY (actionplanid) REFERENCES eg_wms_action_plan (id),
    CONSTRAINT fk_eg_wms_ap_fund_comp FOREIGN KEY (componentid)  REFERENCES eg_wms_ap_component (id),
    CONSTRAINT chk_eg_wms_ap_fund_amount CHECK (amount >= 0)
);

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_fund_ap   ON eg_wms_ap_fund_detail (actionplanid);
CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_fund_comp ON eg_wms_ap_fund_detail (componentid);


-- ---------------------------------------------------------------------
-- 7. BENEFICIARY - componentid NULL = plan level, else component level
--    "Beneficiary = Yes" is derived from active rows existing.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eg_wms_ap_beneficiary (
    id                      VARCHAR(64)   NOT NULL,
    tenantid                VARCHAR(64)   NOT NULL,
    actionplanid            VARCHAR(64)   NOT NULL,
    componentid             VARCHAR(64),
    name                    VARCHAR(256)  NOT NULL,
    remark                  VARCHAR(1024),
    isactive                BOOLEAN       NOT NULL DEFAULT TRUE,
    additionaldetails       JSONB,
    createdby               VARCHAR(64)   NOT NULL,
    createdtime             BIGINT        NOT NULL,
    lastmodifiedby          VARCHAR(64),
    lastmodifiedtime        BIGINT,

    CONSTRAINT pk_eg_wms_ap_beneficiary PRIMARY KEY (id),
    CONSTRAINT fk_eg_wms_ap_ben_ap   FOREIGN KEY (actionplanid) REFERENCES eg_wms_action_plan (id),
    CONSTRAINT fk_eg_wms_ap_ben_comp FOREIGN KEY (componentid)  REFERENCES eg_wms_ap_component (id)
);

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_ben_ap ON eg_wms_ap_beneficiary (actionplanid);


-- ---------------------------------------------------------------------
-- DOCUMENT tables - DIGIT standard (Document contract: id, documentType,
-- fileStoreId, documentUid). One table per owner, each with a real FK.
-- File name / size come from egov-filestore metadata.
-- ---------------------------------------------------------------------

-- 8. Plan documents
--    documenttype (MDMS): ACTION_PLAN, ADMIN_SANCTION, TECHNICAL_SANCTION,
--                         APPROVAL_PROCEEDING, REFERENCE
CREATE TABLE IF NOT EXISTS eg_wms_ap_document (
    id                      VARCHAR(64)   NOT NULL,
    tenantid                VARCHAR(64)   NOT NULL,
    actionplanid            VARCHAR(64)   NOT NULL,
    documenttype            VARCHAR(64)   NOT NULL,
    filestoreid             VARCHAR(64)   NOT NULL,
    documentuid             VARCHAR(64),
    isactive                BOOLEAN       NOT NULL DEFAULT TRUE,
    createdby               VARCHAR(64)   NOT NULL,             -- uploaded by
    createdtime             BIGINT        NOT NULL,             -- upload date
    lastmodifiedby          VARCHAR(64),
    lastmodifiedtime        BIGINT,

    CONSTRAINT pk_eg_wms_ap_document PRIMARY KEY (id),
    CONSTRAINT fk_eg_wms_ap_doc_ap FOREIGN KEY (actionplanid) REFERENCES eg_wms_action_plan (id)
);

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_doc_ap ON eg_wms_ap_document (actionplanid);


-- 9. Recommendation documents
--    documenttype (MDMS): RECOMMENDATION
CREATE TABLE IF NOT EXISTS eg_wms_ap_recommendation_document (
    id                      VARCHAR(64)   NOT NULL,
    tenantid                VARCHAR(64)   NOT NULL,
    recommendationid        VARCHAR(64)   NOT NULL,
    documenttype            VARCHAR(64)   NOT NULL,
    filestoreid             VARCHAR(64)   NOT NULL,
    documentuid             VARCHAR(64),
    isactive                BOOLEAN       NOT NULL DEFAULT TRUE,
    createdby               VARCHAR(64)   NOT NULL,
    createdtime             BIGINT        NOT NULL,
    lastmodifiedby          VARCHAR(64),
    lastmodifiedtime        BIGINT,

    CONSTRAINT pk_eg_wms_ap_rec_document PRIMARY KEY (id),
    CONSTRAINT fk_eg_wms_ap_rec_doc_rec FOREIGN KEY (recommendationid) REFERENCES eg_wms_ap_recommendation (id)
);

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_rec_doc_rec ON eg_wms_ap_recommendation_document (recommendationid);


-- 10. Component documents
--     documenttype (MDMS): COMPONENT_SUPPORTING
CREATE TABLE IF NOT EXISTS eg_wms_ap_component_document (
    id                      VARCHAR(64)   NOT NULL,
    tenantid                VARCHAR(64)   NOT NULL,
    componentid             VARCHAR(64)   NOT NULL,
    documenttype            VARCHAR(64)   NOT NULL,
    filestoreid             VARCHAR(64)   NOT NULL,
    documentuid             VARCHAR(64),
    isactive                BOOLEAN       NOT NULL DEFAULT TRUE,
    createdby               VARCHAR(64)   NOT NULL,
    createdtime             BIGINT        NOT NULL,
    lastmodifiedby          VARCHAR(64),
    lastmodifiedtime        BIGINT,

    CONSTRAINT pk_eg_wms_ap_comp_document PRIMARY KEY (id),
    CONSTRAINT fk_eg_wms_ap_comp_doc_comp FOREIGN KEY (componentid) REFERENCES eg_wms_ap_component (id)
);

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_comp_doc_comp ON eg_wms_ap_component_document (componentid);


-- ---------------------------------------------------------------------
-- 11. AUDIT tables - data modification history only.
--    Persister inserts the OLD row here before every UPDATE.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eg_wms_action_plan_audit       (LIKE eg_wms_action_plan       INCLUDING DEFAULTS);
CREATE TABLE IF NOT EXISTS eg_wms_ap_project_detail_audit (LIKE eg_wms_ap_project_detail INCLUDING DEFAULTS);
CREATE TABLE IF NOT EXISTS eg_wms_ap_recommendation_audit (LIKE eg_wms_ap_recommendation INCLUDING DEFAULTS);
CREATE TABLE IF NOT EXISTS eg_wms_ap_location_audit       (LIKE eg_wms_ap_location       INCLUDING DEFAULTS);
CREATE TABLE IF NOT EXISTS eg_wms_ap_component_audit      (LIKE eg_wms_ap_component      INCLUDING DEFAULTS);
CREATE TABLE IF NOT EXISTS eg_wms_ap_fund_detail_audit    (LIKE eg_wms_ap_fund_detail    INCLUDING DEFAULTS);
CREATE TABLE IF NOT EXISTS eg_wms_ap_beneficiary_audit    (LIKE eg_wms_ap_beneficiary    INCLUDING DEFAULTS);
CREATE TABLE IF NOT EXISTS eg_wms_ap_document_audit       (LIKE eg_wms_ap_document       INCLUDING DEFAULTS);
CREATE TABLE IF NOT EXISTS eg_wms_ap_recommendation_document_audit (LIKE eg_wms_ap_recommendation_document INCLUDING DEFAULTS);
CREATE TABLE IF NOT EXISTS eg_wms_ap_component_document_audit      (LIKE eg_wms_ap_component_document      INCLUDING DEFAULTS);

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_audit_id   ON eg_wms_action_plan_audit (id);
CREATE INDEX IF NOT EXISTS idx_eg_wms_pd_audit_id   ON eg_wms_ap_project_detail_audit (id);
CREATE INDEX IF NOT EXISTS idx_eg_wms_rec_audit_id  ON eg_wms_ap_recommendation_audit (id);
CREATE INDEX IF NOT EXISTS idx_eg_wms_loc_audit_id  ON eg_wms_ap_location_audit (id);
CREATE INDEX IF NOT EXISTS idx_eg_wms_comp_audit_id ON eg_wms_ap_component_audit (id);
CREATE INDEX IF NOT EXISTS idx_eg_wms_fund_audit_id ON eg_wms_ap_fund_detail_audit (id);
CREATE INDEX IF NOT EXISTS idx_eg_wms_ben_audit_id  ON eg_wms_ap_beneficiary_audit (id);
CREATE INDEX IF NOT EXISTS idx_eg_wms_doc_audit_id  ON eg_wms_ap_document_audit (id);
CREATE INDEX IF NOT EXISTS idx_eg_wms_rec_doc_audit_id  ON eg_wms_ap_recommendation_document_audit (id);
CREATE INDEX IF NOT EXISTS idx_eg_wms_comp_doc_audit_id ON eg_wms_ap_component_document_audit (id);
