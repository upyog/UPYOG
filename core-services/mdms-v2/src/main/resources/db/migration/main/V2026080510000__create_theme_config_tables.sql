CREATE TABLE IF NOT EXISTS ug_theme_config  (
    id UUID PRIMARY KEY,
    tenantid VARCHAR(256) NOT NULL,
    themetype VARCHAR(100) NOT NULL,
    config JSONB NOT NULL,

    isactive BOOLEAN DEFAULT TRUE,

    -- DEFAULT, PENDING, APPROVED, REJECTED, EXPIRED
    status VARCHAR(50) NOT NULL,

    workflowid VARCHAR(256),

    createdby VARCHAR(256),
    createdtime BIGINT,

    lastmodifiedby VARCHAR(256),
    lastmodifiedtime BIGINT
);


-- Allow multiple versions of same theme.
-- Only one live APPROVED theme should exist.
CREATE INDEX IF NOT EXISTS idx_theme_config_search
ON ug_theme_config(
    tenantid,
    themetype,
    status,
    isactive
);


-- Prevent multiple active approved themes.
CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_active_approved_theme
ON ug_theme_config(
    tenantid,
    themetype
)
WHERE status='APPROVED'
AND isactive=true;

CREATE TABLE IF NOT EXISTS ug_theme_config_audit (
    id UUID PRIMARY KEY,
    tenantid VARCHAR(256) NOT NULL,
    themetype VARCHAR(100) NOT NULL,

    config JSONB NOT NULL,

    isactive BOOLEAN DEFAULT TRUE,

    status VARCHAR(50) NOT NULL,

    workflowid VARCHAR(256),

    createdby VARCHAR(256),
    createdtime BIGINT,

    lastmodifiedby VARCHAR(256),
    lastmodifiedtime BIGINT,

    operation VARCHAR(50) NOT NULL
);