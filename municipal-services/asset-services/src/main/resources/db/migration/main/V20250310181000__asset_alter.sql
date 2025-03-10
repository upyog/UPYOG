DO $$
BEGIN
    -- For table eg_asset_assetdetails

    -- Check and add 'assetAssignable' column
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_assetdetails'
          AND column_name = 'assetAssignable'
    ) THEN
        ALTER TABLE eg_asset_assetdetails ADD COLUMN assetAssignable BOOLEAN DEFAULT false;
    END IF;

    -- For table eg_asset_auditdetails

    -- Check and add 'assetAssignable' column
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_auditdetails'
          AND column_name = 'assetAssignable'
    ) THEN
        ALTER TABLE eg_asset_auditdetails ADD COLUMN assetAssignable BOOLEAN DEFAULT false;
    END IF;

END $$;

