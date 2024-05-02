
CREATE TABLE IF NOT EXISTS eg_ws_scheduler
(
  id character varying(64),
  transactiontype character varying(64),
  locality character varying(64) NOT NULL,
  status character varying(64) NOT NULL,
  billingcyclestartdate bigint NOT NULL,
  billingcycleenddate bigint NOT NULL,
  createdby character varying(64),
  lastmodifiedby character varying(64),
  createdtime bigint,
  lastmodifiedtime bigint,
  tenantid character varying(64)
);

CREATE INDEX IF NOT EXISTS index_eg_ws_scheduler_tenantid ON eg_ws_scheduler (tenantid);
CREATE INDEX IF NOT EXISTS index_eg_ws_scheduler_locality ON eg_ws_scheduler (locality);
