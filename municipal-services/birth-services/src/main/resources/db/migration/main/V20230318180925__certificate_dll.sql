ALTER TABLE eg_birth_certificate_request_audit
        ADD applicationid character varying(64),
        ADD ack_no character varying(64);

ALTER TABLE   eg_register_birth_details
        ADD applicationid character varying(64);

ALTER TABLE   eg_register_birth_details_audit
        ADD applicationid character varying(64);

        ALTER TABLE eg_birth_certificate_request
        ADD COLUMN certificateno character varying(64) COLLATE pg_catalog."default";

        ALTER TABLE eg_birth_certificate_request_audit
        ADD COLUMN certificateno character varying(64) COLLATE pg_catalog."default";


        ALTER TABLE   eg_register_birth_details
                ADD applicationtype character varying(64);

        ALTER TABLE   eg_register_birth_details_audit
                ADD applicationtype character varying(64);