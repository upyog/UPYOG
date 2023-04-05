-- Table: public.eg_birth_children_born

-- DROP TABLE IF EXISTS public.eg_birth_children_born;

CREATE TABLE IF NOT EXISTS public.eg_birth_children_born
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    child_name_en character varying(1000) COLLATE pg_catalog."default",
    child_name_ml character varying(1000) COLLATE pg_catalog."default",
    sex character varying(45) COLLATE pg_catalog."default",
    order_of_birth int,
    dob bigint,
    is_alive boolean,
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    CONSTRAINT eg_birth_children_born_pkey PRIMARY KEY (birthdtlid,order_of_birth),
    CONSTRAINT eg_birth_children_born_key UNIQUE (id)
    );


-- Table: public.eg_birth_applicant

-- DROP TABLE IF EXISTS public.eg_birth_applicant;

CREATE TABLE IF NOT EXISTS public.eg_birth_applicant
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    birthdtlid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    name_en character varying(1000) COLLATE pg_catalog."default",
    address_en character varying(2500) COLLATE pg_catalog."default",
    aadharno character varying(15) COLLATE pg_catalog."default",
    mobileno character varying(12) COLLATE pg_catalog."default",
    createdby character varying(45) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(45) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    is_declared boolean,
    declaration_id character varying(64) COLLATE pg_catalog."default",
    is_esigned boolean,
    CONSTRAINT eg_birth_applicant_pkey PRIMARY KEY (id),
    CONSTRAINT eg_birth_applicant_fkey FOREIGN KEY (birthdtlid)
    REFERENCES public.eg_birth_details (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
    );