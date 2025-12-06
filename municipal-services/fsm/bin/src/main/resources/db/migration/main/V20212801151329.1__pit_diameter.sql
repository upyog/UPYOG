ALTER TABLE eg_fsm_pit_detail ADD COLUMN IF NOT EXISTS diameter character varying(64);
ALTER TABLE eg_fsm_pit_detail_auditlog ADD COLUMN IF NOT EXISTS diameter character varying(64) ;
