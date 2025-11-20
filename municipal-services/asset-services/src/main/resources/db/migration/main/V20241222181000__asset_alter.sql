--ALTER TABLE eg_asset_assetdetails ADD COLUMN islegacydata BOOLEAN DEFAULT false;
--ALTER TABLE eg_asset_assetdetails ADD COLUMN minimumvalue DOUBLE PRECISION DEFAULT 0.0;
--ALTER TABLE eg_asset_assetdetails ADD COLUMN originalbookvalue BIGINT;
DO $$
BEGIN
    -- For table eg_asset_assetdetails

    -- Check and add 'islegacydata' column
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_assetdetails'
          AND column_name = 'islegacydata'
    ) THEN
        ALTER TABLE eg_asset_assetdetails ADD COLUMN islegacydata BOOLEAN DEFAULT false;
    END IF;

    -- Check and add 'minimumvalue' column
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_assetdetails'
          AND column_name = 'minimumvalue'
    ) THEN
        ALTER TABLE eg_asset_assetdetails ADD COLUMN minimumvalue DOUBLE PRECISION DEFAULT 0.0;
    END IF;

    -- Check and add 'originalbookvalue' column
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_assetdetails'
          AND column_name = 'originalbookvalue'
    ) THEN
        ALTER TABLE eg_asset_assetdetails ADD COLUMN originalbookvalue BIGINT;
    END IF;

    -- For table eg_asset_auditdetails

    -- Check and add 'islegacydata' column
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_auditdetails'
          AND column_name = 'islegacydata'
    ) THEN
        ALTER TABLE eg_asset_auditdetails ADD COLUMN islegacydata BOOLEAN DEFAULT false;
    END IF;

    -- Check and add 'minimumvalue' column
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_auditdetails'
          AND column_name = 'minimumvalue'
    ) THEN
        ALTER TABLE eg_asset_auditdetails ADD COLUMN minimumvalue DOUBLE PRECISION DEFAULT 0.0;
    END IF;

    -- Check and add 'originalbookvalue' column
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_auditdetails'
          AND column_name = 'originalbookvalue'
    ) THEN
        ALTER TABLE eg_asset_auditdetails ADD COLUMN originalbookvalue BIGINT;
    END IF;
END $$;

