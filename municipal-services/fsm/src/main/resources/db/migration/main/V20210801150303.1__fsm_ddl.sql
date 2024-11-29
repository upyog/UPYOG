ALTER TABLE eg_fsm_pit_detail
	ADD COLUMN IF NOT EXISTS fsm_id character varying(64) NOT NULL,
	DROP COLUMN IF EXISTS fms_id;
	
	ALTER TABLE eg_fsm_pit_detail_auditlog
	ADD COLUMN IF NOT EXISTS fsm_id character varying(64) NOT NULL;
