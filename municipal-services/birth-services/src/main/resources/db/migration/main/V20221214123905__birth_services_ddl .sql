
-- Table: public.eg_birth_adoption_father_information

-- DROP TABLE IF EXISTS public.eg_birth_adoption_father_information;

CREATE TABLE IF NOT EXISTS public.eg_birth_adoption_father_information
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    firstname_en character varying(1000) COLLATE pg_catalog."default",
    firstname_ml character varying(1000) COLLATE pg_catalog."default",
    middlename_en character varying(1000) COLLATE pg_catalog."default",
    middlename_ml character varying(1000) COLLATE pg_catalog."default",
    lastname_en character varying(1000) COLLATE pg_catalog."default",
    lastname_ml character varying(1000) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    ot_passportno character varying(100) COLLATE pg_catalog."default",
    emailid character varying(300) COLLATE pg_catalog."default",
    mobileno character varying(12) COLLATE pg_catalog."default",
    createdtime bigint,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT eg_birth_adoption_father_information_pkey PRIMARY KEY (id),
    CONSTRAINT eg_birth_adoption_father_information_fkey FOREIGN KEY (birthdtlid)
    REFERENCES public.eg_birth_details (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    );
-- Index: idx_eg_birth_adoption_father_information_birthdtlid

-- DROP INDEX IF EXISTS public.idx_eg_birth_adoption_father_information_birthdtlid;

