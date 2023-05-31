ALTER TABLE eg_birth_details ALTER COLUMN  assignee  TYPE jsonb USING assignee::jsonb;
ALTER TABLE eg_birth_details_audit ALTER COLUMN   assignee  TYPE jsonb USING assignee::jsonb;