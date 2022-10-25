CREATE TABLE eg_tl_structureplacedetail
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    tenantid character varying(64) COLLATE pg_catalog."default",
    structureplacesubtype character varying(64) COLLATE pg_catalog."default",
    blockno character varying(64) COLLATE pg_catalog."default",
    surveyno character varying(64) COLLATE pg_catalog."default",
    subdivisionno character varying(64) COLLATE pg_catalog."default",
    zonalcode character varying(64) COLLATE pg_catalog."default",
    wardcode character varying(64) COLLATE pg_catalog."default",
    wardno integer,
    doorno integer,
    doorsub character varying(64) COLLATE pg_catalog."default",
    buildingid numeric,
    vehicleno character varying(64) COLLATE pg_catalog."default",
    vesselno character varying(64) COLLATE pg_catalog."default",
    tradelicensedetailid character varying(64) COLLATE pg_catalog."default",
    active boolean,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    CONSTRAINT pk_eg_tl_structureplacedetail PRIMARY KEY (id),
    CONSTRAINT fk_eg_tl_structureplacedetail FOREIGN KEY (tradelicensedetailid)
        REFERENCES public.eg_tl_tradelicensedetail (id) 
        ON UPDATE CASCADE
        ON DELETE CASCADE
);