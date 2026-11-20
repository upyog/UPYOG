ALTER TABLE eg_wms_ap_project_detail
    ADD COLUMN IF NOT EXISTS approvedamount NUMERIC(15,2);

ALTER TABLE eg_wms_ap_project_detail_audit
    ADD COLUMN IF NOT EXISTS approvedamount NUMERIC(15,2);
