ALTER TABLE eg_birth_certificate_request
ADD COLUMN certificateno character varying(64) COLLATE pg_catalog."default";

ALTER TABLE eg_birth_certificate_request_audit
ADD COLUMN certificateno character varying(64) COLLATE pg_catalog."default";