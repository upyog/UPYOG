DROP TABLE IF EXISTS eg_ndc_owner;

CREATE TABLE eg_ndc_owner (
    uuid         VARCHAR(256) NOT NULL,
    tenantid              VARCHAR(256),
    ndcapplicationuuid    VARCHAR(256) NOT NULL,
    status                VARCHAR(128) ,
    isprimaryowner        BOOLEAN DEFAULT FALSE,
    ownertype             VARCHAR(256) ,
    ownershippercentage   VARCHAR(128),
    institutionid         VARCHAR(128) ,
    relationship          VARCHAR(128) ,
    createdby             VARCHAR(128) ,
    createdtime           BIGINT      ,
    lastmodifiedby        VARCHAR(128) ,
    lastmodifiedtime      BIGINT      ,
    additionaldetails     JSONB,

    CONSTRAINT pk_eg_ndc_owner PRIMARY KEY (uuid, ndcapplicationuuid),
    CONSTRAINT fk_eg_ndc_owner FOREIGN KEY (ndcapplicationuuid)
        REFERENCES eg_ndc_application(uuid)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_eg_ndc_owner_ndcapplicationuuid ON eg_ndc_owner (ndcapplicationuuid);
CREATE INDEX idx_eg_ndc_owner_tenantid ON eg_ndc_owner (tenantid);

