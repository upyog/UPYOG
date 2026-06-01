-- ============================================================
-- GC Calculator - Consolidated Schema Migration
-- Merges: V202510110600__create_gc.sql
--         V202534112888__alter_scheduler_edit.sql
-- All ALTER TABLE changes have been folded into the initial
-- CREATE TABLE definitions to produce a clean, idempotent DDL.
-- ============================================================

-- ------------------------------------------------------------
-- Table: eg_gc_bulkbill_audit
-- Tracks bulk-bill generation audit records per tenant.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eg_gc_bulkbill_audit (
    id               CHARACTER VARYING(128)  NOT NULL,
    tenantid         CHARACTER VARYING(256)  NOT NULL,
    businessservice  CHARACTER VARYING(256)  NOT NULL,
    batchoffset      BIGINT                  NOT NULL,
    recordCount      BIGINT                  NOT NULL,
    createdtime      BIGINT                  NOT NULL,
    audittime        BIGINT                  NOT NULL,
    message          CHARACTER VARYING(2048) NOT NULL,
    CONSTRAINT pk_eg_gc_bulkbill_audit_id PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- Table: eg_gc_scheduler
-- Stores scheduler run metadata per tenant / locality / group.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eg_gc_scheduler (
    id                    CHARACTER VARYING(64) NOT NULL,
    tenantid              CHARACTER VARYING(64),
    transactiontype       CHARACTER VARYING(64),
    locality              CHARACTER VARYING(64),
    groups                TEXT,
    status                CHARACTER VARYING(64) NOT NULL,
    billingcyclestartdate BIGINT                NOT NULL,
    billingcycleenddate   BIGINT                NOT NULL,
    createdby             CHARACTER VARYING(64),
    lastmodifiedby        CHARACTER VARYING(64),
    createdtime           BIGINT,
    lastmodifiedtime      BIGINT
);

CREATE INDEX IF NOT EXISTS index_eg_gc_scheduler_tenantid  ON eg_gc_scheduler (tenantid);
CREATE INDEX IF NOT EXISTS index_eg_gc_scheduler_locality  ON eg_gc_scheduler (locality);

-- ------------------------------------------------------------
-- Table: eg_gc_bill_scheduler_connection_status
-- Tracks per-connection billing status within a scheduler run.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eg_gc_bill_scheduler_connection_status (
    id                  CHARACTER VARYING(64)  NOT NULL,
    tenantid            CHARACTER VARYING(64),
    eg_gc_scheduler_id  CHARACTER VARYING(64),
    consumercode        CHARACTER VARYING(64),
    locality            CHARACTER VARYING(64),
    module              CHARACTER VARYING(64),
    status              CHARACTER VARYING(64),
    reason              CHARACTER VARYING(1000),
    createdtime         BIGINT,
    lastupdatedtime     BIGINT,
    CONSTRAINT pk_eg_gc_bill_scheduler_connection_status_id PRIMARY KEY (id)
);