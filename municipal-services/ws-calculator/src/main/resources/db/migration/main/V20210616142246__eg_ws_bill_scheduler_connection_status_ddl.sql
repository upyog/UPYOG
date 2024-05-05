create table IF NOT EXISTS eg_ws_bill_scheduler_connection_status (id character varying(64) PRIMARY KEY, consumercode character varying(64), eg_ws_scheduler_id character varying(64),locality character varying(64), module character varying(64), createdtime bigint, lastupdatedtime bigint, status character varying(64),tenantid character varying(64), reason character varying(1000) );

