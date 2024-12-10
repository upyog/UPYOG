CREATE TABLE IF NOT EXISTS public.eg_asset_assignmentdetails
(
    assignmentId character varying(255)  NOT NULL,
    applicationno character varying(255) ,
    tenantid character varying(255) ,
    assignedusername character varying(255) ,
    designation character varying(255) ,
    department character varying(255) ,
    assigneddate bigint,
    returndate bigint,
    createdby character varying(255) ,
    lastmodifiedby character varying(255) ,
    createdtime bigint,
    lastmodifiedtime bigint,
    assetid character varying(255),
    isassigned boolean,
    employeecode character varying(64),
    CONSTRAINT eg_asset_assignentdetails_pkey PRIMARY KEY (assignmentId),
    CONSTRAINT eg_asset_assignentdetails_assetid_fkey FOREIGN KEY (assetid)
        REFERENCES public.eg_asset_assetdetails (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);


CREATE TABLE IF NOT EXISTS public.eg_asset_assignment_history
(
    assignmentId character varying(255)  NOT NULL,
    applicationno character varying(255) ,
    tenantid character varying(255) ,
    assignedusername character varying(255) ,
    designation character varying(255) ,
    department character varying(255) ,
    assigneddate bigint,
    returndate bigint,
    createdby character varying(255) ,
    lastmodifiedby character varying(255) ,
    createdtime bigint,
    lastmodifiedtime bigint,
    assetid character varying(255) ,
    isassigned boolean,
    employeecode character varying(64)
);