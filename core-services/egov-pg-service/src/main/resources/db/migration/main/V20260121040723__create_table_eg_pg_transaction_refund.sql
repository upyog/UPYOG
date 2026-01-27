CREATE TABLE "eg_pg_transaction_refund" (
    "id" VARCHAR(128) NOT NULL,
    "tanant_id" VARCHAR(128) NOT NULL,
    "refund_id" VARCHAR(128) NOT NULL,
    "original_txn_id" VARCHAR(128) NOT NULL,
    "service_code" VARCHAR(64) NOT NULL,

    "original_amount" NUMERIC(15,2) NOT NULL,
    "refund_amount" NUMERIC(15,2) NOT NULL,

    "gateway_txn_id" VARCHAR(128) NULL DEFAULT NULL,
    "gateway" VARCHAR(64) NOT NULL,

    "status" VARCHAR(64) NOT NULL,

    "additional_details" VARCHAR(1024) NULL DEFAULT NULL,

    "created_by" VARCHAR(64),
    "created_time" BIGINT,
    "last_updated_by" VARCHAR(64),
    "last_updated_time" BIGINT,
    PRIMARY KEY ("refund_id")
    );