alter table eg_register_birth_permanent_address
    add column  country_name_en character varying(1000) COLLATE pg_catalog."default",
    add column  country_name_ml character varying(1000) COLLATE pg_catalog."default";

alter table eg_register_birth_permanent_address_audit
    add column  country_name_en character varying(1000) COLLATE pg_catalog."default",
    add column  country_name_ml character varying(1000) COLLATE pg_catalog."default";

alter table eg_register_birth_present_address
    add column  country_name_en character varying(1000) COLLATE pg_catalog."default",
    add column  country_name_ml character varying(1000) COLLATE pg_catalog."default";

alter table eg_register_birth_present_address_audit
    add column  country_name_en character varying(1000) COLLATE pg_catalog."default",
    add column  country_name_ml character varying(1000) COLLATE pg_catalog."default";