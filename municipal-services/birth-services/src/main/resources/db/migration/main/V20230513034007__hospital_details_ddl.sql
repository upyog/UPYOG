ALTER TABLE eg_birth_details ADD COLUMN IF NOT EXISTS  assignee character varying(128) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_details_audit ADD COLUMN IF NOT EXISTS  assignee character varying(128) COLLATE pg_catalog."default";

ALTER TABLE eg_birth_details ADD COLUMN IF NOT EXISTS  rdo_proceedings_no character varying(250) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_details_audit ADD COLUMN IF NOT EXISTS  rdo_proceedings_no character varying(250) COLLATE pg_catalog."default";

ALTER TABLE eg_birth_details ADD COLUMN IF NOT EXISTS  nac_registration_no character varying(250) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_details_audit ADD COLUMN IF NOT EXISTS  nac_registration_no character varying(250) COLLATE pg_catalog."default";

ALTER TABLE eg_birth_initiator ADD COLUMN IF NOT EXISTS  initiator character varying(128) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_initiator_audit ADD COLUMN IF NOT EXISTS  initiator character varying(128) COLLATE pg_catalog."default";

ALTER TABLE eg_birth_initiator ADD COLUMN IF NOT EXISTS  isGuardian boolean;
ALTER TABLE eg_birth_initiator_audit ADD COLUMN IF NOT EXISTS  isGuardian boolean;