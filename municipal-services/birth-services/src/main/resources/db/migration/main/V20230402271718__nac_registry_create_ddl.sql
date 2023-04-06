CREATE TABLE IF NOT EXISTS public.eg_birth_nac_registry
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    registrationno character varying(64) COLLATE pg_catalog."default",
	birthdetailsid character varying(64) COLLATE pg_catalog."default" NOT NULL,
	applicant_name_en character varying(1000) COLLATE pg_catalog."default",	
	care_of_applicant_name_en character varying(1000) COLLATE pg_catalog."default",
	child_name_en character varying(200) COLLATE pg_catalog."default",
	dateofbirth bigint,
	mother_name_en character varying(200) COLLATE pg_catalog."default",
	birth_place_en character varying(200) COLLATE pg_catalog."default",
	birth_districtid character varying(64) COLLATE pg_catalog."default",
    birth_stateid character varying(64) COLLATE pg_catalog."default",
	birth_villageid character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,    
    lastmodifiedtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
	applicationtype character varying(64) COLLATE pg_catalog."default" NOT NULL,
    filestoreid character varying(256) COLLATE pg_catalog."default",
    status character varying(64) COLLATE pg_catalog."default",
    additionaldetail jsonb,
    embeddedurl character varying(64) COLLATE pg_catalog."default",
    dateofissue bigint,
    tenantid character varying(64) COLLATE pg_catalog."default",
    certificateno character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT eg_birth_nac_registry_pkey PRIMARY KEY (id),
    CONSTRAINT eg_birth_nac_registry_fkey FOREIGN KEY (birthdetailsid)
        REFERENCES public.eg_birth_details (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
)

