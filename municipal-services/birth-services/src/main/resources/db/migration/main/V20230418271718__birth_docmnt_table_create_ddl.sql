CREATE TABLE IF NOT EXISTS public.eg_birth_application_document
(
    id character varying(128) COLLATE pg_catalog."default",
    tenantid character varying(128) COLLATE pg_catalog."default",
    document_name character varying(128) COLLATE pg_catalog."default" ,
    document_type character varying(128) COLLATE pg_catalog."default" NOT NULL,
    document_description character varying(140) COLLATE pg_catalog."default",
    filestoreid character varying(1024) COLLATE pg_catalog."default",
    document_link character varying(1024) COLLATE pg_catalog."default",
    file_type character varying(20) COLLATE pg_catalog."default",
    file_size bigint,
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    active boolean NOT NULL,
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,    
    CONSTRAINT eg_birth_application_document_pkey UNIQUE (id)
);

CREATE TABLE IF NOT EXISTS public.eg_birth_application_document_audit
(
    operation character(1) COLLATE pg_catalog."default" NOT NULL,
    stamp timestamp without time zone NOT NULL,
    id character varying(128) COLLATE pg_catalog."default",
    tenantid character varying(128) COLLATE pg_catalog."default",
    document_name character varying(128) COLLATE pg_catalog."default",
    document_type character varying(128) COLLATE pg_catalog."default",
    document_description character varying(140) COLLATE pg_catalog."default",
    filestoreid character varying(1024) COLLATE pg_catalog."default",
    document_link character varying(1024) COLLATE pg_catalog."default",
    file_type character varying(20) COLLATE pg_catalog."default",
    file_size bigint,
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    active boolean,
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint
);
