ALTER TABLE eg_fsm_application ADD COLUMN IF NOT EXISTS paymentpreference character varying(64);
ALTER TABLE eg_fsm_application_auditlog  ADD COLUMN IF NOT EXISTS paymentpreference character varying(64) ;
