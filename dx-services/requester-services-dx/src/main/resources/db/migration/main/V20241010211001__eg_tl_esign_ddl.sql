DROP TABLE IF EXISTS  eg_tl_esignedfilestore;

CREATE TABLE "eg_tl_esignedfilestore" (
	"txn_id" VARCHAR(128) NOT NULL,
	"tenant_id" VARCHAR(128) not null,
	"consumercode" VARCHAR(64) NOT NULL,
	"filestore_id" VARCHAR(64) NOT NULL,
	"signed_filestore_id" VARCHAR(128),
	"module" VARCHAR(64) NOT NULL,	
	"name" VARCHAR(128),
    "created_by" character varying(64),
    "created_time" bigint NOT NULL,
    "last_modified_by" character varying(64),
    "last_modified_time" bigint ,
	PRIMARY KEY ("txn_id")
);