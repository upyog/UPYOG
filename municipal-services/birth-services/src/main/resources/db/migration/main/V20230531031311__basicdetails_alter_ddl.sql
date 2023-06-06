ALTER TABLE eg_birth_details DROP COLUMN IF EXISTS assignee;
ALTER TABLE eg_birth_details ADD COLUMN  IF NOT EXISTS assignee jsonb;

ALTER TABLE eg_birth_details_audit DROP COLUMN IF EXISTS assignee;
ALTER TABLE eg_birth_details_audit ADD COLUMN IF NOT EXISTS assignee  jsonb;