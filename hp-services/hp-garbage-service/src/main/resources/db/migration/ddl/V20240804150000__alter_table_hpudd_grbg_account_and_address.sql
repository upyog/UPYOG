ALTER TABLE hpudd_grbg_account ADD COLUMN gender VARCHAR(100);
ALTER TABLE hpudd_grbg_account ADD COLUMN email_id VARCHAR(100);
ALTER TABLE hpudd_grbg_account ADD COLUMN additional_detail JSONB;

ALTER TABLE grbg_address ADD COLUMN zone VARCHAR(100);
ALTER TABLE grbg_address ADD COLUMN ulb_name VARCHAR(100);
ALTER TABLE grbg_address ADD COLUMN ulb_type VARCHAR(100);
ALTER TABLE grbg_address ADD COLUMN ward_name VARCHAR(100);
ALTER TABLE grbg_address ADD COLUMN additional_detail JSONB;
