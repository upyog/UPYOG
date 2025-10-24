CREATE TABLE eg_noc(
    id character varying(64) NOT NULL,
    applicationno character varying(64),
    tenantid character varying(256),
    status character varying(64),

    createdby character varying(256),
    lastmodifiedby character varying(256),
    createdtime bigint,
    lastmodifiedtime bigint,
    nocno character varying(64) DEFAULT NULL,
    applicationType character varying(64) NOT NULL,
    noctype character varying(64) NOT NULL,
    accountid character varying(256) DEFAULT NULL,


    applicationstatus character varying(64) NOT NULL,
    CONSTRAINT pk_eg_noc PRIMARY KEY (id)
);

CREATE TABLE eg_noc_auditdetails(
    id character varying(64) NOT NULL,
    applicationno character varying(64),
    tenantid character varying(256),
    status character varying(64),
--    landid character varying(256),
--    additionaldetails jsonb,
    createdby character varying(256),
    lastmodifiedby character varying(256),
    createdtime bigint,
    lastmodifiedtime bigint,
    nocno character varying(64) DEFAULT NULL,
    applicationType character varying(64) NOT NULL,
    noctype character varying(64) NOT NULL,
    accountid character varying(256) DEFAULT NULL,

    applicationstatus character varying(64) NOT NULL
);

CREATE TABLE eg_noc_document(
    uuid character varying(64) NOT NULL,
    documenttype character varying(64),
    documentattachment character varying(64),
    documentuid character varying(64),
    nocid character varying(64),

    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
    CONSTRAINT uk_eg_noc_document PRIMARY KEY (uuid),
    CONSTRAINT fk_eg_noc_document FOREIGN KEY (nocid)
        REFERENCES public.eg_noc (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE eg_noc_details(
    id character varying(64) NOT NULL,
    nocid character varying(64) NOT NULL,
    -- Applicant Details
    additionaldetails jsonb,
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
    tenantid character varying(256),
    CONSTRAINT pk_eg_noc_details PRIMARY KEY (id),
    CONSTRAINT fk_eg_noc_details FOREIGN KEY (nocid)
        REFERENCES public.eg_noc (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE INDEX noc_index ON eg_noc
(
    applicationno,
    nocno,
    tenantid,
    id,
    applicationstatus,
    noctype
);

CREATE INDEX idx_eg_noc_details_nocuuid ON eg_noc_details (nocid);

CREATE SEQUENCE IF NOT EXISTS SEQ_EG_NOC_RECEIPT_ID;

