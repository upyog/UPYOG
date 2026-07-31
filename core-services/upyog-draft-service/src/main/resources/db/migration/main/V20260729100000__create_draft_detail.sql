CREATE TABLE ug_draft_detail (
    draft_id           VARCHAR(64)  PRIMARY KEY,
    tenant_id          VARCHAR(64)  NOT NULL,
    user_uuid          VARCHAR(64)  NOT NULL,
    business_service   VARCHAR(64)  NOT NULL,
    module_entity_id   VARCHAR(64),
    draft_data         JSONB        NOT NULL,
    completion_pct     NUMERIC(5,2) DEFAULT 0,
    status             VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    createdby          VARCHAR(64)  NOT NULL,
    lastmodifiedby     VARCHAR(64),
    createdtime        BIGINT       NOT NULL,
    lastmodifiedtime   BIGINT,
    CONSTRAINT uq_user_module_draft UNIQUE (tenant_id, user_uuid, business_service, draft_id)
);

CREATE INDEX idx_draft_user_tenant ON ug_draft_detail (tenant_id, user_uuid, status, lastmodifiedtime DESC);
CREATE INDEX idx_draft_business_service ON ug_draft_detail (business_service, status);
