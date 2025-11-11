-- Table: public.eg_ptr_registration

-- DROP TABLE IF EXISTS public.eg_ptr_registration;

CREATE TABLE IF NOT EXISTS public.eg_ptr_registration
(
    id character varying(64) COLLATE pg_catalog."default",
    tenantid character varying(64) COLLATE pg_catalog."default",
    applicationnumber character varying(64) COLLATE pg_catalog."default",
    applicantname character varying(64) COLLATE pg_catalog."default",
    fathername character varying(64) COLLATE pg_catalog."default",
    mobilenumber character varying(64) COLLATE pg_catalog."default",
    emailid character varying(64) COLLATE pg_catalog."default",
    aadharnumber character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    applicationtype character varying(64) COLLATE pg_catalog."default",
    validitydate bigint,
    status character varying(64) COLLATE pg_catalog."default",
    expireflag boolean,
    pettoken character varying(64) COLLATE pg_catalog."default",
    previousapplicationnumber character varying(64) COLLATE pg_catalog."default",
    propertyid character varying(64) COLLATE pg_catalog."default",
	CONSTRAINT eg_ptr_registration_pk PRIMARY KEY (id)
    
);

-- Table: public.eg_ptr_petdetails

-- DROP TABLE IF EXISTS public.eg_ptr_petdetails;

CREATE TABLE IF NOT EXISTS public.eg_ptr_petdetails
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    petname character varying(64) COLLATE pg_catalog."default",
    pettype character varying(64) COLLATE pg_catalog."default",
    breedtype character varying(64) COLLATE pg_catalog."default",
    petage character varying(64) COLLATE pg_catalog."default",
    petgender character varying(64) COLLATE pg_catalog."default",
    clinicname character varying(64) COLLATE pg_catalog."default",
    doctorname character varying(64) COLLATE pg_catalog."default",
    lastvaccinedate character varying(64) COLLATE pg_catalog."default",
    vaccinationnumber character varying(64) COLLATE pg_catalog."default",
    petdetailsid character varying(64) COLLATE pg_catalog."default",
    petcolor character varying(64) COLLATE pg_catalog."default",
    adoptiondate bigint,
    birthdate bigint,
    identificationmark character varying(256) COLLATE pg_catalog."default",
    CONSTRAINT uk_eg_ptr_petdetails PRIMARY KEY (id),
    CONSTRAINT fk_eg_ptr_petdetails FOREIGN KEY (petdetailsid)
        REFERENCES public.eg_ptr_registration (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
    
);

-- Table: public.eg_ptr_address

-- DROP TABLE IF EXISTS public.eg_ptr_address;

CREATE TABLE IF NOT EXISTS public.eg_ptr_address
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    tenantid character varying(64) COLLATE pg_catalog."default",
    doorno character varying(64) COLLATE pg_catalog."default",
    latitude double precision,
    longitude double precision,
    buildingname character varying(64) COLLATE pg_catalog."default",
    addressid character varying(64) COLLATE pg_catalog."default",
    addressnumber character varying(64) COLLATE pg_catalog."default",
    type character varying(64) COLLATE pg_catalog."default",
    addressline1 character varying(256) COLLATE pg_catalog."default",
    addressline2 character varying(256) COLLATE pg_catalog."default",
    landmark character varying(64) COLLATE pg_catalog."default",
    street character varying(64) COLLATE pg_catalog."default",
    city character varying(64) COLLATE pg_catalog."default",
    locality character varying(64) COLLATE pg_catalog."default",
    pincode character varying(64) COLLATE pg_catalog."default",
    detail character varying(64) COLLATE pg_catalog."default",
    registrationid character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    CONSTRAINT uk_eg_ptr_address PRIMARY KEY (id),
    CONSTRAINT fk_eg_ptr_address FOREIGN KEY (registrationid)
        REFERENCES public.eg_ptr_registration (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE  
);