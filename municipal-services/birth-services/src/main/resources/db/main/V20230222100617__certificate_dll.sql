ALTER TABLE eg_birth_certificate_request
    ALTER COLUMN registrationno  TYPE character varying(64) COLLATE pg_catalog."default",
     ALTER COLUMN status TYPE character varying(64) COLLATE pg_catalog."default";

CREATE TABLE IF NOT EXISTS public.eg_birth_certificate_request_audit
(
    operation character(1) COLLATE pg_catalog."default" NOT NULL,
    stamp timestamp without time zone NOT NULL,
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    registrationno character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    registrydetailsid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    lastmodifiedtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    filestoreid character varying(256) COLLATE pg_catalog."default",
    status character varying(64) COLLATE pg_catalog."default",
    additionaldetail jsonb,
    embeddedurl character varying(64) COLLATE pg_catalog."default",
    dateofissue bigint,
    tenantid character varying(64) COLLATE pg_catalog."default"
);
-- FUNCTION: public.process_eg_birth_certificate_request_audit()

-- DROP FUNCTION IF EXISTS public.process_eg_birth_certificate_request_audit();

CREATE OR REPLACE FUNCTION public.process_eg_birth_certificate_request_audit()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO eg_birth_certificate_request_audit SELECT 'D', now(), OLD.*;
RETURN OLD;
ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO eg_birth_certificate_request_audit SELECT 'U', now(), OLD.*;
RETURN NEW;
END IF;
RETURN NULL;
END;
$BODY$;
-- Trigger: eg_birth_certificate_request_audit

-- DROP TRIGGER IF EXISTS eg_birth_certificate_request_audit ON public.eg_birth_certificate_request;

CREATE TRIGGER eg_birth_certificate_request_audit
    BEFORE DELETE OR UPDATE
                         ON public.eg_birth_certificate_request_audit
                         FOR EACH ROW
                         EXECUTE FUNCTION public.process_eg_birth_certificate_request_audit();