-- public.scheduler_master_mapping definition

-- Drop table

-- DROP TABLE public.scheduler_master_mapping;

CREATE TABLE public.scheduler_master_mapping (
	id int8 NOT NULL,
	startdate int8 NULL,
	enddate int8 NULL,
	slotsize int8 NULL,
	frequency int8 NULL,
	dateformat varchar NULL,
	CONSTRAINT scheduler_master_mapping_pk PRIMARY KEY (id)
);

-- public.seq_scheduler_master_mapping definition

-- DROP SEQUENCE public.seq_scheduler_master_mapping;

CREATE SEQUENCE public.seq_scheduler_master_mapping
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;