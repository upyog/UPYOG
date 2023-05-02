 alter table eg_nac_certificate_request     
 add column   applicationid character varying(64) ,
 add column   ack_no character varying(64);
 
 
  
    CREATE TABLE IF NOT EXISTS public.eg_nac_certificate_request_audit
(
    operation character(1) COLLATE pg_catalog."default" NOT NULL,
    stamp timestamp without time zone NOT NULL,
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    registrationno character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    registrydetailsid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    lastmodifiedtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    filestoreid character varying(256) COLLATE pg_catalog."default",
    status character varying(64) COLLATE pg_catalog."default",
    additionaldetail jsonb,
    embeddedurl character varying(64) COLLATE pg_catalog."default",
    dateofissue bigint,
    tenantid character varying(64) COLLATE pg_catalog."default",
    applicationid character varying(64) COLLATE pg_catalog."default",
    ack_no character varying(64) COLLATE pg_catalog."default",
    certificateno character varying(64) COLLATE pg_catalog."default"
);


