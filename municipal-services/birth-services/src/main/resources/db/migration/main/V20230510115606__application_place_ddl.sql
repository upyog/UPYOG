ALTER TABLE eg_birth_place
    ADD COLUMN IF NOT EXISTS  hosp_ip_op character varying(10) COLLATE pg_catalog."default",   
    ADD COLUMN IF NOT EXISTS  hosp_ip_op_number character varying(64) COLLATE pg_catalog."default",
    ADD COLUMN IF NOT EXISTS  obstetrics_gync_number character varying(128) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_place_audit
    ADD COLUMN IF NOT EXISTS  hosp_ip_op character varying(10) COLLATE pg_catalog."default",
    ADD COLUMN IF NOT EXISTS  hosp_ip_op_number character varying(64) COLLATE pg_catalog."default",
    ADD COLUMN IF NOT EXISTS  obstetrics_gync_number character varying(128) COLLATE pg_catalog."default";
