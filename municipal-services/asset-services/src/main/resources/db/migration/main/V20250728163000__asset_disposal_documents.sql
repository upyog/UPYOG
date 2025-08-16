-- Table: public.eg_asset_disposal_documents

-- DROP TABLE IF EXISTS public.eg_asset_disposal_documents;

CREATE TABLE IF NOT EXISTS public.eg_asset_disposal_documents
(
    documentid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    documenttype character varying(64) COLLATE pg_catalog."default",
    filestoreid character varying(64) COLLATE pg_catalog."default",
    documentuid character varying(64) COLLATE pg_catalog."default",
    docdetails jsonb,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    disposalid character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT uk_eg_asset_disposal_documents PRIMARY KEY (documentid)
    );
CREATE TABLE IF NOT EXISTS public.eg_asset_maintenance_documents
(
    documentid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    documenttype character varying(64) COLLATE pg_catalog."default",
    filestoreid character varying(64) COLLATE pg_catalog."default",
    documentuid character varying(64) COLLATE pg_catalog."default",
    docdetails jsonb,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    maintenanceid character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT uk_eg_asset_maintenance_documents PRIMARY KEY (documentid)
    );