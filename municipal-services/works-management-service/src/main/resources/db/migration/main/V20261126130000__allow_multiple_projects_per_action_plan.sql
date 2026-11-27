
DROP INDEX IF EXISTS uk_eg_wms_ap_pd_one;

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_pd_actionplan_active
    ON eg_wms_ap_project_detail (actionplanid)
    WHERE isactive = TRUE;
