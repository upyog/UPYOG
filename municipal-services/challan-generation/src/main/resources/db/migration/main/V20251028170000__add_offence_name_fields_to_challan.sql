
-- V20251028170000__add_offence_name_fields_to_challan.sql

DO $$ 
BEGIN
    -- Add offence_type_name column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'eg_challan' AND column_name = 'offence_type_name') THEN
        ALTER TABLE eg_challan ADD COLUMN offence_type_name VARCHAR(100);
    END IF;
    
    -- Add offence_category_name column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'eg_challan' AND column_name = 'offence_category_name') THEN
        ALTER TABLE eg_challan ADD COLUMN offence_category_name VARCHAR(100);
    END IF;
    
    -- Add offence_subcategory_name column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'eg_challan' AND column_name = 'offence_subcategory_name') THEN
        ALTER TABLE eg_challan ADD COLUMN offence_subcategory_name VARCHAR(100);
    END IF;
    
    -- Add challan_amount column if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'eg_challan' AND column_name = 'challan_amount') THEN
        ALTER TABLE eg_challan ADD COLUMN challan_amount DECIMAL(10,2);
    END IF;
END $$;

-- Add indexes for better performance (only if they don't exist)
DO $$ 
BEGIN
    -- Create offence_type_name index if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_eg_challan_offence_type_name') THEN
        CREATE INDEX idx_eg_challan_offence_type_name ON eg_challan(offence_type_name);
    END IF;
    
    -- Create offence_category_name index if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_eg_challan_offence_category_name') THEN
        CREATE INDEX idx_eg_challan_offence_category_name ON eg_challan(offence_category_name);
    END IF;
    
    -- Create offence_subcategory_name index if it doesn't exist
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_eg_challan_offence_subcategory_name') THEN
        CREATE INDEX idx_eg_challan_offence_subcategory_name ON eg_challan(offence_subcategory_name);
    END IF;
END $$;

-- Add comments for documentation
COMMENT ON COLUMN eg_challan.offence_type_name IS 'User-friendly offence type name';
COMMENT ON COLUMN eg_challan.offence_category_name IS 'User-friendly offence category name';
COMMENT ON COLUMN eg_challan.offence_subcategory_name IS 'User-friendly offence subcategory name';
COMMENT ON COLUMN eg_challan.challan_amount IS 'Amount entered by user';
