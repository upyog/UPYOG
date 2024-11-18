ALTER TABLE eg_asset_assetdetails ADD COLUMN financialYear character varying(64);
ALTER TABLE eg_asset_auditdetails ADD COLUMN financialYear character varying(64);
ALTER TABLE eg_asset_assetdetails ADD COLUMN sourceOfFinance character varying(256);
ALTER TABLE eg_asset_auditdetails ADD COLUMN sourceOfFinance character varying(256);