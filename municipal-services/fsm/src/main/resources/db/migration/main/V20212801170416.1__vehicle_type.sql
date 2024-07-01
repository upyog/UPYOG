
ALTER TABLE eg_fsm_pit_detail_auditlog DROP COLUMN IF EXISTS vehicletype ;
ALTER TABLE eg_fsm_application_auditlog ADD COLUMN IF NOT EXISTS vehicletype character varying(64) ;
