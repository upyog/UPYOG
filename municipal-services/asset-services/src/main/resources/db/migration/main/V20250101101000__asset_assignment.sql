DO $$
BEGIN
    -- Check if the unique constraint already exists
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE constraint_type = 'UNIQUE'
          AND table_name = 'eg_asset_assignmentdetails'
          AND constraint_name = 'unique_assetid'
    ) THEN
        -- Create the unique constraint if it does not exist
        ALTER TABLE eg_asset_assignmentdetails
        ADD CONSTRAINT unique_assetid UNIQUE (assetid);
    END IF;
END $$;