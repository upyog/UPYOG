-- BEGIN TRANSACTION for adding petRegistrationNumber column
BEGIN;

-- Add petregistrationnumber column to eg_ptr_registration table (lowercase for PostgreSQL compatibility)
ALTER TABLE eg_ptr_registration 
ADD COLUMN IF NOT EXISTS petregistrationnumber character varying(64);

-- COMMIT all changes if everything succeeds
COMMIT;

