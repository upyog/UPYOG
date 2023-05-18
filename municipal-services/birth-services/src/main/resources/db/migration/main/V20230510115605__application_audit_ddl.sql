ALTER TABLE eg_register_birth_permanent_address ALTER COLUMN pinno TYPE varchar(50);
ALTER TABLE eg_register_birth_permanent_address_audit ALTER COLUMN pinno TYPE varchar(50);
ALTER TABLE eg_register_birth_permanent_address ALTER COLUMN poid TYPE varchar(150);
ALTER TABLE eg_register_birth_permanent_address_audit ALTER COLUMN poid TYPE varchar(150);
ALTER TABLE eg_register_birth_permanent_address ALTER COLUMN ot_zipcode TYPE varchar(50);
ALTER TABLE eg_register_birth_permanent_address_audit ALTER COLUMN ot_zipcode TYPE varchar(50);

ALTER TABLE eg_register_birth_present_address ALTER COLUMN pinno TYPE varchar(50);
ALTER TABLE eg_register_birth_present_address_audit ALTER COLUMN pinno TYPE varchar(50);
ALTER TABLE eg_register_birth_present_address ALTER COLUMN poid TYPE varchar(150);
ALTER TABLE eg_register_birth_present_address_audit ALTER COLUMN poid TYPE varchar(150);
ALTER TABLE eg_register_birth_present_address ALTER COLUMN ot_zipcode TYPE varchar(50);
ALTER TABLE eg_register_birth_present_address ALTER COLUMN ot_zipcode TYPE varchar(50);