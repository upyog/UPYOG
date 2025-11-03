-- Add division and district columns to eg_asset_assetdetails table

ALTER TABLE public.eg_asset_assetdetails 
ADD COLUMN IF NOT EXISTS division character varying(256) COLLATE pg_catalog."default";

ALTER TABLE public.eg_asset_assetdetails 
ADD COLUMN IF NOT EXISTS district character varying(256) COLLATE pg_catalog."default";

COMMENT ON COLUMN public.eg_asset_assetdetails.division IS 'Division information from MDMS';
COMMENT ON COLUMN public.eg_asset_assetdetails.district IS 'District information from MDMS';