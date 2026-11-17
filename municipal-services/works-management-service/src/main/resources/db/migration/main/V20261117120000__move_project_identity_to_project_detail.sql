ALTER TABLE eg_wms_ap_project_detail
    ADD COLUMN IF NOT EXISTS projectid VARCHAR(12),
    ADD COLUMN IF NOT EXISTS projectname VARCHAR(256);

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_pd_tenant_project
    ON eg_wms_ap_project_detail (tenantid, projectid)
    WHERE isactive = TRUE;

ALTER TABLE eg_wms_ap_project_detail_audit
    ADD COLUMN IF NOT EXISTS projectid VARCHAR(12),
    ADD COLUMN IF NOT EXISTS projectname VARCHAR(256);

ALTER TABLE eg_wms_action_plan
    DROP CONSTRAINT IF EXISTS uk_eg_wms_ap_project_version;

ALTER TABLE eg_wms_action_plan
    DROP COLUMN IF EXISTS projectid,
    DROP COLUMN IF EXISTS projectname;

ALTER TABLE eg_wms_action_plan_audit
    DROP COLUMN IF EXISTS projectid,
    DROP COLUMN IF EXISTS projectname;
