ALTER TABLE eg_birth_place
    ADD COLUMN public_locality_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_locality_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_street_name_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_street_name_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN ot_birth_place_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_birth_place_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address1_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address1_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address2_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address2_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_state_region_province_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_state_region_province_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_zipcode character varying(10) COLLATE pg_catalog."default";

ALTER TABLE eg_birth_place_audit
    ADD COLUMN public_locality_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_locality_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_street_name_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_street_name_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN ot_birth_place_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_birth_place_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address1_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address1_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address2_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address2_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_state_region_province_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_state_region_province_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_zipcode character varying(10) COLLATE pg_catalog."default";


ALTER TABLE eg_register_birth_place
    ADD COLUMN public_locality_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_locality_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_street_name_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_street_name_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN ot_birth_place_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_birth_place_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address1_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address1_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address2_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address2_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_state_region_province_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_state_region_province_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_zipcode character varying(10) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_place_audit
    ADD COLUMN public_locality_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_locality_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_street_name_en character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_street_name_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN ot_birth_place_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_birth_place_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address1_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address1_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address2_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_address2_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_state_region_province_en character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_state_region_province_ml character varying(2500) COLLATE pg_catalog."default",
    ADD COLUMN ot_zipcode character varying(10) COLLATE pg_catalog."default";