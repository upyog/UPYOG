CREATE SEQUENCE seq_egf_budgethead
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

CREATE TABLE egf_budgethead
(
  id bigint NOT NULL DEFAULT nextval('seq_egf_budgethead'),
  name character varying(100) NOT NULL,
  code character varying(100) NOT NULL,
  accountType character varying(100) NOT NULL,
  accountTypeCode character varying(10),
  program character varying(10),
  category character varying(100),
  isactive boolean,
  state_code VARCHAR(100),
  createdby bigint NOT NULL,
  lastmodifiedby bigint,
  createddate timestamp without time zone NOT NULL,
  lastmodifieddate timestamp without time zone,
  version bigint DEFAULT 0,
  CONSTRAINT pk_budgethead PRIMARY KEY (id)
);

ALTER SEQUENCE seq_egf_budgethead OWNED BY egf_budgethead.id;
