-- PQM Anomaly Finder Database Schema

CREATE TABLE IF NOT EXISTS eg_pqm_anomaly_details
(
    id                character varying(64),
    testId            character varying(64),
    tenantId          character varying(64),
    anomalyType       character varying(64),
    description       character varying(500),
    referenceId       character varying(64),
    resolutionStatus  character varying(64),
    additionalDetails json,
    isActive          boolean,
    createdBy         character varying(64),
    lastModifiedBy    character varying(64),
    createdTime       bigint,
    lastModifiedTime  bigint,
    CONSTRAINT pk_pqm_anomaly_details PRIMARY KEY (id),
    CONSTRAINT fk_pqm_anomaly_details_pqm_tests FOREIGN KEY (testId) REFERENCES eg_pqm_tests (testId)
        ON DELETE CASCADE
);



CREATE TABLE IF NOT EXISTS eg_pqm_anomaly_details_auditlog
(
    id                character varying(64),
    testId            character varying(64),
    tenantId          character varying(64),
    anomalyType       character varying(64),
    description       character varying(500),
    referenceId       character varying(64),
    resolutionStatus  character varying(64),
    additionalDetails json,
    isActive          boolean,
    createdBy         character varying(64),
    lastModifiedBy    character varying(64),
    createdTime       bigint,
    lastModifiedTime  bigint
);
