CREATE TABLE public.eg_grbg_bill_tracker (
	uuid varchar(256) NOT NULL,
	grbg_application_id varchar(256) NULL,
	tenant_id varchar(256) NULL,
	"month" varchar(256) NULL,
	"year" varchar(256) NULL,
	from_date varchar(256) NULL,
	to_date varchar(256) NULL,
	grbg_bill_amount numeric(10, 2) NULL,
	created_by varchar(128) NOT NULL,
	created_time int8 NOT NULL,
	last_modified_by varchar(128) NULL,
	last_modified_time int8 NULL,
	CONSTRAINT eg_grbg_bill_tracker_pk PRIMARY KEY (uuid)
);
