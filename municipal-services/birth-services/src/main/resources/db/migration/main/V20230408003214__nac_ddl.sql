alter table eg_birth_details_audit
    add column  nac_order_of_child integer,
    add column  application_sub_type character varying(1000) COLLATE pg_catalog."default";
alter table eg_birth_details
    add column  application_sub_type character varying(1000) COLLATE pg_catalog."default";