-- PQM Database Schema V1

CREATE TABLE IF NOT EXISTS eg_pqm_test_criteria_results
(
    id           character varying(64),
    testId       character varying(64),
    criteriaCode character varying(255),
    resultValue bigint,
    resultStatus character varying(64),
    isActive     boolean,
    createdBy         character varying(64),
    lastModifiedBy    character varying(64),
    createdTime       bigint,
    lastModifiedTime  bigint,
    CONSTRAINT pk_pqm_test_criteria_result PRIMARY KEY (id),
    CONSTRAINT fk_eg_pqm_test_criteria_results_tests FOREIGN KEY (testId) REFERENCES eg_pqm_tests (id)
);


CREATE INDEX IF NOT EXISTS index_criteriaCode_eg_pqm_test_criteria_results ON eg_pqm_test_criteria_results (criteriaCode);

CREATE TABLE IF NOT EXISTS eg_pqm_test_criteria_results_auditlog
(
    id           character varying(64),
    testId       character varying(64),
    criteriaCode character varying(255),
    resultValue bigint,
    resultStatus character varying(64),
    isActive     boolean,
    createdBy         character varying(64),
    lastModifiedBy    character varying(64),
    createdTime       bigint,
    lastModifiedTime  bigint
);


CREATE INDEX IF NOT EXISTS index_criteriaCode_eg_pqm_test_criteria_results_auditlog ON eg_pqm_test_criteria_results_auditlog (criteriaCode);

ALTER TABLE eg_pqm_tests DROP COLUMN qualityCriteria;
ALTER TABLE eg_pqm_tests_auditlog DROP COLUMN qualityCriteria;
