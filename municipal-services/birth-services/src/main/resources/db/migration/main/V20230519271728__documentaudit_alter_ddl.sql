ALTER TABLE eg_birth_application_document_audit DROP COLUMN IF EXISTS operation,DROP COLUMN IF EXISTS stamp;

ALTER TABLE eg_birth_certificate_request ADD COLUMN IF NOT EXISTS  applicationid character varying(64) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_certificate_request_audit ADD COLUMN IF NOT EXISTS  applicationid character varying(64) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_certificate_request ADD COLUMN IF NOT EXISTS  ack_no character varying(128) COLLATE pg_catalog."default";
ALTER TABLE eg_birth_certificate_request_audit ADD COLUMN IF NOT EXISTS  ack_no character varying(128) COLLATE pg_catalog."default";
