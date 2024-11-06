--ALTER TABLE eg_asset_auditdetails ADD COLUMN remarks character varying(256);
ALTER TABLE eg_asset_auditdetails ALTER COLUMN additionaldetails TYPE JSONB;
ALTER TABLE eg_asset_addressdetails ALTER COLUMN locality_latitude TYPE VARCHAR(64);
ALTER TABLE eg_asset_addressdetails ALTER COLUMN locality_longitude TYPE VARCHAR(64);

