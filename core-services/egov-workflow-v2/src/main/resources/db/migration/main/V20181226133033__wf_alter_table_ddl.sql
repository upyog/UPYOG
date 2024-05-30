--ALTER TABLE eg_wf_processinstance_v2
-- RENAME COLUMN sla TO stateSla;

-- ALTER TABLE eg_wf_processinstance_v2 ADD COLUMN IF NOT EXISTS moduleName character varying(64);

ALTER TABLE eg_wf_processinstance_v2 ADD COLUMN IF NOT EXISTS businessServiceSla bigint;

ALTER TABLE eg_wf_businessservice_v2 ADD COLUMN IF NOT EXISTS businessServiceSla bigint;


