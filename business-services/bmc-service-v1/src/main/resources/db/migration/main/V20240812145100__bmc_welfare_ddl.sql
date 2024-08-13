          

ALTER TABLE IF EXISTS eg_bmc_usersubschememapping DROP COLUMN IF EXISTS schemeid;
ALTER TABLE IF EXISTS eg_bmc_usersubschememapping
    ADD COLUMN IF NOT EXISTS machineid bigint;