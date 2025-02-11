ALTER TABLE eq_plant_user_map ADD COLUMN IF NOT EXISTS plantUserType  character varying(64);
ALTER TABLE eq_plant_user_map_auditlog ADD COLUMN IF NOT EXISTS plantUserType character varying(64);
ALTER TABLE eq_plant_user_map RENAME COLUMN plantOperatorUuid TO plantUserUuid;
ALTER TABLE eq_plant_user_map_auditlog RENAME COLUMN plantOperatorUuid TO plantUserUuid;