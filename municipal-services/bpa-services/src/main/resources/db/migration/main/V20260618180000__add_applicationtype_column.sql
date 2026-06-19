ALTER TABLE eg_bpa_buildingplan ADD COLUMN IF NOT EXISTS applicationtype character varying(64);
ALTER TABLE eg_bpa_auditdetails ADD COLUMN IF NOT EXISTS applicationtype character varying(64);
