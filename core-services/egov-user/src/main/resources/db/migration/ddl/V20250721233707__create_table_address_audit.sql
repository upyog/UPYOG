CREATE TABLE eg_user_address_audit (
    id bigint NOT NULL,
    version numeric DEFAULT 0,
    createddate timestamp NOT NULL,
    lastmodifieddate timestamp,
    createdby bigint NOT NULL,
    lastmodifiedby bigint,
    type VARCHAR (50) NOT NULL,
    address VARCHAR(300),
    city VARCHAR(300),
    pincode VARCHAR(10),
    userid bigint NOT NULL,
    tenantid VARCHAR(256) NOT NULL,
    auditcreatedby bigint,
    auditcreatedtime bigint
);

ALTER TABLE eg_user_address_audit ADD CONSTRAINT eg_user_address_audit_pkey PRIMARY KEY (id, auditcreatedtime);

CREATE TABLE eg_userrole_v1_audit (
    role_code character varying(50) NOT NULL,
    role_tenantid character varying(256) NOT NULL,
    user_id bigint NOT NULL,
    user_tenantid character varying(256) NOT NULL,
    lastmodifieddate timestamp,
    auditcreatedby bigint,
    auditcreatedtime bigint
);

ALTER TABLE eg_userrole_v1_audit ADD CONSTRAINT eg_userrole_v1_audit_pkey PRIMARY KEY (role_code, role_tenantid, user_id, user_tenantid, auditcreatedtime); 