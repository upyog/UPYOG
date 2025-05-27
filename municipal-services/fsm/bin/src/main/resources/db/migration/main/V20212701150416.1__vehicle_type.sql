ALTER TABLE eg_fsm_application ADD COLUMN IF NOT EXISTS vehicletype character varying(64);
ALTER TABLE eg_fsm_pit_detail_auditlog ADD COLUMN IF NOT EXISTS vehicletype character varying(64) ;
