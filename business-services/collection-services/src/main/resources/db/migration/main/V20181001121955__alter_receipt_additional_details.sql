ALTER TABLE egcl_receiptheader ADD COLUMN IF NOT EXISTS additionalDetails JSONB;
ALTER TABLE egcl_receiptdetails ADD COLUMN IF NOT EXISTS  additionalDetails JSONB;
ALTER TABLE egcl_instrumentheader ADD COLUMN IF NOT EXISTS  additionalDetails JSONB;

ALTER TABLE egcl_receiptheader ADD COLUMN IF NOT EXISTS payeemobile varchar(50);

ALTER TABLE egcl_receiptheader ALTER COLUMN transactionid TYPE varchar(50);

ALTER TABLE egcl_instrumentheader ADD COLUMN IF NOT EXISTS  instrumentDate BIGINT;
ALTER TABLE egcl_instrumentheader ADD COLUMN IF NOT EXISTS  instrumentNumber varchar(50);