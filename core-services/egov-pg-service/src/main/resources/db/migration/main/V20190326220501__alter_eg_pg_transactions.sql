ALTER TABLE eg_pg_transactions ALTER COLUMN "module" DROP NOT NULL;
ALTER TABLE eg_pg_transactions ALTER COLUMN "module_id" DROP NOT NULL;
ALTER TABLE eg_pg_transactions ALTER COLUMN "txn_status_msg" DROP NOT NULL;
ALTER TABLE eg_pg_transactions ADD COLUMN IF NOT EXISTS  "consumer_code" VARCHAR(128);
UPDATE eg_pg_transactions SET consumer_code = module_id;
ALTER TABLE eg_pg_transactions ADD COLUMN IF NOT EXISTS   "additional_details" JSONB NULL;