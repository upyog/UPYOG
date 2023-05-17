ALTER TABLE eg_birth_certificate_request ADD COLUMN IF NOT EXISTS  applicationid character varying(64) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_certificate_request_audit ADD COLUMN IF NOT EXISTS  applicationid character varying(64) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_certificate_request ADD COLUMN IF NOT EXISTS  ack_no character varying(128) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_certificate_request_audit ADD COLUMN IF NOT EXISTS  ack_no character varying(128) COLLATE pg_catalog."default";
