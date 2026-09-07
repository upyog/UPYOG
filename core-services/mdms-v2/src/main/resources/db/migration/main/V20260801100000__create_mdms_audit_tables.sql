CREATE TABLE IF NOT EXISTS eg_mdms_schema_definition_audit (
    id VARCHAR(64) NOT NULL,
    tenantid VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    description VARCHAR(512),
    definition JSONB NOT NULL,
    isactive BOOLEAN NOT NULL,
    createdBy character varying(64),
    lastModifiedBy character varying(64),
    createdTime bigint,
    lastModifiedTime bigint,
    operation varchar(20)
);

CREATE TABLE IF NOT EXISTS eg_mdms_data_audit (
    id VARCHAR(64) NOT NULL,
    tenantid VARCHAR(255) NOT NULL,
    uniqueidentifier VARCHAR(255),
    schemacode VARCHAR(255) NOT NULL,
    data JSONB NOT NULL,
    isactive BOOLEAN NOT NULL,
    createdBy character varying(64),
    lastModifiedBy character varying(64),
    createdTime bigint,
    lastModifiedTime bigint,
    operation varchar(20)
);