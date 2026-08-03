-- Add allotment_no, due_date and status columns to ug_em_allotment_details table
ALTER TABLE ug_em_allotment_details ADD COLUMN IF NOT EXISTS allotment_no VARCHAR(64);
ALTER TABLE ug_em_allotment_details ADD COLUMN IF NOT EXISTS due_date DATE;
ALTER TABLE ug_em_allotment_details ADD COLUMN IF NOT EXISTS status VARCHAR(50);

-- Add allotment_no column to ug_em_monthly_rent_payment table
ALTER TABLE ug_em_monthly_rent_payment ADD COLUMN IF NOT EXISTS allotment_no VARCHAR(64);

-- Create sequence for estate management allotment number IDGen
CREATE SEQUENCE IF NOT EXISTS seq_estate_management_allotment_no;
