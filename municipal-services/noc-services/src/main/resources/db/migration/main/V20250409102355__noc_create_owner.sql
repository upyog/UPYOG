CREATE TABLE eg_noc_owner (
    id         VARCHAR(256) NOT NULL,
    tenantid              VARCHAR(256),
    nocid    VARCHAR(256) NOT NULL,
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

    CONSTRAINT pk_eg_noc_owner PRIMARY KEY (id, nocid),
    CONSTRAINT fk_eg_noc_owner FOREIGN KEY (nocid)
        REFERENCES eg_noc(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_eg_noc_owner_nocid ON eg_noc_owner (nocid);
CREATE INDEX idx_eg_noc_owner_tenantid ON eg_noc_owner (tenantid);