CREATE INDEX IF NOT EXISTS idx_eg_birth_adoption_father_information_birthdtlid
    ON public.eg_birth_adoption_father_information USING btree
    (birthdtlid COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- Table: public.eg_birth_adoption_father_information_audit

-- DROP TABLE IF EXISTS public.eg_birth_adoption_father_information_audit;

CREATE TABLE IF NOT EXISTS public.eg_birth_adoption_father_information_audit
(
    operation character(1) COLLATE pg_catalog."default" NOT NULL,
    stamp timestamp without time zone NOT NULL,
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    firstname_en character varying(1000) COLLATE pg_catalog."default",
    firstname_ml character varying(1000) COLLATE pg_catalog."default",
    middlename_en character varying(1000) COLLATE pg_catalog."default",
    middlename_ml character varying(1000) COLLATE pg_catalog."default",
    lastname_en character varying(1000) COLLATE pg_catalog."default",
    lastname_ml character varying(1000) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    ot_passportno character varying(100) COLLATE pg_catalog."default",
    emailid character varying(300) COLLATE pg_catalog."default",
    mobileno character varying(12) COLLATE pg_catalog."default",
    createdtime bigint,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL
    );

-- FUNCTION: public.process_eg_birth_adoption_father_information_audit()

-- DROP FUNCTION IF EXISTS public.process_eg_birth_adoption_father_information_audit();

CREATE OR REPLACE FUNCTION public.process_eg_birth_adoption_father_information_audit()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO eg_birth_adoption_father_information_audit SELECT 'D', now(), OLD.*;
RETURN OLD;
ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO eg_birth_adoption_father_information_audit SELECT 'U', now(), OLD.*;
RETURN NEW;
END IF;
RETURN NULL;
END;
$BODY$;

-- Trigger: eg_birth_adoption_father_information_audit

-- DROP TRIGGER IF EXISTS eg_birth_adoption_father_information_audit ON public.eg_birth_adoption_father_information;

CREATE TRIGGER eg_birth_adoption_father_information_audit
    BEFORE DELETE OR UPDATE ON public.eg_birth_adoption_father_information
                         FOR EACH ROW EXECUTE FUNCTION public.process_eg_birth_adoption_father_information_audit();


-- Table: public.eg_birth_adoption_mother_information

-- DROP TABLE IF EXISTS public.eg_birth_adoption_mother_information;

CREATE TABLE IF NOT EXISTS public.eg_birth_adoption_mother_information
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    firstname_en character varying(1000) COLLATE pg_catalog."default",
    firstname_ml character varying(1000) COLLATE pg_catalog."default",
    middlename_en character varying(1000) COLLATE pg_catalog."default",
    middlename_ml character varying(1000) COLLATE pg_catalog."default",
    lastname_en character varying(1000) COLLATE pg_catalog."default",
    lastname_ml character varying(1000) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    ot_passportno character varying(100) COLLATE pg_catalog."default",
    emailid character varying(300) COLLATE pg_catalog."default",
    mobileno character varying(12) COLLATE pg_catalog."default",
    createdtime bigint,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT eg_birth_adoption_mother_information_pkey PRIMARY KEY (id),
    CONSTRAINT eg_birth_adoption_mother_information_fkey FOREIGN KEY (birthdtlid)
    REFERENCES public.eg_birth_details (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    );

-- Index: idx_eg_birth_adoption_mother_information_birthdtlid

-- DROP INDEX IF EXISTS public.idx_eg_birth_adoption_mother_information_birthdtlid;

CREATE INDEX IF NOT EXISTS idx_eg_birth_adoption_mother_information_birthdtlid
    ON public.eg_birth_adoption_mother_information USING btree
    (birthdtlid COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;


-- Table: public.eg_birth_adoption_mother_information_audit

-- DROP TABLE IF EXISTS public.eg_birth_adoption_mother_information_audit;

CREATE TABLE IF NOT EXISTS public.eg_birth_adoption_mother_information_audit
(
    operation character(1) COLLATE pg_catalog."default" NOT NULL,
    stamp timestamp without time zone NOT NULL,
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    firstname_en character varying(1000) COLLATE pg_catalog."default",
    firstname_ml character varying(1000) COLLATE pg_catalog."default",
    middlename_en character varying(1000) COLLATE pg_catalog."default",
    middlename_ml character varying(1000) COLLATE pg_catalog."default",
    lastname_en character varying(1000) COLLATE pg_catalog."default",
    lastname_ml character varying(1000) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    ot_passportno character varying(100) COLLATE pg_catalog."default",
    emailid character varying(300) COLLATE pg_catalog."default",
    mobileno character varying(12) COLLATE pg_catalog."default",
    createdtime bigint,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL
    );
-- FUNCTION: public.process_eg_birth_adoption_mother_information_audit()

-- DROP FUNCTION IF EXISTS public.process_eg_birth_adoption_mother_information_audit();

CREATE OR REPLACE FUNCTION public.process_eg_birth_adoption_mother_information_audit()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO eg_birth_adoption_mother_information_audit SELECT 'D', now(), OLD.*;
RETURN OLD;
ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO eg_birth_adoption_mother_information_audit SELECT 'U', now(), OLD.*;
RETURN NEW;
END IF;
RETURN NULL;
END;
$BODY$;

-- Trigger: eg_birth_adoption_mother_information_audit

-- DROP TRIGGER IF EXISTS eg_birth_adoption_mother_information_audit ON public.eg_birth_adoption_mother_information;

CREATE TRIGGER eg_birth_adoption_mother_information_audit
    BEFORE DELETE OR UPDATE ON public.eg_birth_adoption_mother_information
                         FOR EACH ROW EXECUTE FUNCTION public.process_eg_birth_adoption_mother_information_audit();

-- Table: public.eg_birth_adoption_permanent_address

-- DROP TABLE IF EXISTS public.eg_birth_adoption_permanent_address;

CREATE TABLE IF NOT EXISTS public.eg_birth_adoption_permanent_address
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    resdnce_addr_type character varying(64) COLLATE pg_catalog."default",
    buildingno character varying(200) COLLATE pg_catalog."default",
    houseno character varying(200) COLLATE pg_catalog."default",
    res_asso_no character varying(250) COLLATE pg_catalog."default",
    housename_en character varying(2500) COLLATE pg_catalog."default",
    housename_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address1_en character varying(2500) COLLATE pg_catalog."default",
    ot_address1_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address2_en character varying(2500) COLLATE pg_catalog."default",
    ot_address2_ml character varying(2500) COLLATE pg_catalog."default",
    locality_en character varying(2500) COLLATE pg_catalog."default",
    locality_ml character varying(2500) COLLATE pg_catalog."default",
    city_en character varying(2500) COLLATE pg_catalog."default",
    city_ml character varying(2500) COLLATE pg_catalog."default",
    villageid character varying(64) COLLATE pg_catalog."default",
    tenantid character varying(64) COLLATE pg_catalog."default",
    talukid character varying(64) COLLATE pg_catalog."default",
    districtid character varying(64) COLLATE pg_catalog."default",
    stateid character varying(64) COLLATE pg_catalog."default",
    poid character varying(64) COLLATE pg_catalog."default",
    pinno character varying(10) COLLATE pg_catalog."default",
    ot_state_region_province_en character varying(2500) COLLATE pg_catalog."default",
    ot_state_region_province_ml character varying(2500) COLLATE pg_catalog."default",
    countryid character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    birthdtlid character varying(64) COLLATE pg_catalog."default",
    same_as_permanent integer,
    CONSTRAINT eg_birth_adoption_permanent_address_pkey PRIMARY KEY (id),
    CONSTRAINT eg_birth_adoption_permanent_address_fkey FOREIGN KEY (birthdtlid)
    REFERENCES public.eg_birth_details (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    );

-- Index: idx_eg_birth_adoption_permanent_address_info_birthdtlid

-- DROP INDEX IF EXISTS public.idx_eg_birth_adoption_permanent_address_info_birthdtlid;

CREATE INDEX IF NOT EXISTS idx_eg_birth_adoption_permanent_address_info_birthdtlid
    ON public.eg_birth_adoption_permanent_address USING btree
    (birthdtlid COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- Table: public.eg_birth_adoption_permanent_address_audit

-- DROP TABLE IF EXISTS public.eg_birth_adoption_permanent_address_audit;

CREATE TABLE IF NOT EXISTS public.eg_birth_adoption_permanent_address_audit
(
    operation character(1) COLLATE pg_catalog."default" NOT NULL,
    stamp timestamp without time zone NOT NULL,
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    resdnce_addr_type character varying(64) COLLATE pg_catalog."default",
    buildingno character varying(200) COLLATE pg_catalog."default",
    houseno character varying(200) COLLATE pg_catalog."default",
    res_asso_no character varying(250) COLLATE pg_catalog."default",
    housename_en character varying(2500) COLLATE pg_catalog."default",
    housename_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address1_en character varying(2500) COLLATE pg_catalog."default",
    ot_address1_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address2_en character varying(2500) COLLATE pg_catalog."default",
    ot_address2_ml character varying(2500) COLLATE pg_catalog."default",
    locality_en character varying(2500) COLLATE pg_catalog."default",
    locality_ml character varying(2500) COLLATE pg_catalog."default",
    city_en character varying(2500) COLLATE pg_catalog."default",
    city_ml character varying(2500) COLLATE pg_catalog."default",
    villageid character varying(64) COLLATE pg_catalog."default",
    tenantid character varying(64) COLLATE pg_catalog."default",
    talukid character varying(64) COLLATE pg_catalog."default",
    districtid character varying(64) COLLATE pg_catalog."default",
    stateid character varying(64) COLLATE pg_catalog."default",
    poid character varying(64) COLLATE pg_catalog."default",
    pinno character varying(10) COLLATE pg_catalog."default",
    ot_state_region_province_en character varying(2500) COLLATE pg_catalog."default",
    ot_state_region_province_ml character varying(2500) COLLATE pg_catalog."default",
    countryid character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    birthdtlid character varying(64) COLLATE pg_catalog."default",
    same_as_permanent integer
    );

-- FUNCTION: public.process_eg_birth_adoption_permanent_address_audit()

-- DROP FUNCTION IF EXISTS public.process_eg_birth_adoption_permanent_address_audit();

CREATE OR REPLACE FUNCTION public.process_eg_birth_adoption_permanent_address_audit()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO eg_birth_adoption_permanent_address_audit SELECT 'D', now(), OLD.*;
RETURN OLD;
ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO eg_birth_adoption_permanent_address_audit SELECT 'U', now(), OLD.*;
RETURN NEW;
END IF;
RETURN NULL;
END;
$BODY$;

-- Trigger: eg_birth_adoption_permanent_address_audit

-- DROP TRIGGER IF EXISTS eg_birth_adoption_permanent_address_audit ON public.eg_birth_adoption_permanent_address;

CREATE TRIGGER eg_birth_adoption_permanent_address_audit
    BEFORE DELETE OR UPDATE ON public.eg_birth_adoption_permanent_address
                         FOR EACH ROW EXECUTE FUNCTION public.process_eg_birth_adoption_permanent_address_audit();

-- Table: public.eg_birth_adoption_present_address

-- DROP TABLE IF EXISTS public.eg_birth_adoption_present_address;

CREATE TABLE IF NOT EXISTS public.eg_birth_adoption_present_address
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    resdnce_addr_type character varying(64) COLLATE pg_catalog."default",
    buildingno character varying(200) COLLATE pg_catalog."default",
    houseno character varying(200) COLLATE pg_catalog."default",
    res_asso_no character varying(250) COLLATE pg_catalog."default",
    housename_en character varying(2500) COLLATE pg_catalog."default",
    housename_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address1_en character varying(2500) COLLATE pg_catalog."default",
    ot_address1_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address2_en character varying(2500) COLLATE pg_catalog."default",
    ot_address2_ml character varying(2500) COLLATE pg_catalog."default",
    locality_en character varying(2500) COLLATE pg_catalog."default",
    locality_ml character varying(2500) COLLATE pg_catalog."default",
    city_en character varying(2500) COLLATE pg_catalog."default",
    city_ml character varying(2500) COLLATE pg_catalog."default",
    villageid character varying(64) COLLATE pg_catalog."default",
    tenantid character varying(64) COLLATE pg_catalog."default",
    talukid character varying(64) COLLATE pg_catalog."default",
    districtid character varying(64) COLLATE pg_catalog."default",
    stateid character varying(64) COLLATE pg_catalog."default",
    poid character varying(64) COLLATE pg_catalog."default",
    pinno character varying(10) COLLATE pg_catalog."default",
    ot_state_region_province_en character varying(2500) COLLATE pg_catalog."default",
    ot_state_region_province_ml character varying(2500) COLLATE pg_catalog."default",
    countryid character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    birthdtlid character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT eg_birth_adoption_present_address_pkey PRIMARY KEY (id),
    CONSTRAINT eg_birth_adoption_present_address_fkey FOREIGN KEY (birthdtlid)
    REFERENCES public.eg_birth_details (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    );
-- Index: idx_eg_birth_adoption_present_address_birthdtlid

-- DROP INDEX IF EXISTS public.idx_eg_birth_adoption_present_address_birthdtlid;

CREATE INDEX IF NOT EXISTS idx_eg_birth_adoption_present_address_birthdtlid
    ON public.eg_birth_adoption_present_address USING btree
    (birthdtlid COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

CREATE TABLE IF NOT EXISTS public.eg_birth_adoption_present_address_audit
(
    operation character(1) COLLATE pg_catalog."default" NOT NULL,
    stamp timestamp without time zone NOT NULL,
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    resdnce_addr_type character varying(64) COLLATE pg_catalog."default",
    buildingno character varying(200) COLLATE pg_catalog."default",
    houseno character varying(200) COLLATE pg_catalog."default",
    res_asso_no character varying(250) COLLATE pg_catalog."default",
    housename_en character varying(2500) COLLATE pg_catalog."default",
    housename_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address1_en character varying(2500) COLLATE pg_catalog."default",
    ot_address1_ml character varying(2500) COLLATE pg_catalog."default",
    ot_address2_en character varying(2500) COLLATE pg_catalog."default",
    ot_address2_ml character varying(2500) COLLATE pg_catalog."default",
    locality_en character varying(2500) COLLATE pg_catalog."default",
    locality_ml character varying(2500) COLLATE pg_catalog."default",
    city_en character varying(2500) COLLATE pg_catalog."default",
    city_ml character varying(2500) COLLATE pg_catalog."default",
    villageid character varying(64) COLLATE pg_catalog."default",
    tenantid character varying(64) COLLATE pg_catalog."default",
    talukid character varying(64) COLLATE pg_catalog."default",
    districtid character varying(64) COLLATE pg_catalog."default",
    stateid character varying(64) COLLATE pg_catalog."default",
    poid character varying(64) COLLATE pg_catalog."default",
    pinno character varying(10) COLLATE pg_catalog."default",
    ot_state_region_province_en character varying(2500) COLLATE pg_catalog."default",
    ot_state_region_province_ml character varying(2500) COLLATE pg_catalog."default",
    countryid character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    birthdtlid character varying(64) COLLATE pg_catalog."default"
    );

-- FUNCTION: public.process_eg_birth_adoption_present_address_audit()

-- DROP FUNCTION IF EXISTS public.process_eg_birth_adoption_present_address_audit();

CREATE OR REPLACE FUNCTION public.process_eg_birth_adoption_present_address_audit()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO eg_birth_adoption_present_address_audit SELECT 'D', now(), OLD.*;
RETURN OLD;
ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO eg_birth_adoption_present_address_audit SELECT 'U', now(), OLD.*;
RETURN NEW;
END IF;
RETURN NULL;
END;
$BODY$;



-- Trigger: eg_birth_adoption_present_address_audit

-- DROP TRIGGER IF EXISTS eg_birth_adoption_present_address_audit ON public.eg_birth_adoption_present_address;

CREATE TRIGGER eg_birth_adoption_present_address_audit
    BEFORE DELETE OR UPDATE ON public.eg_birth_adoption_present_address
                         FOR EACH ROW EXECUTE FUNCTION public.process_eg_birth_adoption_present_address_audit();
