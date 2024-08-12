DROP TABLE IF EXISTS eg_bmc_CourseMaster;
DROP TABLE IF EXISTS eg_bmc_ElectoralWardMaster;
DROP TABLE IF EXISTS eg_bmc_WardMaster;
DROP TABLE IF EXISTS eg_bmc_bank;
DROP TABLE IF EXISTS eg_bmc_bankbranch;



CREATE TABLE IF NOT EXISTS eg_bmc_bank
(
    id bigint NOT NULL,
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



CREATE TABLE IF NOT EXISTS eg_bmc_bankbranch
(
    id bigint NOT NULL,
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

