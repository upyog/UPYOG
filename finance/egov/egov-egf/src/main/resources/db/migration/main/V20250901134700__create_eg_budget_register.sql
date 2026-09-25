-- sequence
CREATE SEQUENCE IF NOT EXISTS seq_eg_budgetregister
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

-- table
CREATE TABLE IF NOT EXISTS eg_budgetregister (
    id bigint NOT NULL DEFAULT nextval('seq_eg_budgetregister'),
    budgetregisternumber character varying(50) NOT NULL,
    budgetregistername character varying(100) NOT NULL,
    createddate timestamp without time zone NOT NULL DEFAULT now(),

    currentfinancialyearid BIGINT NOT NULL,
    financialyearid BIGINT NOT NULL,

    statusid bigint,
    state_type character varying(100),
    state_id bigint,
    createdby bigint NOT NULL,
    lastmodifiedby bigint,
    lastmodifieddate timestamp without time zone,
    isactive boolean DEFAULT true,
    version bigint DEFAULT 0,

    CONSTRAINT pk_eg_budgetregister PRIMARY KEY (id),
    CONSTRAINT uk_eg_budgetregister_number UNIQUE (budgetregisternumber)
);

-- Foreign keys (add constraints separately so errors are clearer)
ALTER TABLE ONLY eg_budgetregister
    ADD CONSTRAINT fk_budgetregister_status FOREIGN KEY (statusid)
        REFERENCES egw_status(id);

ALTER TABLE ONLY eg_budgetregister
    ADD CONSTRAINT fk_financialyear FOREIGN KEY (financialyearid)
        REFERENCES financialyear(id) ON DELETE CASCADE;

ALTER TABLE ONLY eg_budgetregister
     ADD CONSTRAINT fk_currentfinancialyear FOREIGN KEY (currentfinancialyearid)
        REFERENCES financialyear(id) ON DELETE CASCADE;

ALTER TABLE ONLY eg_budgetregister
    ADD CONSTRAINT fk_budgetregister_state FOREIGN KEY (state_id)
        REFERENCES eg_wf_states(id);
