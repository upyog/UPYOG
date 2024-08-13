ALTER TABLE IF EXISTS eg_bmc_userqualification DROP CONSTRAINT IF EXISTS uni_user_qualification;
ALTER TABLE IF EXISTS eg_bmc_userqualification
    ADD CONSTRAINT uni_user_qualification UNIQUE (userID, qualificationID);
    
    
ALTER TABLE IF EXISTS eg_bmc_userbank DROP CONSTRAINT IF EXISTS uni_user_bank;
ALTER TABLE IF EXISTS eg_bmc_userbank
    ADD CONSTRAINT uni_user_bank UNIQUE (userid,bankbranchid,tenantid);
    
    
ALTER TABLE IF EXISTS eg_bmc_userdocument DROP CONSTRAINT IF EXISTS uni_user_document;
ALTER TABLE IF EXISTS eg_bmc_userdocument
    ADD CONSTRAINT uni_user_document UNIQUE (userid,documentid,tenantid);
    
    
 ALTER TABLE IF EXISTS eg_address DROP CONSTRAINT IF EXISTS uni_user_address;
 ALTER TABLE IF EXISTS eg_address
    ADD CONSTRAINT uni_user_address UNIQUE (userid,tenantid);  