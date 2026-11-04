-- Add column only if it doesn't already exist
ALTER TABLE IF EXISTS scheme
ADD COLUMN IF NOT EXISTS statecode CHAR(12);

-- Safely expand the 'name' column length to 255 characters
ALTER TABLE IF EXISTS scheme
ALTER COLUMN name TYPE VARCHAR(255);