ALTER TABLE grbg_collection_unit
ADD COLUMN is_active BOOLEAN;


ALTER TABLE hpudd_grbg_account
ADD COLUMN uuid VARCHAR(256);
