-- Create ptr_owner table for storing owner information
-- This table will store only owner metadata and reference to user service

CREATE TABLE IF NOT EXISTS public.eg_ptr_owner
(
    uuid character varying(256) COLLATE pg_catalog."default" NOT NULL,
    tenantid character varying(256) COLLATE pg_catalog."default",
    ptrregistrationid character varying(256) COLLATE pg_catalog."default" NOT NULL,
    status character varying(128) COLLATE pg_catalog."default",
    isprimaryowner boolean DEFAULT false,
    ownertype character varying(256) COLLATE pg_catalog."default",
    ownershippercentage character varying(128) COLLATE pg_catalog."default",
    institutionid character varying(128) COLLATE pg_catalog."default",
    relationship character varying(128) COLLATE pg_catalog."default",
    createdby character varying(128) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedby character varying(128) COLLATE pg_catalog."default",
    lastmodifiedtime bigint,
    additionaldetails jsonb,
    CONSTRAINT eg_ptr_owner_pk PRIMARY KEY (uuid, ptrregistrationid),
    CONSTRAINT fk_eg_ptr_owner_registration FOREIGN KEY (ptrregistrationid)
        REFERENCES public.eg_ptr_registration (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_eg_ptr_owner_ptrregistrationid ON public.eg_ptr_owner (ptrregistrationid);
CREATE INDEX IF NOT EXISTS idx_eg_ptr_owner_tenantid ON public.eg_ptr_owner (tenantid);
CREATE INDEX IF NOT EXISTS idx_eg_ptr_owner_uuid ON public.eg_ptr_owner (uuid);
CREATE INDEX IF NOT EXISTS idx_eg_ptr_owner_status ON public.eg_ptr_owner (status);
CREATE INDEX IF NOT EXISTS idx_eg_ptr_owner_isprimaryowner ON public.eg_ptr_owner (isprimaryowner);

-- Remove applicant-related columns from ptr_registration table
-- These columns will no longer be used as owner data will be stored in user service
ALTER TABLE public.eg_ptr_registration 
DROP COLUMN IF EXISTS applicantname,
DROP COLUMN IF EXISTS fathername,
DROP COLUMN IF EXISTS mobilenumber,
DROP COLUMN IF EXISTS emailid,
DROP COLUMN IF EXISTS aadharnumber;
