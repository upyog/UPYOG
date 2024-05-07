
CREATE TABLE IF NOT EXISTS eg_sw_scheduler
(
  id character varying(64),
  transactiontype character varying(64),
  locality character varying(64) NOT NULL,
  status character varying(64) NOT NULL,
  billingcyclestartdate bigint NOT NULL,
  billingcycleenddate bigint NOT NULL,
  createdBy character varying(64),
  lastModifiedBy character varying(64),
  createdTime bigint,
  lastModifiedTime bigint,
  tenantid character varying(64)
);

CREATE INDEX IF NOT EXISTS idx_eg_sw_scheduler_tenantid ON eg_sw_scheduler (tenantid);
CREATE INDEX IF NOT EXISTS idx_eg_sw_scheduler_locality ON eg_sw_scheduler (locality);
