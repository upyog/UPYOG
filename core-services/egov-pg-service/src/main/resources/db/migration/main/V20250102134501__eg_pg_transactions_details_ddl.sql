DROP TABLE IF EXISTS  eg_pg_transactions_details;

CREATE TABLE "eg_pg_transactions_details" (
	"uuid" VARCHAR(255) NOT NULL,
	"txn_id" varchar(128) NOT NULL,
	"txn_amount" NUMERIC(15,2) NOT NULL,
	"bill_id" VARCHAR(64) NOT NULL,
	"consumer_code" varchar(128) NULL,
    "created_by" character varying(64),
    "created_time" bigint,
    "last_modified_by" character varying(64),
    "last_modified_time" bigint,
	CONSTRAINT eg_pg_transactions_details_pkey PRIMARY KEY (uuid),
	CONSTRAINT eg_pg_transactions_details_txn_id_fk FOREIGN KEY (txn_id) REFERENCES public.eg_pg_transactions(txn_id) ON DELETE CASCADE ON UPDATE CASCADE
);