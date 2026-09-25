CREATE TABLE eg_challan_citizen (
    uuid       VARCHAR(256) NOT NULL,
    tenantid              VARCHAR(256),
    challanid    VARCHAR(256) NOT NULL,
    primaryrole     JSONB,
    createdby             VARCHAR(128) ,
    createdtime           BIGINT      ,
    lastmodifiedby        VARCHAR(128) ,
    lastmodifiedtime      BIGINT,


    CONSTRAINT pk_eg_challan_citizen PRIMARY KEY (uuid, challanid),
    CONSTRAINT fk_eg_challan_citizen FOREIGN KEY (challanid)
        REFERENCES eg_challan(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_eg_challan_citizen_challanid ON eg_challan_citizen (challanid);
CREATE INDEX idx_eg_challan_citizen_tenantid ON eg_challan_citizen (tenantid);