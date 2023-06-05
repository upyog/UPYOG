ALTER TABLE eg_birth_present_address DROP COLUMN IF EXISTS assignee;
ALTER TABLE eg_birth_present_address ADD COLUMN  assignee jsonb;

ALTER TABLE eg_birth_present_address_audit DROP COLUMN IF EXISTS assignee;
ALTER TABLE eg_birth_present_address_audit ADD COLUMN assignee  jsonb;

ALTER TABLE eg_birth_permanent_address DROP COLUMN IF EXISTS assignee;
ALTER TABLE eg_birth_permanent_address ADD COLUMN assignee  jsonb;

ALTER TABLE eg_birth_permanent_address_audit DROP COLUMN IF EXISTS assignee;
ALTER TABLE eg_birth_permanent_address_audit ADD COLUMN  assignee  jsonb;