-- Table: public.eg_ptr_applicationdocuments

-- DROP TABLE IF EXISTS public.eg_ptr_applicationdocuments;

CREATE TABLE IF NOT EXISTS public.eg_ptr_applicationdocuments
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    tenantid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    documenttype character varying(64) COLLATE pg_catalog."default" NOT NULL,
    filestoreid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    documentuid character varying(128) COLLATE pg_catalog."default",
    petapplicationid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    active boolean,
    createdby character varying(64) COLLATE pg_catalog."default" NOT NULL,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    CONSTRAINT uk_eg_ptr_applicationdocument PRIMARY KEY (id),
    CONSTRAINT fk_eg_ptr_applicationdocument FOREIGN KEY (petapplicationid)
        REFERENCES public.eg_ptr_registration (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION 
);
