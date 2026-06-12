CREATE TABLE IF NOT EXISTS eg_ws_batch_demand_log (
    id character varying(64) PRIMARY KEY,
    tenantid character varying(64) NOT NULL,
    taxperiodfrom bigint NOT NULL,
    taxperiodto bigint NOT NULL,
    insertiontime bigint NOT NULL,
    totalconnectioncount bigint NOT NULL,
    isdemandexecuted boolean NOT NULL
);

CREATE INDEX IF NOT EXISTS index_eg_ws_batch_demand_log_tenant_period ON eg_ws_batch_demand_log (tenantid, taxperiodfrom, taxperiodto);

CREATE TABLE IF NOT EXISTS eg_ws_batch_connection_log (
    id character varying(64) PRIMARY KEY,
    connectionno character varying(64) NOT NULL,
    taxperiodfrom bigint NOT NULL,
    taxperiodto bigint NOT NULL,
    insertiondate bigint NOT NULL,
    taxamount decimal NOT NULL,
    tenantid character varying(64) NOT NULL
);

CREATE INDEX IF NOT EXISTS index_eg_ws_batch_connection_log_conn_period ON eg_ws_batch_connection_log (connectionno, taxperiodfrom, taxperiodto);
