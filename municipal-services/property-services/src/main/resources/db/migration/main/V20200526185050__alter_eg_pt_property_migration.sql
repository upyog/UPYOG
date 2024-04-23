ALTER TABLE eg_pt_property_migration ADD COLUMN IF NOT EXISTS tenantid CHARACTER VARYING (256) NOT NULL;
ALTER TABLE eg_pt_property_migration ADD COLUMN IF NOT EXISTS recordCount bigint NOT NULL;