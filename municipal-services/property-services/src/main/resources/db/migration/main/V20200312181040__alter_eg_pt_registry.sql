ALTER TABLE eg_pt_owner ALTER COLUMN IF NOT EXISTS ownertype DROP NOT NULL;

ALTER TABLE eg_pt_institution ALTER COLUMN nameofauthorizedperson DROP NOT NULL;