CREATE TABLE IF NOT EXISTS ug_draft_detail (
    draft_id           VARCHAR(64)  PRIMARY KEY,
    tenant_id          VARCHAR(64)  NOT NULL,
    business_service   VARCHAR(64)  NOT NULL,
    module_name        VARCHAR(64),
    module_entity_id   VARCHAR(64),
    creator_type       VARCHAR(32)  NOT NULL DEFAULT 'USER',
    draft_data         JSONB        NOT NULL,
    completion_pct     NUMERIC(5,2) DEFAULT 0,
    status             VARCHAR(32)  NOT NULL DEFAULT 'ACTIVE',
    createdby          VARCHAR(64)  NOT NULL,
    lastmodifiedby     VARCHAR(64),
    createdtime        BIGINT       NOT NULL,
    lastmodifiedtime   BIGINT,
    CONSTRAINT uq_user_module_draft UNIQUE (tenant_id, business_service, draft_id)
);

CREATE INDEX IF NOT EXISTS idx_draft_creator_tenant ON ug_draft_detail (tenant_id, createdby, status, lastmodifiedtime DESC);
CREATE INDEX IF NOT EXISTS idx_draft_business_service ON ug_draft_detail (business_service, status);
CREATE INDEX IF NOT EXISTS idx_draft_module_service ON ug_draft_detail (module_name, business_service, status);

CREATE TABLE IF NOT EXISTS shedlock (
    name       VARCHAR(64)  NOT NULL PRIMARY KEY,
    lock_until TIMESTAMP    NOT NULL,
    locked_at  TIMESTAMP    NOT NULL,
    locked_by  VARCHAR(255) NOT NULL
);
