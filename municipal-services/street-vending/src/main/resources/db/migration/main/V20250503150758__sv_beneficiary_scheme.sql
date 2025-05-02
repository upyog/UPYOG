ALTER TABLE public.eg_sv_street_vending_detail
DROP COLUMN beneficiary_of_social_schemes;

ALTER TABLE public.eg_sv_street_vending_detail_auditdetails
DROP COLUMN beneficiary_of_social_schemes;

CREATE TABLE eg_sv_beneficiary_schemes_detail
(
    id character varying(64)  NOT NULL,
    application_id character varying(64)  NOT NULL,
    scheme_name character varying(100)  NOT NULL,
    enrollment_id character varying(64)  NOT NULL,
   
    CONSTRAINT eg_sv_beneficiary_scheme_detail PRIMARY KEY (id),
    CONSTRAINT eg_sv_beneficiary_scheme_detail_application_id_fk FOREIGN KEY (application_id)
        REFERENCES eg_sv_street_vending_detail (application_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);