ALTER TABLE eg_wms_ap_component
    ADD COLUMN IF NOT EXISTS projectdetailid VARCHAR(64);


ALTER TABLE eg_wms_ap_component
    ADD CONSTRAINT fk_eg_wms_ap_component_project_detail
    FOREIGN KEY (projectdetailid) REFERENCES eg_wms_ap_project_detail (id);

CREATE INDEX IF NOT EXISTS idx_eg_wms_ap_component_project_detail
    ON eg_wms_ap_component (projectdetailid);

ALTER TABLE eg_wms_ap_component_audit
    ADD COLUMN IF NOT EXISTS projectdetailid VARCHAR(64);
