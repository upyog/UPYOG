-- Table: public.eg_userrole_v1

-- DROP TABLE IF EXISTS public.eg_userrole_v1;

CREATE TABLE IF NOT EXISTS public.eg_userrole_v1
(
    role_code character varying(50) COLLATE pg_catalog."default",
    role_tenantid character varying(256) COLLATE pg_catalog."default",
    user_id bigint,
    user_tenantid character varying(256) COLLATE pg_catalog."default",
    lastmodifieddate timestamp without time zone,
    CONSTRAINT idx_eg_userrole_v1_unique UNIQUE (role_code, role_tenantid, user_id, user_tenantid)
)

TABLESPACE pg_default;
