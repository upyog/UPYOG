ALTER TABLE eg_swach_service_v2 ADD COLUMN active BOOLEAN DEFAULT TRUE;

CREATE INDEX IF NOT EXISTS index_eg_swach_address_v2_locality ON eg_swach_address_v2 (locality);

ALTER TABLE eg_swach_service_v2 ALTER COLUMN description DROP NOT NULL;
