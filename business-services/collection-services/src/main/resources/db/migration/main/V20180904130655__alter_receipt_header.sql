ALTER TABLE "egcl_receiptheader"
ADD COLUMN IF NOT EXISTS "collectedamount" NUMERIC(12,2) DEFAULT NULL,
ADD COLUMN IF NOT EXISTS "manualreceiptdate"  BIGINT;
