CREATE TABLE IF NOT EXISTS public.eg_bpa_preapprovedplan
(
    id character varying(256) COLLATE pg_catalog."default" NOT NULL,
    drawing_number character varying(64) COLLATE pg_catalog."default",
    tenantid character varying(256) COLLATE pg_catalog."default",
    plot_length numeric,
    plot_width numeric,
    road_width numeric,
    drawing_detail jsonb,
    active boolean,
    additional_details jsonb,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    plot_length_in_feet numeric,
    plot_width_in_feet numeric,
    preapproved_code character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT pk_eg_bpa_preapprovedplan PRIMARY KEY (id)
);
 
CREATE INDEX IF NOT EXISTS bpa_preapprovedplan_index
    ON public.eg_bpa_preapprovedplan USING btree
    (drawing_number COLLATE pg_catalog."default" ASC NULLS LAST, active ASC NULLS LAST, tenantid COLLATE pg_catalog."default" ASC NULLS LAST, id COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;
 
 
CREATE TABLE IF NOT EXISTS public.eg_bpa_preapprovedplan_documents
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    documenttype character varying(64) COLLATE pg_catalog."default",
    filestoreid character varying(64) COLLATE pg_catalog."default",
    documentuid character varying(64) COLLATE pg_catalog."default",
    preapprovedplanid character varying(64) COLLATE pg_catalog."default",
    additionaldetails jsonb,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    CONSTRAINT pk_eg_bpa_preapprovedplan_documents PRIMARY KEY (id),
    CONSTRAINT fk_eg_bpa_preapprovedplan_documents FOREIGN KEY (preapprovedplanid)
        REFERENCES public.eg_bpa_preapprovedplan (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);