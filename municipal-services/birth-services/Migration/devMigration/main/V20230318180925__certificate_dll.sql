ALTER TABLE eg_birth_certificate_request
        ADD registrydetailsid character varying(64),
        ADD ack_no character varying(64);

ALTER TABLE eg_birth_certificate_request_audit
        ADD registrydetailsid character varying(64),
        ADD ack_no character varying(64);

ALTER TABLE   eg_register_birth_details
        ADD applicationid character varying(64);

ALTER TABLE   eg_register_birth_details_audit
        ADD applicationid character varying(64);