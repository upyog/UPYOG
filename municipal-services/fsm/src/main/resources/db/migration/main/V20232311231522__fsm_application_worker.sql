CREATE TABLE IF NOT EXISTS eg_fsm_application_worker
(
    id                character varying(256) NOT NULL,
    tenantid          character varying(64),
    application_id    character varying(64)  NOT NULL,
    individual_id     character varying(64)  NOT NULL,
    workerType        character varying(64),
    status            character varying(64),
    additionaldetails jsonb,
    createdby         character varying(64),
    lastmodifiedby    character varying(64),
    createdtime       bigint,
    lastmodifiedtime  bigint,
    CONSTRAINT pk_eg_fsm_application_worker PRIMARY KEY (id),
    CONSTRAINT fk_eg_fsm_application_worker FOREIGN KEY (application_id) REFERENCES eg_fsm_application (id)
);

CREATE INDEX IF NOT EXISTS idx_application_eg_fsm_application_worker ON eg_fsm_application_worker (application_id);
CREATE INDEX IF NOT EXISTS idx_worker_eg_fsm_application_worker ON eg_fsm_application_worker (individual_id);
