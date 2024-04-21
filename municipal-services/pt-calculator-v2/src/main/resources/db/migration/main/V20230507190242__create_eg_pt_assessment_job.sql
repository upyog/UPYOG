CREATE TABLE IF NOT EXISTS  eg_pt_assessment_job
(
    uuid character varying(64) NOT NULL,
    locality character varying(64),
    financialyear character varying(64),
    createdtime bigint,
    status character varying(64),
    tenantid character varying(64) NOT NULL,
    assessmentstobegenerated int,
    successfulAssessments int,
    failedAssessments int,
    additionaldetails character varying(255),
    error character varying(255),
    CONSTRAINT pk_eg_pt_assessment_job PRIMARY KEY (uuid)
);
