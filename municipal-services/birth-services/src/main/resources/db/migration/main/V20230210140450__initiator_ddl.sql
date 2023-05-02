CREATE TABLE IF NOT EXISTS public.eg_birth_initiator
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    initiator_name character varying(1000) COLLATE pg_catalog."default",
    initiator_institution character varying(64) COLLATE pg_catalog."default",
    initiator_inst_desig character varying(64) COLLATE pg_catalog."default",
    relation character varying(64) COLLATE pg_catalog."default",
    initiator_address character varying(2000) COLLATE pg_catalog."default",
    is_declared boolean,
    declaration_id character varying(64) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    mobileno character varying(12) COLLATE pg_catalog."default",
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    CONSTRAINT eg_birth_initiator_pkey PRIMARY KEY (id),
    CONSTRAINT eg_birth_initiator_fkey FOREIGN KEY (birthdtlid)
    REFERENCES public.eg_birth_details (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    );

ALTER TABLE IF EXISTS public.eg_birth_initiator_information
    OWNER to postgres;
-- Index: idx_eg_birth_initiator_birthdtlid

-- DROP INDEX IF EXISTS public.idx_eg_birth_initiator_birthdtlid;

CREATE INDEX IF NOT EXISTS idx_eg_birth_initiator_birthdtlid
    ON public.eg_birth_initiator USING btree
    (birthdtlid COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;


CREATE TABLE IF NOT EXISTS public.eg_birth_initiator_audit
(
    operation character(1) COLLATE pg_catalog."default" NOT NULL,
    stamp timestamp without time zone NOT NULL,
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    initiator_name character varying(1000) COLLATE pg_catalog."default",
    initiator_institution character varying(64) COLLATE pg_catalog."default",
    initiator_inst_desig character varying(64) COLLATE pg_catalog."default",
    relation character varying(64) COLLATE pg_catalog."default",
    initiator_address character varying(2000) COLLATE pg_catalog."default",
    is_declared boolean,
    declaration_id character varying(64) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    mobileno character varying(12) COLLATE pg_catalog."default",
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint
    );

-- FUNCTION: public.process_eg_birth_initiator_audit()

-- DROP FUNCTION IF EXISTS public.process_eg_birth_initiator_audit();

CREATE OR REPLACE FUNCTION public.process_eg_birth_initiator_audit()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO eg_birth_initiator_audit SELECT 'D', now(), OLD.*;
RETURN OLD;
ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO eg_birth_initiator_audit SELECT 'U', now(), OLD.*;
RETURN NEW;
END IF;
RETURN NULL;
END;
$BODY$;

-- Trigger: eg_birth_initiator_audit

-- DROP TRIGGER IF EXISTS eg_birth_initiator_audit ON public.eg_birth_initiator;

CREATE TRIGGER eg_birth_initiator_audit
    BEFORE DELETE OR UPDATE ON public.eg_birth_initiator
                         FOR EACH ROW EXECUTE FUNCTION public.process_eg_birth_initiator_audit();






