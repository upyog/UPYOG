-- Create ENUM types
--CREATE TYPE public.asset_classification AS ENUM ('MOVABLE', 'IMMOVABLE');
--CREATE TYPE public.parent_type AS ENUM ('LAND', 'BUILDING', 'SERVICE','OTHER');

--ALTER TABLE eg_asset_assetdetails DROP COLUMN action;

--ALTER TABLE eg_asset_auditdetails DROP COLUMN action;

--ALTER TABLE eg_asset_auditdetails ADD COLUMN remarks character varying(256);
ALTER TABLE eg_asset_assetdetails ADD COLUMN financialYear character varying(64);
ALTER TABLE eg_asset_auditdetails ADD COLUMN financialYear character varying(64);
ALTER TABLE eg_asset_assetdetails ADD COLUMN sourceOfFinance character varying(256);
ALTER TABLE eg_asset_auditdetails ADD COLUMN sourceOfFinance character varying(256);