ALTER TABLE eg_fsm_application ADD COLUMN IF NOT EXISTS applicationtype character varying(256) DEFAULT 'ADHOC' NOT NULL;

ALTER TABLE eg_fsm_application_auditlog ADD COLUMN IF NOT EXISTS applicationtype character varying(256) DEFAULT 'ADHOC' NOT NULL;
	
ALTER TABLE eg_fsm_application ADD COLUMN IF NOT EXISTS oldapplicationno character varying(256) DEFAULT NULL;

ALTER TABLE eg_fsm_application_auditlog ADD COLUMN IF NOT EXISTS oldapplicationno character varying(256) DEFAULT NULL;
