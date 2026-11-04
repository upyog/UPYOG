CREATE TABLE eg_ndc_applicants (
    uuid VARCHAR(128) PRIMARY KEY,
    tenantid VARCHAR(1000),
    firstname VARCHAR(1000),
    lastname VARCHAR(1000),
    mobile VARCHAR(15),
    email VARCHAR(1000),
    address TEXT,
    applicationstatus VARCHAR(100),
    createdby VARCHAR(1000),
    lastmodifiedby VARCHAR(1000),
    createdtime BIGINT,
    lastmodifiedtime BIGINT
);

CREATE TABLE eg_ndc_details (
    uuid VARCHAR(128) PRIMARY KEY,
    applicantid VARCHAR(128) REFERENCES eg_ndc_applicants(uuid),
    businessservice VARCHAR(128),
    consumercode VARCHAR(128),
    additionaldetails JSONB,
    dueamount NUMERIC,
    status VARCHAR(100)
);

CREATE TABLE eg_ndc_documents (
    uuid VARCHAR(128) PRIMARY KEY,
    applicantid VARCHAR(128) REFERENCES eg_ndc_applicants(uuid),
    documenttype VARCHAR(1000),
    documentattachment TEXT,
    createdby VARCHAR(1000),
    lastmodifiedby VARCHAR(1000),
    createdtime BIGINT,
    lastmodifiedtime BIGINT
);