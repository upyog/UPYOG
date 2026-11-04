-- V20251030002811__alter_offence_columns_size.sql
-- Alter offence type, category, and subcategory column sizes from VARCHAR(100) to VARCHAR(500)

DO $$ 
BEGIN
    -- Alter offence_type_name column size from 100 to 500
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'eg_challan' 
               AND column_name = 'offence_type_name'
               AND character_maximum_length = 100) THEN
        ALTER TABLE eg_challan ALTER COLUMN offence_type_name TYPE VARCHAR(500);
        RAISE NOTICE 'Altered offence_type_name column size from 100 to 500';
    ELSE
        RAISE NOTICE 'offence_type_name column does not exist or already has different size';
    END IF;
    
    -- Alter offence_category_name column size from 100 to 500
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'eg_challan' 
               AND column_name = 'offence_category_name'
               AND character_maximum_length = 100) THEN
        ALTER TABLE eg_challan ALTER COLUMN offence_category_name TYPE VARCHAR(500);
        RAISE NOTICE 'Altered offence_category_name column size from 100 to 500';
    ELSE
        RAISE NOTICE 'offence_category_name column does not exist or already has different size';
    END IF;
    
    -- Alter offence_subcategory_name column size from 100 to 500
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'eg_challan' 
               AND column_name = 'offence_subcategory_name'
               AND character_maximum_length = 100) THEN
        ALTER TABLE eg_challan ALTER COLUMN offence_subcategory_name TYPE VARCHAR(500);
        RAISE NOTICE 'Altered offence_subcategory_name column size from 100 to 500';
    ELSE
        RAISE NOTICE 'offence_subcategory_name column does not exist or already has different size';
    END IF;
END $$;

-- Update column comments
COMMENT ON COLUMN eg_challan.offence_type_name IS 'User-friendly offence type name (VARCHAR 500)';
COMMENT ON COLUMN eg_challan.offence_category_name IS 'User-friendly offence category name (VARCHAR 500)';
COMMENT ON COLUMN eg_challan.offence_subcategory_name IS 'User-friendly offence subcategory name (VARCHAR 500)';
