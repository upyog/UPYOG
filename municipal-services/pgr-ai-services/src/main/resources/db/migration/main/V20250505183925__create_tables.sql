CREATE TABLE IF NOT EXISTS ug_pgr_service
(
    id character varying(64),
    tenantid character varying(256) NOT NULL,
    servicecode character varying(256) NOT NULL,
    serviceType character varying(256),
    inputgrievance character varying(1000),
    servicerequestid character varying(256) NOT NULL,
    accountid character varying(256),
    additionaldetails jsonb,
    applicationstatus character varying(128),
    rating smallint,
    source character varying(256),
    active boolean DEFAULT true,
    priority character varying(50),
	createdby character varying(256) NOT NULL,
    createdtime bigint NOT NULL,
    lastmodifiedby character varying(256),
    lastmodifiedtime bigint,
    CONSTRAINT pk_ug_pgr_service PRIMARY KEY (tenantid, servicerequestid),
    CONSTRAINT uk_ug_pgr_service UNIQUE (id)
);

CREATE INDEX IF NOT EXISTS index_ug_pgr_service_accountid
    ON ug_pgr_service USING btree
    (accountid ASC NULLS LAST);

CREATE INDEX IF NOT EXISTS index_ug_pgr_service_applicationstatus
    ON ug_pgr_service USING btree
    (applicationstatus ASC NULLS LAST);

CREATE INDEX IF NOT EXISTS index_ug_pgr_service_id
    ON ug_pgr_service USING btree
    (id ASC NULLS LAST);

CREATE INDEX IF NOT EXISTS index_ug_pgr_service_servicecode
    ON ug_pgr_service USING btree
    (servicecode ASC NULLS LAST);

CREATE INDEX IF NOT EXISTS index_ug_pgr_service_tenantid_servicerequestid
    ON ug_pgr_service USING btree
    (tenantid ASC NULLS LAST, servicerequestid ASC NULLS LAST);



CREATE TABLE IF NOT EXISTS ug_pgr_address
(
    tenantid character varying(256) NOT NULL,
    id character varying(256) NOT NULL,
    parentid character varying(256) NOT NULL,
    doorno character varying(128),
    plotno character varying(256),
    buildingname character varying(1024),
    street character varying(1024),
    landmark character varying(1024),
    city character varying(512),
    pincode character varying(16),
    locality character varying(128) NOT NULL,
    district character varying(256),
    region character varying(256),
    state character varying(256),
    country character varying(512),
    latitude numeric(9,6),
    longitude numeric(10,7),
    createdby character varying(128) NOT NULL,
    createdtime bigint NOT NULL,
    lastmodifiedby character varying(128),
    lastmodifiedtime bigint,
    additionaldetails jsonb,
    CONSTRAINT pk_ug_pgr_address PRIMARY KEY (id),
    CONSTRAINT fk_ug_pgr_address FOREIGN KEY (parentid)
        REFERENCES ug_pgr_service (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE INDEX IF NOT EXISTS index_ug_pgr_address_locality
    ON ug_pgr_address USING btree
    (locality ASC NULLS LAST);