-- Add 'asset_maintenance_date' column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_maintenance'
          AND column_name = 'asset_maintenance_date'
    ) THEN
        ALTER TABLE public.eg_asset_maintenance
        ADD COLUMN asset_maintenance_date BIGINT;
    END IF;
END $$;

-- Add 'asset_next_maintenance_date' column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_maintenance'
          AND column_name = 'asset_next_maintenance_date'
    ) THEN
        ALTER TABLE public.eg_asset_maintenance
        ADD COLUMN asset_next_maintenance_date BIGINT;
    END IF;
END $$;

-- Add 'tenant_id' column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_maintenance'
          AND column_name = 'tenant_id'
    ) THEN
        ALTER TABLE public.eg_asset_maintenance
        ADD COLUMN tenant_id CHARACTER VARYING(64);
    END IF;
END $$;

-- Add 'asset_maintenance_status' column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'eg_asset_maintenance'
          AND column_name = 'asset_maintenance_status'
    ) THEN
        ALTER TABLE public.eg_asset_maintenance
        ADD COLUMN asset_maintenance_status CHARACTER VARYING(255);
    END IF;
END $$;
