alter table public.eg_birth_place
    ADD COLUMN  relation character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN  informantsname_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN  informants_office_name character varying(2500) COLLATE pg_catalog."default";

alter table public.eg_birth_place_audit
    ADD COLUMN  relation character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN  informantsname_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN  informants_office_name character varying(2500) COLLATE pg_catalog."default";


CREATE TABLE eg_birth_abandoned_care_taker(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    care_taker_name character varying(1000) COLLATE pg_catalog."default",
    care_taker_institution character varying(64) COLLATE pg_catalog."default",
    care_taker_inst_designation character varying(64) COLLATE pg_catalog."default",
    care_taker_address character varying(2000) COLLATE pg_catalog."default",
    care_taker_mobileno character varying(12) COLLATE pg_catalog."default",
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint);