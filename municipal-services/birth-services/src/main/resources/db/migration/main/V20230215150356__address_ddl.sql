ALTER TABLE eg_birth_present_address
    ADD COLUMN postoffice_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN postoffice_ml character varying(1000) COLLATE pg_catalog."default";

ALTER TABLE eg_birth_present_address_audit
    ADD COLUMN postoffice_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN postoffice_ml character varying(1000) COLLATE pg_catalog."default";

ALTER TABLE eg_birth_permanent_address
    ADD COLUMN postoffice_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN postoffice_ml character varying(1000) COLLATE pg_catalog."default";

ALTER TABLE eg_birth_permanent_address_audit
    ADD COLUMN postoffice_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN postoffice_ml character varying(1000) COLLATE pg_catalog."default";


ALTER TABLE eg_register_birth_present_address
    ADD COLUMN postoffice_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN postoffice_ml character varying(1000) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_present_address_audit
    ADD COLUMN postoffice_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN postoffice_ml character varying(1000) COLLATE pg_catalog."default";


ALTER TABLE eg_register_birth_permanent_address
    ADD COLUMN postoffice_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN postoffice_ml character varying(1000) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_permanent_address_audit
    ADD COLUMN postoffice_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN postoffice_ml character varying(1000) COLLATE pg_catalog."default";
