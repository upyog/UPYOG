ALTER TABLE egcl_paymentDetail_audit ADD COLUMN IF NOT EXISTS receiptdate BIGINT;
ALTER TABLE egcl_paymentDetail_audit ADD COLUMN IF NOT EXISTS receipttype character varying(256);