-- Add allotment_no column to eg_est_scheduler_log table
ALTER TABLE eg_est_scheduler_log ADD COLUMN IF NOT EXISTS allotment_no VARCHAR(64);
