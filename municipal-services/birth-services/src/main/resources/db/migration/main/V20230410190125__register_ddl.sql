alter table eg_register_birth_details
    add column  application_sub_type character varying(1000) COLLATE pg_catalog."default";
alter table eg_register_birth_details_audit
    add column  application_sub_type character varying(1000) COLLATE pg_catalog."default";