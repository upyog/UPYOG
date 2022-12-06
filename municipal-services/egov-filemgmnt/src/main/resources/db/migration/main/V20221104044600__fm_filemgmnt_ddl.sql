-- Table: public.eg_fm_applicantpersonal

-- DROP TABLE IF EXISTS public.eg_fm_applicantpersonal;

CREATE TABLE IF NOT EXISTS public.eg_fm_applicantpersonal
(
    id character varying(64) ,
    aadhaarno character varying(20) ,
    email character varying(64) ,
    firstname character varying(20) ,
    lastname character varying(64) ,
    title character varying(10) ,
    mobileno character varying(64) ,
    tenantid character varying(15) ,
    createdby character varying(64) ,
    createdtime bigint,
    lastmodifiedby character varying(64) ,
    lastmodifiedtime bigint,
    fatherfirstname character varying(45) ,
    fatherlastname character varying(45) ,
    motherfirstname character varying(45) ,
    motherlastname character varying(45) ,
    CONSTRAINT eg_fm_applicantpersonal_pkey PRIMARY KEY (id)
);

-- Table: public.eg_fm_applicantaddress

-- DROP TABLE IF EXISTS public.eg_fm_applicantaddress;

CREATE TABLE IF NOT EXISTS public.eg_fm_applicantaddress
(
    id character varying(64) ,
    houseno character varying(20) ,
    housename character varying(64) ,
    street character varying(64) ,
    pincode character varying(10) ,
    postofficename character varying(64) ,
    createdby character varying(64) ,
    createddate bigint,
    lastmodifiedby character varying(64) ,
    lastmodifieddate bigint,
    applicantpersonalid character varying(64) ,
    CONSTRAINT eg_fm_applicantaddress_pkey PRIMARY KEY (id),
    CONSTRAINT eg_fm_applicantaddress_fkey FOREIGN KEY (applicantpersonalid)
        REFERENCES public.eg_fm_applicantpersonal (id) 
);

-- Table: public.eg_fm_applicantdocuments

-- DROP TABLE IF EXISTS public.eg_fm_applicantdocuments;

CREATE TABLE IF NOT EXISTS public.eg_fm_applicantdocuments
(
    id character varying(64),
    applicantpersonalid character varying(64) ,
    documenttypeid character varying(45) ,
    documentnumber character varying(45),
    docexpirydate bigint,
    createdby character varying(45) ,
    createddate bigint,
    lastmodifiedby character varying(45) ,
    lastmodifieddate bigint,
    CONSTRAINT eg_fm_applicantdocuments_pkey PRIMARY KEY (id),
 CONSTRAINT eg_fm_applicantdocuments_fkey FOREIGN KEY (applicantpersonalid)
        REFERENCES public.eg_fm_applicantpersonal (id) 
);

-- Table: public.eg_fm_applicantservicedocuments

-- DROP TABLE IF EXISTS public.eg_fm_applicantservicedocuments;

CREATE TABLE IF NOT EXISTS public.eg_fm_applicantservicedocuments
(
    id character varying(64),
    applicantpersonalid character varying(45),
    servicedetailsid character varying(64) ,
    documenttypeid character varying(45) ,
    filestoreid character varying(45) ,
    active character varying(45) ,
    createdby character varying(45) ,
    createddate bigint,
    lastmodifiedby character varying(45) ,
    lastmodifieddate bigint,
    documentnumber character varying(45),
    CONSTRAINT eg_fm_applicantservicendocuments_pkey PRIMARY KEY (id),
CONSTRAINT eg_fm_applicantservicedocuments_fkey FOREIGN KEY (applicantpersonalid)
        REFERENCES public.eg_fm_applicantpersonal (id) 
);

-- Table: public.eg_fm_servicedetails

-- DROP TABLE IF EXISTS public.eg_fm_servicedetails;

CREATE TABLE IF NOT EXISTS public.eg_fm_servicedetails
(
    id character varying(64) ,
    applicantpersonalid character varying(64) ,
    serviceid character varying(45) ,
    servicecode character varying(45) ,
    businessservice character varying(45) ,
    workflowcode character varying(45) ,
    createdby character varying(45) ,
    createdtime bigint,
    lastmodifiedby character varying(45) ,
    lastmodifiedtime bigint,
    servicesubtype character varying(45) ,
    CONSTRAINT eg_fm_servicedetails_pkey PRIMARY KEY (id),
CONSTRAINT eg_fm_servicedetails_fkey FOREIGN KEY (applicantpersonalid )
        REFERENCES public.eg_fm_applicantpersonal (id)
);

-- Table: public.eg_fm_filedetails

-- DROP TABLE IF EXISTS public.eg_fm_filedetails;

CREATE TABLE IF NOT EXISTS public.eg_fm_filedetails
(
    id character varying(64) ,
    tenantid character varying(15) ,
    applicantpersonalid character varying(45),
    servicedetailsid character varying(45) ,
    filenumber character varying(45) ,
    filecode character varying(45) ,
    filename character varying(45) ,
    filearisingmode character varying(45) ,
    filearisingdate bigint,
    financialyear character varying(10) ,
    applicationdate character varying(45) ,
    workflowcode character varying(45) ,
    action character varying(45) ,
    filestatus character varying(45) ,
    createdby character varying(45) ,
    createddate bigint,
    lastmodifiedby character varying(45) ,
    lastmodifieddate bigint,
    filecategory character varying(45), 
   CONSTRAINT eg_fm_filedetails_pkey PRIMARY KEY (id),
CONSTRAINT eg_fm_filedetails_service_fkey FOREIGN KEY (servicedetailsid)
        REFERENCES public.eg_fm_servicedetails (id) ,
CONSTRAINT eg_fm_filedetails_applicant_fkey FOREIGN KEY (applicantpersonalid )
        REFERENCES public.eg_fm_applicantpersonal (id) 
);

