ALTER TABLE eg_birth_permanent_address
    ADD COLUMN family_emailid character varying(300) COLLATE pg_catalog."default",
    ADD COLUMN family_mobileno character varying(20) COLLATE pg_catalog."default";

ALTER TABLE eg_birth_permanent_address_audit
    ADD COLUMN family_emailid character varying(300) COLLATE pg_catalog."default",
    ADD COLUMN family_mobileno character varying(20) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_permanent_address
    ADD COLUMN family_emailid character varying(300) COLLATE pg_catalog."default",
    ADD COLUMN family_mobileno character varying(20) COLLATE pg_catalog."default";

ALTER TABLE eg_register_birth_permanent_address_audit
    ADD COLUMN family_emailid character varying(300) COLLATE pg_catalog."default",
    ADD COLUMN family_mobileno character varying(20) COLLATE pg_catalog."default";


ALTER TABLE eg_birth_permanent_address RENAME housename_en TO housename_no_en;
ALTER TABLE eg_birth_permanent_address RENAME housename_ml TO housename_no_ml;
ALTER TABLE eg_birth_permanent_address RENAME main_place_en TO locality_en;
ALTER TABLE eg_birth_permanent_address RENAME main_place_ml TO locality_ml;
ALTER TABLE eg_birth_permanent_address RENAME street_locality_area_en TO street_name_en;
ALTER TABLE eg_birth_permanent_address RENAME street_locality_area_ml TO street_name_ml;

ALTER TABLE eg_birth_permanent_address_audit RENAME housename_en TO housename_no_en;
ALTER TABLE eg_birth_permanent_address_audit RENAME housename_ml TO housename_no_ml;
ALTER TABLE eg_birth_permanent_address_audit RENAME main_place_en TO locality_en;
ALTER TABLE eg_birth_permanent_address_audit RENAME main_place_ml TO locality_ml;
ALTER TABLE eg_birth_permanent_address_audit RENAME street_locality_area_en TO street_name_en;
ALTER TABLE eg_birth_permanent_address_audit RENAME street_locality_area_ml TO street_name_ml;

ALTER TABLE eg_birth_present_address RENAME housename_en TO housename_no_en;
ALTER TABLE eg_birth_present_address RENAME housename_ml TO housename_no_ml;
ALTER TABLE eg_birth_present_address RENAME main_place_en TO locality_en;
ALTER TABLE eg_birth_present_address RENAME main_place_ml TO locality_ml;
ALTER TABLE eg_birth_present_address RENAME street_locality_area_en TO street_name_en;
ALTER TABLE eg_birth_present_address RENAME street_locality_area_ml TO street_name_ml;

ALTER TABLE eg_birth_present_address_audit RENAME housename_en TO housename_no_en;
ALTER TABLE eg_birth_present_address_audit RENAME housename_ml TO housename_no_ml;
ALTER TABLE eg_birth_present_address_audit RENAME main_place_en TO locality_en;
ALTER TABLE eg_birth_present_address_audit RENAME main_place_ml TO locality_ml;
ALTER TABLE eg_birth_present_address_audit RENAME street_locality_area_en TO street_name_en;
ALTER TABLE eg_birth_present_address_audit RENAME street_locality_area_ml TO street_name_ml;

ALTER TABLE eg_register_birth_permanent_address RENAME housename_en TO housename_no_en;
ALTER TABLE eg_register_birth_permanent_address RENAME housename_ml TO housename_no_ml;
ALTER TABLE eg_register_birth_permanent_address RENAME main_place_en TO locality_en;
ALTER TABLE eg_register_birth_permanent_address RENAME main_place_ml TO locality_ml;
ALTER TABLE eg_register_birth_permanent_address RENAME street_locality_area_en TO street_name_en;
ALTER TABLE eg_register_birth_permanent_address RENAME street_locality_area_ml TO street_name_ml;

ALTER TABLE eg_register_birth_permanent_address_audit RENAME housename_en TO housename_no_en;
ALTER TABLE eg_register_birth_permanent_address_audit RENAME housename_ml TO housename_no_ml;
ALTER TABLE eg_register_birth_permanent_address_audit RENAME main_place_en TO locality_en;
ALTER TABLE eg_register_birth_permanent_address_audit RENAME main_place_ml TO locality_ml;
ALTER TABLE eg_register_birth_permanent_address_audit RENAME street_locality_area_en TO street_name_en;
ALTER TABLE eg_register_birth_permanent_address_audit RENAME street_locality_area_ml TO street_name_ml;

ALTER TABLE eg_register_birth_present_address RENAME housename_en TO housename_no_en;
ALTER TABLE eg_register_birth_present_address RENAME housename_ml TO housename_no_ml;
ALTER TABLE eg_register_birth_present_address RENAME main_place_en TO locality_en;
ALTER TABLE eg_register_birth_present_address RENAME main_place_ml TO locality_ml;
ALTER TABLE eg_register_birth_present_address RENAME street_locality_area_en TO street_name_en;
ALTER TABLE eg_register_birth_present_address RENAME street_locality_area_ml TO street_name_ml;

ALTER TABLE eg_register_birth_present_address_audit RENAME housename_en TO housename_no_en;
ALTER TABLE eg_register_birth_present_address_audit RENAME housename_ml TO housename_no_ml;
ALTER TABLE eg_register_birth_present_address_audit RENAME main_place_en TO locality_en;
ALTER TABLE eg_register_birth_present_address_audit RENAME main_place_ml TO locality_ml;
ALTER TABLE eg_register_birth_present_address_audit RENAME street_locality_area_en TO street_name_en;
ALTER TABLE eg_register_birth_present_address_audit RENAME street_locality_area_ml TO street_name_ml;