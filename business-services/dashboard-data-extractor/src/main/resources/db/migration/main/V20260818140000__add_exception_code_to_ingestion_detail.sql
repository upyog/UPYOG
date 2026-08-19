-- For ingestion_detail
ALTER TABLE ingestion_detail 
    ADD COLUMN IF NOT EXISTS exception_code VARCHAR(128);

COMMENT ON COLUMN ingestion_detail.exception_code IS 
    'Short error/exception code captured when ingestion_status = FAILURE.';

-- For legacy_data_ingestion_detail
ALTER TABLE legacy_data_ingestion_detail 
    ADD COLUMN IF NOT EXISTS exception_code VARCHAR(128);

COMMENT ON COLUMN legacy_data_ingestion_detail.exception_code IS 
    'Short error/exception code captured when ingestion_status = FAILURE.';