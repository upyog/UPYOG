DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_depreciation_details'
          AND column_name = 'depreciation_method'
    ) THEN
ALTER TABLE public.eg_asset_depreciation_details
    ADD COLUMN depreciation_method character varying(50) COLLATE pg_catalog."default";
END IF;
END $$;
