ALTER TABLE eg_birth_details ADD COLUMN is_stillbirth boolean;
ALTER TABLE eg_birth_details_audit ADD COLUMN is_stillbirth boolean;
ALTER TABLE eg_birth_statitical_information ADD COLUMN cause_of_foetal_death character varying(64) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_statitical_information_audit ADD COLUMN cause_of_foetal_death character varying(64) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_place
            ADD COLUMN is_inform_declare boolean,
            DROP COLUMN informantsname_en,
            DROP COLUMN informantsname_ml;
            
ALTER TABLE eg_birth_place_audit
            ADD COLUMN is_inform_declare boolean;
            
ALTER TABLE eg_birth_place_audit
            DROP COLUMN informantsname_en,
            DROP COLUMN informantsname_ml;

ALTER TABLE eg_birth_initiator
            ADD COLUMN is_care_taker boolean,
            ADD COLUMN is_esigned boolean;
ALTER TABLE eg_birth_initiator_audit
            ADD COLUMN is_care_taker boolean,
            ADD COLUMN is_esigned boolean;
