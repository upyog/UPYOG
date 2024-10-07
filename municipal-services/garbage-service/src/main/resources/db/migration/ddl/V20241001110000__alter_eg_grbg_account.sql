
ALTER TABLE eg_grbg_account ADD COLUMN parent_account VARCHAR(255);
ALTER TABLE eg_grbg_account ADD COLUMN is_active BOOLEAN;
ALTER TABLE eg_grbg_account ADD COLUMN sub_account_count INT8 DEFAULT 0;
