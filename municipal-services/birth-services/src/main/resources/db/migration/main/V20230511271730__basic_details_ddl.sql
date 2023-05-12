ALTER TABLE eg_birth_details ADD COLUMN IF NOT EXISTS  birthdate date;
ALTER TABLE eg_birth_details_audit ADD COLUMN IF NOT EXISTS  birthdate date;

ALTER TABLE eg_register_birth_details ADD COLUMN IF NOT EXISTS  birthdate date;
ALTER TABLE eg_register_birth_details_audit ADD COLUMN IF NOT EXISTS  birthdate date;

