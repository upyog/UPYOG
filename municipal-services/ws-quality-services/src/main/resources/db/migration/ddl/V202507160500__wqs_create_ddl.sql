
CREATE TABLE IF NOT EXISTS eg_wq_application
(
  id character varying(64) NOT NULL,
  tenantid character varying(250) NOT NULL,
  applicationno character varying(64) NOT NULL,
  type character varying(256),
  applicationdetails JSONB,
  createdBy character varying(64),
  createdTime bigint,
  CONSTRAINT eg_wq_application_pkey PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS index_eg_wq_application_tenantId ON eg_wq_application (tenantid);
CREATE INDEX IF NOT EXISTS index_eg_wq_application_applicationNo ON eg_wq_application (applicationno);
CREATE INDEX IF NOT EXISTS index_eg_wq_application_type ON eg_wq_application (type);

CREATE SEQUENCE public.eg_wq_application_id_seq INCREMENT BY 1;
