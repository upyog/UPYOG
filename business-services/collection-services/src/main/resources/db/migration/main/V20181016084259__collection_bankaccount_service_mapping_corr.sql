-- -- DROP TABLEIF EXISTS egcl_bankaccountservicemapping;

-- DROP SEQUENCE IF EXISTS seq_egcl_bankaccountservicemapping;

CREATE TABLE IF NOT EXISTS egcl_bankaccountservicemapping
(
  id bigint NOT NULL,
  businessdetails character varying(12) NOT NULL,
  bankaccount character varying(12) NOT NULL,
  bank character varying(12) ,
  bankbranch character varying(12),
  active boolean,
  version bigint NOT NULL DEFAULT 1,
  createdby bigint NOT NULL,
  lastmodifiedby bigint NOT NULL,
  createddate bigint,
  lastmodifieddate bigint,
  tenantid character varying(252) NOT NULL,
  CONSTRAINT pk_egcl_bankaccountservicemapping PRIMARY KEY (id)
  );

CREATE SEQUENCE IF NOT EXISTS seq_egcl_bankaccountservicemapping;