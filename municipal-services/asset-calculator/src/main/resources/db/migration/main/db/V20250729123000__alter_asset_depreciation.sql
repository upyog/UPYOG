
ALTER TABLE public.eg_asset_depreciation_details
    ADD COLUMN IF NOT EXISTS depreciation_method character varying(50) ;
