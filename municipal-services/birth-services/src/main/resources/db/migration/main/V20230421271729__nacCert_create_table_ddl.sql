 
CREATE TABLE IF NOT EXISTS public.eg_nac_certificate_request
(
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
    certificateno character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT eg_nac_certificate_request_pkey PRIMARY KEY (id),
    CONSTRAINT eg_nac_certificate_request_fkey FOREIGN KEY (registrydetailsid)
        REFERENCES public.eg_birth_nac_registry (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);



CREATE INDEX IF NOT EXISTS idx_eg_nac_certificate_request_registrationno
    ON public.eg_nac_certificate_request USING btree
    (registrationno COLLATE pg_catalog."default" ASC NULLS LAST);


CREATE INDEX IF NOT EXISTS idx_eg_nac_certificate_request_tenantid
    ON public.eg_nac_certificate_request USING btree
    (tenantid COLLATE pg_catalog."default" ASC NULLS LAST);

