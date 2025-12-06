-- Rename column in the main table
DO $$ 
BEGIN 
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'eq_plant_user_map' AND column_name = 'individualid') THEN
        EXECUTE 'ALTER TABLE eq_plant_user_map RENAME COLUMN individualid TO plantOperatorUuid'; 
    END IF;
END $$;

-- Rename column in the audit log table (assuming it exists)
DO $$ 
BEGIN 
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'eq_plant_user_map_auditlog' AND column_name = 'individualid') THEN
        EXECUTE 'ALTER TABLE eq_plant_user_map_auditlog RENAME COLUMN individualid TO plantOperatorUuid'; 
    END IF;
END $$;
