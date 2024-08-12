DROP TABLE IF EXISTS eg_bmc_bank cascade;
CREATE TABLE IF NOT EXISTS eg_bmc_bank
(
    id SERIAL PRIMARY KEY,
    code character varying(50)  NOT NULL,
    name character varying(100)  NOT NULL,
    narration character varying(250) ,
    isactive boolean NOT NULL,
    type character varying(50) ,
    createddate timestamp without time zone DEFAULT now(),
    lastmodifieddate timestamp without time zone DEFAULT now(),
    lastmodifiedby bigint,
    version bigint DEFAULT 0,
    createdby bigint
);

DROP TABLE IF EXISTS eg_bmc_bankbranch cascade;

CREATE TABLE IF NOT EXISTS eg_bmc_bankbranch
(
    id SERIAL PRIMARY KEY,
    bankid bigint,
    branchcode character varying(50)  NOT NULL,
    branchname character varying(50)  NOT NULL,
    branchaddress1 character varying(50)  NOT NULL,
    branchaddress2 character varying(50) ,
    branchcity character varying(50) ,
    branchstate character varying(50) ,
    branchpin character varying(50) ,
    branchphone character varying(15) ,
    branchfax character varying(15) ,
    ifsc character varying(50)  NOT NULL,
    contactperson character varying(50) ,
    isactive boolean NOT NULL,
    narration character varying(250) ,
    micr character varying(50) ,
    createddate timestamp without time zone DEFAULT now(),
    lastmodifieddate timestamp without time zone DEFAULT now(),
    lastmodifiedby bigint,
    version bigint DEFAULT 0,
    createdby bigint
);

CREATE TABLE IF NOT EXISTS eg_bmc_userbank
(
    id SERIAL PRIMARY KEY,
    bankbranchid bigint NOT NULL,
    userid bigint NOT NULL,
    tenantid character varying(255)  NOT NULL,
    accountnumber character varying(50)  NOT NULL,
    modifiedon bigint,
    createdby character varying(255)  NOT NULL,
    modifiedby character varying(255) ,
    CONSTRAINT eg_bmc_userbank_pkey PRIMARY KEY (id),
    CONSTRAINT fk_bank FOREIGN KEY (bankbranchid)
        REFERENCES public.eg_bmc_bankbranch (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
        NOT VALID,
    CONSTRAINT fk_bank_user FOREIGN KEY (userid, tenantid)
        REFERENCES public.eg_user (id, tenantid) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS fki_fk_bank
    ON public.eg_bmc_userbank USING btree
    (bankbranchid ASC NULLS LAST);

CREATE TABLE IF NOT EXISTS eg_bmc_document (
    ID SERIAL PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Description VARCHAR(1000),
    CreatedOn BIGINT NOT NULL,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS eg_bmc_userdocument (
    Id SERIAL PRIMARY KEY,
    documentID BIGINT NOT NULL,
    userid BIGINT NOT NULL,
    tenantid character varying(255)  NOT NULL,
    available boolean default false,
    ModifiedOn BIGINT,
    CreatedBy VARCHAR(255) NOT NULL,
    ModifiedBy VARCHAR(255),
    CONSTRAINT fk_document FOREIGN KEY (documentID)
        REFERENCES eg_bmc_document (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
        NOT VALID,
    CONSTRAINT fk_document_user FOREIGN KEY (userid, tenantid)
        REFERENCES eg_user (id, tenantid) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);