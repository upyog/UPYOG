ALTER TABLE IF EXISTS eg_bmc_userschemeapplication  DROP COLUMN IF EXISTS agreetopay;
ALTER TABLE IF EXISTS eg_bmc_userschemeapplication
    ADD COLUMN agreetopay boolean;
    
ALTER TABLE IF EXISTS eg_bmc_userschemeapplication  DROP COLUMN IF EXISTS statement;
ALTER TABLE IF EXISTS eg_bmc_userschemeapplication
    ADD COLUMN statement boolean;
    
ALTER TABLE IF EXISTS eg_bmc_userotherdetails  DROP COLUMN IF EXISTS occupation;
ALTER TABLE IF EXISTS eg_bmc_userotherdetails
    ADD COLUMN occupation varchar(255) NULL;
    
ALTER TABLE IF EXISTS eg_bmc_userotherdetails  DROP COLUMN IF EXISTS income;
ALTER TABLE IF EXISTS eg_bmc_userotherdetails
    ADD COLUMN income float8 NULL;            
