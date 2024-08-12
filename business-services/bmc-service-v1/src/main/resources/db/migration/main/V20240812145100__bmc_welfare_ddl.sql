          
ALTER TABLE IF EXISTS eg_bmc_usersubschememapping
RENAME COLUMN IF EXISTS schemeid TO machineid;