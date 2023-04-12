ALTER TABLE public.eg_register_birth_details
DROP CONSTRAINT IF EXISTS  eg_register_birth_details_registrationno_key;

ALTER TABLE public.eg_register_birth_details
ADD COLUMN ack_no character varying(64) COLLATE pg_catalog."default",
ADD COLUMN is_migrated boolean,
ADD COLUMN migrated_date bigint;

ALTER TABLE public.eg_register_birth_details
    ADD CONSTRAINT eg_register_birth_details_ackno_tenantid_key UNIQUE(ack_no,tenantid);

ALTER TABLE public.eg_register_birth_details_audit
ADD COLUMN ack_no character varying(64) COLLATE pg_catalog."default",
ADD COLUMN is_migrated boolean,
ADD COLUMN migrated_date bigint;


ALTER TABLE  eg_register_birth_place
ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_place_audit
    ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_father_information
ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_father_information_audit
ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_mother_information
ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_mother_information_audit
ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_permanent_address
ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_permanent_address_audit
ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_present_address
ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_present_address_audit
    ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_statitical_information
ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_statitical_information_audit
ADD COLUMN mig_chvackno character varying(64) COLLATE pg_catalog."default";


