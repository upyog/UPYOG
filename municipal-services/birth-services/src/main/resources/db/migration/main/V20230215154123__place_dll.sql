ALTER TABLE eg_birth_place RENAME ho_main_place_en TO ho_locality_en;
ALTER TABLE eg_birth_place RENAME ho_main_place_ml TO ho_locality_ml;

ALTER TABLE eg_birth_place RENAME ho_street_locality_area_en TO ho_street_name_en;
ALTER TABLE eg_birth_place RENAME ho_street_locality_area_ml TO ho_street_name_ml;

ALTER TABLE eg_birth_place_audit RENAME ho_main_place_en TO ho_locality_en;
ALTER TABLE eg_birth_place_audit RENAME ho_main_place_ml TO ho_locality_ml;

ALTER TABLE eg_birth_place_audit RENAME ho_street_locality_area_en TO ho_street_name_en;
ALTER TABLE eg_birth_place_audit RENAME ho_street_locality_area_ml TO ho_street_name_ml;


ALTER TABLE eg_register_birth_place RENAME ho_main_place_en TO ho_locality_en;
ALTER TABLE eg_register_birth_place RENAME ho_main_place_ml TO ho_locality_ml;

ALTER TABLE eg_register_birth_place RENAME ho_street_locality_area_en TO ho_street_name_en;
ALTER TABLE eg_register_birth_place RENAME ho_street_locality_area_ml TO ho_street_name_ml;

ALTER TABLE eg_register_birth_place_audit RENAME ho_main_place_en TO ho_locality_en;
ALTER TABLE eg_register_birth_place_audit RENAME ho_main_place_ml TO ho_locality_ml;

ALTER TABLE eg_register_birth_place_audit RENAME ho_street_locality_area_en TO ho_street_name_en;
ALTER TABLE eg_register_birth_place_audit RENAME ho_street_locality_area_ml TO ho_street_name_ml;


ALTER TABLE eg_birth_place RENAME vehicle_haltplace TO vehicle_haltplace_en;
ALTER TABLE eg_birth_place_audit RENAME vehicle_haltplace TO vehicle_haltplace_en;

ALTER TABLE eg_register_birth_place RENAME vehicle_haltplace TO vehicle_haltplace_en;
ALTER TABLE eg_register_birth_place_audit RENAME vehicle_haltplace TO vehicle_haltplace_en;

ALTER TABLE eg_birth_place
    ADD COLUMN vehicle_haltplace_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN vehicle_desc character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_place_desc character varying(2000) COLLATE pg_catalog."default";

ALTER TABLE eg_birth_place_audit
    ADD COLUMN vehicle_haltplace_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN vehicle_desc character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_place_desc character varying(2000) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_place
    ADD COLUMN vehicle_haltplace_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN vehicle_desc character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_place_desc character varying(2000) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_place_audit
    ADD COLUMN vehicle_haltplace_ml character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN vehicle_desc character varying(1000) COLLATE pg_catalog."default",
    ADD COLUMN public_place_desc character varying(2000) COLLATE pg_catalog."default";