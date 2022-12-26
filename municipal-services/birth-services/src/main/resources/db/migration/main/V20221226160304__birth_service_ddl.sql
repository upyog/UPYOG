-- Table: public.eg_birth_correction

-- DROP TABLE IF EXISTS public.eg_birth_correction;

CREATE TABLE IF NOT EXISTS public.eg_birth_correction
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    dateofreport bigint,
    dateofbirth bigint,
    timeofbirth bigint,
    am_pm character varying(20) COLLATE pg_catalog."default",
    firstname_en character varying(1000) COLLATE pg_catalog."default",
    firstname_ml character varying(1000) COLLATE pg_catalog."default",
    middlename_en character varying(1000) COLLATE pg_catalog."default",
    middlename_ml character varying(1000) COLLATE pg_catalog."default",
    lastname_en character varying(1000) COLLATE pg_catalog."default",
    lastname_ml character varying(1000) COLLATE pg_catalog."default",
    father_firstname_en character varying(1000) COLLATE pg_catalog."default",
    father_firstname_ml character varying(1000) COLLATE pg_catalog."default",
    father_middlename_en character varying(1000) COLLATE pg_catalog."default",
    father_middlename_ml character varying(1000) COLLATE pg_catalog."default",
    father_lastname_en character varying(1000) COLLATE pg_catalog."default",
    father_lastname_ml character varying(1000) COLLATE pg_catalog."default",
    mother_firstname_en character varying(1000) COLLATE pg_catalog."default",
    mother_firstname_ml character varying(1000) COLLATE pg_catalog."default",
    mother_middlename_en character varying(1000) COLLATE pg_catalog."default",
    mother_middlename_ml character varying(1000) COLLATE pg_catalog."default",
    mother_lastname_en character varying(1000) COLLATE pg_catalog."default",
    mother_lastname_ml character varying(1000) COLLATE pg_catalog."default",
    tenantid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    gender smallint NOT NULL,
    remarks_en character varying(2500) COLLATE pg_catalog."default",
    remarks_ml character varying(2500) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    esign_user_code character varying(64) COLLATE pg_catalog."default",
    esign_user_desig_code character varying(64) COLLATE pg_catalog."default",
    is_born_outside boolean,
    ot_passportno character varying(100) COLLATE pg_catalog."default",
    ot_dateofarrival bigint,
    applicationtype character varying(64) COLLATE pg_catalog."default" NOT NULL,
    businessservice character varying(64) COLLATE pg_catalog."default" NOT NULL,
    workflowcode character varying(64) COLLATE pg_catalog."default" NOT NULL,
    fm_fileno character varying(64) COLLATE pg_catalog."default",
    file_date bigint,
    file_status character varying(64) COLLATE pg_catalog."default",
    applicationno character varying(64) COLLATE pg_catalog."default",
    registrationno character varying(64) COLLATE pg_catalog."default",
    registration_date bigint,
    action character varying(64) COLLATE pg_catalog."default",
    status character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT eg_birth_correction_pkey PRIMARY KEY (id),
    CONSTRAINT eg_birth_correction_applicationno_key UNIQUE (applicationno),
    CONSTRAINT eg_birth_correction_fm_fileno_ukey UNIQUE (fm_fileno, tenantid),
    CONSTRAINT eg_birth_correction_registrationno_ukey1 UNIQUE (registrationno, tenantid)
    );

-- Index: idx_eg_birth_correction_tenantid

-- DROP INDEX IF EXISTS public.idx_eg_birth_correction_tenantid;

