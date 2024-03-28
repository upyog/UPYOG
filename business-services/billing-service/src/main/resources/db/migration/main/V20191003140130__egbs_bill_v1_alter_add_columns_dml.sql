ALTER TABLE egbs_bill_v1 ADD COLUMN IF NOT EXISTS status character varying(64), ADD COLUMN IF NOT EXISTS additionaldetails jsonb;

ALTER TABLE egbs_billdetail_v1 ADD COLUMN IF NOT EXISTS additionaldetails jsonb;

ALTER TABLE egbs_billaccountdetail_v1 ADD COLUMN IF NOT EXISTS additionaldetails jsonb;