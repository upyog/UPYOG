ALTER TABLE eg_register_birth_details
    ADD COLUMN migrated_from character varying(20) COLLATE pg_catalog."default";