CREATE INDEX IF NOT EXISTS idx_eg_birth_correction_tenantid
    ON public.eg_birth_correction USING btree
    (tenantid COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- Table: public.eg_birth_correction_audit

-- DROP TABLE IF EXISTS public.eg_birth_correction_audit;

CREATE TABLE IF NOT EXISTS public.eg_birth_correction_audit
(
    operation character(1) COLLATE pg_catalog."default" NOT NULL,
    stamp timestamp without time zone NOT NULL,
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    dateofreport bigint,
    dateofbirth bigint,
    timeofbirth bigint,
    am_pm character varying(20) COLLATE pg_catalog."default",
    firstname_en character varying(1000) COLLATE pg_catalog."default",
    firstname_ml character varying(1000) COLLATE pg_catalog."default",
    middlename_en character varying(1000) COLLATE pg_catalog."default",
    middlename_ml character varying(1000) COLLATE pg_catalog."default",
    lastname_en character varying(1000) COLLATE pg_catalog."default",
    lastname_ml character varying(1000) COLLATE pg_catalog."default",
    father_firstname_en character varying(1000) COLLATE pg_catalog."default",
    father_firstname_ml character varying(1000) COLLATE pg_catalog."default",
    father_middlename_en character varying(1000) COLLATE pg_catalog."default",
    father_middlename_ml character varying(1000) COLLATE pg_catalog."default",
    father_lastname_en character varying(1000) COLLATE pg_catalog."default",
    father_lastname_ml character varying(1000) COLLATE pg_catalog."default",
    mother_firstname_en character varying(1000) COLLATE pg_catalog."default",
    mother_firstname_ml character varying(1000) COLLATE pg_catalog."default",
    mother_middlename_en character varying(1000) COLLATE pg_catalog."default",
    mother_middlename_ml character varying(1000) COLLATE pg_catalog."default",
    mother_lastname_en character varying(1000) COLLATE pg_catalog."default",
    mother_lastname_ml character varying(1000) COLLATE pg_catalog."default",
    tenantid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    gender smallint NOT NULL,
    remarks_en character varying(2500) COLLATE pg_catalog."default",
    remarks_ml character varying(2500) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    esign_user_code character varying(64) COLLATE pg_catalog."default",
    esign_user_desig_code character varying(64) COLLATE pg_catalog."default",
    is_born_outside boolean,
    ot_passportno character varying(100) COLLATE pg_catalog."default",
    ot_dateofarrival bigint,
    applicationtype character varying(64) COLLATE pg_catalog."default" NOT NULL,
    businessservice character varying(64) COLLATE pg_catalog."default" NOT NULL,
    workflowcode character varying(64) COLLATE pg_catalog."default" NOT NULL,
    fm_fileno character varying(64) COLLATE pg_catalog."default",
    file_date bigint,
    file_status character varying(64) COLLATE pg_catalog."default",
    applicationno character varying(64) COLLATE pg_catalog."default",
    registrationno character varying(64) COLLATE pg_catalog."default",
    registration_date bigint,
    action character varying(64) COLLATE pg_catalog."default",
    status character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default"
    );

-- FUNCTION: public.process_eg_birth_correction_audit()

-- DROP FUNCTION IF EXISTS public.process_eg_birth_correction_audit();

CREATE OR REPLACE FUNCTION public.process_eg_birth_correction_audit()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO eg_birth_correction_audit SELECT 'D', now(), OLD.*;
RETURN OLD;
ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO eg_birth_correction_audit SELECT 'U', now(), OLD.*;
RETURN NEW;
END IF;
RETURN NULL;
END;
$BODY$;

-- Trigger: eg_birth_correction_audit

-- DROP TRIGGER IF EXISTS eg_birth_correction_audit ON public.eg_birth_correction;

CREATE TRIGGER eg_birth_correction_audit
    BEFORE DELETE OR UPDATE
                         ON public.eg_birth_correction
                         FOR EACH ROW
                         EXECUTE FUNCTION public.process_eg_birth_correction_audit();

-- Table: public.eg_birth_correction_permanent_address

-- DROP TABLE IF EXISTS public.eg_birth_correction_permanent_address;

CREATE TABLE IF NOT EXISTS public.eg_birth_correction_permanent_address
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
    CONSTRAINT eg_birth_correction_permanent_address_pkey PRIMARY KEY (id),
    CONSTRAINT eg_birth_correction_permanent_address_fkey FOREIGN KEY (birthdtlid)
    REFERENCES public.eg_birth_correction (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    );

-- Index: idx_eg_birth_correction_permanent_address_info_birthdtlid

-- DROP INDEX IF EXISTS public.idx_eg_birth_correction_permanent_address_info_birthdtlid;

CREATE INDEX IF NOT EXISTS idx_eg_birth_correction_permanent_address_info_birthdtlid
    ON public.eg_birth_correction_permanent_address USING btree
    (birthdtlid COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- Table: public.eg_birth_correction_permanent_address_audit

-- DROP TABLE IF EXISTS public.eg_birth_correction_permanent_address_audit;

CREATE TABLE IF NOT EXISTS public.eg_birth_correction_permanent_address_audit
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
-- FUNCTION: public.process_eg_birth_correction_permanent_address_audit()

-- DROP FUNCTION IF EXISTS public.process_eg_birth_correction_permanent_address_audit();

CREATE OR REPLACE FUNCTION public.process_eg_birth_correction_permanent_address_audit()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO eg_birth_correction_permanent_address_audit SELECT 'D', now(), OLD.*;
RETURN OLD;
ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO eg_birth_correction_permanent_address_audit SELECT 'U', now(), OLD.*;
RETURN NEW;
END IF;
RETURN NULL;
END;
$BODY$;
-- Trigger: eg_birth_correction_permanent_address_audit

-- DROP TRIGGER IF EXISTS eg_birth_correction_permanent_address_audit ON public.eg_birth_correction_permanent_address;

CREATE TRIGGER eg_birth_correction_permanent_address_audit
    BEFORE DELETE OR UPDATE
                         ON public.eg_birth_correction_permanent_address
                         FOR EACH ROW
                         EXECUTE FUNCTION public.process_eg_birth_correction_permanent_address_audit();

-- Table: public.eg_birth_correction_present_address

-- DROP TABLE IF EXISTS public.eg_birth_correction_present_address;

CREATE TABLE IF NOT EXISTS public.eg_birth_correction_present_address
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
    CONSTRAINT eg_birth_correction_present_address_pkey PRIMARY KEY (id),
    CONSTRAINT eg_birth_correction_present_address_fkey FOREIGN KEY (birthdtlid)
    REFERENCES public.eg_birth_correction (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    );

-- Index: idx_eg_birth_correction_present_address_info_birthdtlid

-- DROP INDEX IF EXISTS public.idx_eg_birth_correction_present_address_info_birthdtlid;

CREATE INDEX IF NOT EXISTS idx_eg_birth_correction_present_address_info_birthdtlid
    ON public.eg_birth_correction_present_address USING btree
    (birthdtlid COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- Table: public.eg_birth_correction_present_address_audit

-- DROP TABLE IF EXISTS public.eg_birth_correction_present_address_audit;

CREATE TABLE IF NOT EXISTS public.eg_birth_correction_present_address_audit
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
-- FUNCTION: public.process_eg_birth_correction_present_address_audit()

-- DROP FUNCTION IF EXISTS public.process_eg_birth_correction_present_address_audit();

CREATE OR REPLACE FUNCTION public.process_eg_birth_correction_present_address_audit()
    RETURNS trigger
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE NOT LEAKPROOF
AS $BODY$
BEGIN
        IF (TG_OP = 'DELETE') THEN
            INSERT INTO eg_birth_correction_present_address_audit SELECT 'D', now(), OLD.*;
RETURN OLD;
ELSIF (TG_OP = 'UPDATE') THEN
            INSERT INTO eg_birth_correction_present_address_audit SELECT 'U', now(), OLD.*;
RETURN NEW;
END IF;
RETURN NULL;
END;
$BODY$;

-- Trigger: eg_birth_correction_present_address_audit

-- DROP TRIGGER IF EXISTS eg_birth_correction_present_address_audit ON public.eg_birth_correction_present_address;

CREATE TRIGGER eg_birth_correction_present_address_audit
    BEFORE DELETE OR UPDATE
                         ON public.eg_birth_correction_present_address
                         FOR EACH ROW
                         EXECUTE FUNCTION public.process_eg_birth_correction_present_address_audit();