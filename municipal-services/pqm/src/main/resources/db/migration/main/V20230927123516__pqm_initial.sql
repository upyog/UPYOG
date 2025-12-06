-- PQM Database Schema V1

CREATE TABLE IF NOT EXISTS eg_pqm_tests
(
    id                character varying(64),
    tenantId          character varying(64),
    plantCode           character varying(255),
    processCode         character varying(255),
    stageCode           character varying(255),
    materialCode        character varying(255),
    deviceCode          character varying(255),
    testType              character varying(64),
    status            character varying(64),
    wfStatus            character varying(64),
    scheduledDate     bigint,
    qualityCriteria json not null,
    additionalDetails json,
    isActive          boolean,
    createdBy         character varying(64),
    lastModifiedBy    character varying(64),
    createdTime       bigint,
    lastModifiedTime  bigint,
    CONSTRAINT pk_pqm_tests PRIMARY KEY (id)
);

  CREATE INDEX  IF NOT EXISTS  index_id_eg_pqm  ON eg_pqm_tests
(    id
);

  CREATE INDEX  IF NOT EXISTS  index_tenantId_eg_pqm  ON eg_pqm_tests
(    tenantId
);

  CREATE INDEX  IF NOT EXISTS  index_plantCode_eg_pqm  ON eg_pqm_tests
(    plantCode
);

  CREATE INDEX  IF NOT EXISTS  index_processCode_eg_pqm  ON eg_pqm_tests
(    processCode
);

  CREATE INDEX  IF NOT EXISTS  index_stageCode_eg_pqm  ON eg_pqm_tests
(    stageCode
);

  CREATE INDEX  IF NOT EXISTS  index_materialCode_eg_pqm  ON eg_pqm_tests
(    materialCode
);

  CREATE INDEX  IF NOT EXISTS  index_deviceCode_eg_pqm  ON eg_pqm_tests
(    deviceCode
);

  CREATE INDEX  IF NOT EXISTS  index_testType_eg_pqm  ON eg_pqm_tests
(    testType
);

  CREATE INDEX  IF NOT EXISTS  index_status_eg_pqm  ON eg_pqm_tests
(    status
);

  CREATE INDEX  IF NOT EXISTS  index_wfStatus_eg_pqm  ON eg_pqm_tests
(    wfStatus
);

CREATE TABLE IF NOT EXISTS eg_pqm_test_result_documents
(
    id               character varying(64),
    testId           character varying(64),
    documentUid      character varying(64),
    documentUri      character varying(2084),
    documentType     character varying(64),
    filestoreId      character varying(250),
    isActive         boolean,
    additionalDetails json,
    createdBy        character varying(64),
    lastModifiedBy   character varying(64),
    createdTime      bigint,
    lastModifiedTime bigint,
    CONSTRAINT pk_pqm_test_result_documents PRIMARY KEY (id),
    CONSTRAINT fk_documents_pqm_test_results FOREIGN KEY (testId) REFERENCES eg_pqm_tests (id)
        ON DELETE CASCADE
);

CREATE INDEX  IF NOT EXISTS  index_id_eg_pqm_test_result_documents  ON eg_pqm_test_result_documents
(    id
);

CREATE INDEX  IF NOT EXISTS  index_testId_eg_pqm_test_result_documents  ON eg_pqm_test_result_documents
(    testId
);

CREATE TABLE IF NOT EXISTS eg_pqm_tests_auditlog
(
    id                character varying(64),
    tenantId          character varying(64),
    plantCode           character varying(255),
    processCode         character varying(255),
    stageCode           character varying(255),
    materialCode        character varying(255),
    deviceCode          character varying(255),
    testType              character varying(64),
    status            character varying(64),
    wfStatus            character varying(64),
    scheduledDate     bigint,
    qualityCriteria json not null,
    additionalDetails json,
    isActive          boolean,
    createdBy         character varying(64),
    lastModifiedBy    character varying(64),
    createdTime       bigint,
    lastModifiedTime  bigint
);

CREATE INDEX  IF NOT EXISTS  index_id_eg_pqm_tests_auditlog  ON eg_pqm_tests_auditlog
(
  id

);

CREATE TABLE IF NOT EXISTS eg_pqm_test_result_documents_auditlog
(
    id               character varying(64),
    testId           character varying(64),
    documentUid      character varying(64),
    documentUri      character varying(2084),
    documentType     character varying(64),
    filestoreId      character varying(250),
    additionalDetails json,
    isActive         boolean,
    createdBy        character varying(64),
    lastModifiedBy   character varying(64),
    createdTime      bigint,
    lastModifiedTime bigint
    
);

CREATE INDEX  IF NOT EXISTS  index_id_eg_pqm_test_result_documents_auditlog  ON eg_pqm_test_result_documents_auditlog
(
  id

);