ALTER TABLE egbs_bill ADD COLUMN IF NOT EXISTS mobilenumber character varying(20);

ALTER TABLE egbs_billdetail ADD COLUMN IF NOT EXISTS receiptdate bigint, ADD COLUMN IF NOT EXISTS receiptnumber character varying(256);
