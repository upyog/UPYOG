ALTER TABLE grbg_collection_unit ADD COLUMN garbage_id INT8;
ALTER TABLE grbg_collection_unit ADD COLUMN unit_type VARCHAR(100);
ALTER TABLE grbg_collection_unit ADD COLUMN category VARCHAR(100);
ALTER TABLE grbg_collection_unit ADD COLUMN sub_category VARCHAR(100);
ALTER TABLE grbg_collection_unit ADD COLUMN sub_category_type VARCHAR(100);

ALTER TABLE grbg_address ADD COLUMN garbage_id INT8;