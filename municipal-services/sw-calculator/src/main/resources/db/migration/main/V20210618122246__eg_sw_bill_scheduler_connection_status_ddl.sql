create table IF NOT EXISTS eg_sw_bill_scheduler_connection_status (id character varying(64) PRIMARY KEY, consumercode character varying(64), eg_sw_scheduler_id character varying(64),locality character varying(64), module character varying(64), createdtime bigint, lastupdatedtime bigint, status character varying(64),tenantid character varying(64), reason character varying(1000) );


CREATE INDEX IF NOT EXISTS idx_eg_sw_bill_scheduler_connection_status_eg_sw_scheduler_id ON eg_sw_bill_scheduler_connection_status (eg_sw_scheduler_id);
