-- Table: public.eg_fm_draftfile

-- DROP TABLE IF EXISTS public.eg_fm_draftfile;

CREATE TABLE IF NOT EXISTS public.eg_fm_draftfile
(
    id character varying(64) ,
    filecode character varying(20) ,
    drafttype character varying(64) ,
    drafttext character varying(20) ,
    workflowcode character varying(64) ,
    seatid character varying(10) ,
    draftdate character varying(64) ,
    status character varying(15) ,
    createdby character varying(64) ,
    createdtime bigint,
    lastmodifiedby character varying(64) ,
    lastmodifiedtime bigint,
    fatherfirstname character varying(45) ,
    fatherlastname character varying(45) ,
    motherfirstname character varying(45) ,
    motherlastname character varying(45) ,
    CONSTRAINT eg_fm_draftfile_pkey PRIMARY KEY (id)
);

-- Table: public.eg_fm_communicationfile

-- DROP TABLE IF EXISTS public.eg_fm_communicationfile;

CREATE TABLE IF NOT EXISTS public.eg_fm_communicationfile
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    subjecttypeid character varying(20) COLLATE pg_catalog."default",
    senderid character varying(64) COLLATE pg_catalog."default",
    priorityid character varying(64) COLLATE pg_catalog."default",
    filestoreid character varying(10) COLLATE pg_catalog."default",
    details character varying(64) COLLATE pg_catalog."default",
    createdby character varying(64) COLLATE pg_catalog."default",
    createddate bigint,
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    lastmodifieddate bigint,
   
    CONSTRAINT eg_fm_communicationfile_pkey PRIMARY KEY (id)
   
       
)
