CREATE TABLE eg_adv_owner (
    uuid                  VARCHAR(256) NOT NULL,
    tenantid              VARCHAR(256),
    booking_id            VARCHAR(256) NOT NULL,
    status                VARCHAR(128),
    isprimaryowner        BOOLEAN DEFAULT FALSE,
    ownertype             VARCHAR(256),
    ownershippercentage   VARCHAR(128),
    institutionid         VARCHAR(128),
    relationship          VARCHAR(128),
    createdby             VARCHAR(128),
    createdtime           BIGINT,
    lastmodifiedby        VARCHAR(128),
    lastmodifiedtime      BIGINT,
    additionaldetails     JSONB,

    CONSTRAINT pk_eg_adv_owner PRIMARY KEY (uuid, booking_id),
    CONSTRAINT fk_eg_adv_owner FOREIGN KEY (booking_id)
        REFERENCES eg_adv_booking_detail(booking_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_eg_adv_owner_booking_id ON eg_adv_owner (booking_id);
CREATE INDEX idx_eg_adv_owner_tenantid ON eg_adv_owner (tenantid);


