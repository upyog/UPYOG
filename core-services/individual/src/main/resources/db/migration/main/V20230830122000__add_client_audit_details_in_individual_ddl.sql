ALTER TABLE INDIVIDUAL ADD COLUMN IF NOT EXISTS clientCreatedBy character varying(64);
ALTER TABLE INDIVIDUAL ADD COLUMN IF NOT EXISTS clientLastModifiedBy character varying(64);
