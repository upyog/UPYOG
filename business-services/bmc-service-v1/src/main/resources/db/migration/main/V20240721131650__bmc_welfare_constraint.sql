ALTER TABLE IF EXISTS eg_bmc_userotherdetails DROP CONSTRAINT IF EXISTS uni_user_tenant;
ALTER TABLE IF EXISTS eg_bmc_userotherdetails
    ADD CONSTRAINT uni_user_tenant UNIQUE (userid, tenantid);

ALTER TABLE IF EXISTS eg_bmc_aadharuser DROP CONSTRAINT IF EXISTS uni_aadharuser;
ALTER TABLE IF EXISTS eg_bmc_aadharuser
    ADD CONSTRAINT uni_aadharuser UNIQUE (userid, tenantid);