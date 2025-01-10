-- Add 'asset_disposal_status' column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_disposal_details'
          AND column_name = 'asset_disposal_status'
    ) THEN
        ALTER TABLE eg_asset_disposal_details
        ADD COLUMN asset_disposal_status VARCHAR(50);
    END IF;
END $$;

-- Add 'additional_details' column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_disposal_details'
          AND column_name = 'additional_details'
    ) THEN
        ALTER TABLE eg_asset_disposal_details
        ADD COLUMN additional_details JSONB;
    END IF;
END $$;
