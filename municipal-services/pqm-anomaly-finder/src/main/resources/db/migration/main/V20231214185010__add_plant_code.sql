ALTER TABLE eg_pqm_anomaly_details
    ADD COLUMN IF NOT EXISTS plantCode character varying(255);
ALTER TABLE eg_pqm_anomaly_details_auditlog
    ADD COLUMN IF NOT EXISTS plantCode character varying(255);