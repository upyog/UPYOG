CREATE TABLE egf_refund_application
(
    id                          BIGINT NOT NULL,
    tenantid                    CHARACTER VARYING(64) NOT NULL,
    refundapplicationnumber     CHARACTER VARYING(128) NOT NULL,
    modulename                  CHARACTER VARYING(64) NOT NULL,
    businessservice             CHARACTER VARYING(128) NOT NULL,
    referencenumber             CHARACTER VARYING(128),
    paymentid                   CHARACTER VARYING(128),
    receiptnumber               CHARACTER VARYING(128),
    refundamount                NUMERIC(12, 2) NOT NULL,
    refundreason                CHARACTER VARYING(1000),
    refunddate                  BIGINT,
    debitglcode                 CHARACTER VARYING(64),
    creditglcode                CHARACTER VARYING(64),
    fundcode                    CHARACTER VARYING(50) NOT NULL,
    departmentcode              CHARACTER VARYING(50) NOT NULL,
    functioncode                CHARACTER VARYING(50),
    status                      CHARACTER VARYING(64) NOT NULL,
    vouchernumber               CHARACTER VARYING(128),
    rejectionreason             CHARACTER VARYING(1000),
    state_id                    BIGINT,
    createdby                   BIGINT NOT NULL,
    createddate                 TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    lastmodifiedby              BIGINT,
    lastmodifieddate            TIMESTAMP WITHOUT TIME ZONE
);

CREATE SEQUENCE seq_egf_refund_application
    START WITH 1
    INCREMENT BY 1
    MINVALUE 0
    NO MAXVALUE
    CACHE 1;

ALTER TABLE ONLY egf_refund_application
    ADD CONSTRAINT egf_refund_application_pkey
    PRIMARY KEY (id);

ALTER TABLE ONLY egf_refund_application
    ADD CONSTRAINT uk_egf_refund_application
    UNIQUE (tenantid, refundapplicationnumber);

ALTER TABLE ONLY egf_refund_application
    ADD CONSTRAINT fk_egf_refund_state
    FOREIGN KEY (state_id)
    REFERENCES eg_wf_states(id);

CREATE INDEX indx_egf_refund_status
    ON egf_refund_application
    USING btree (tenantid, status);

CREATE INDEX indx_egf_refund_state
    ON egf_refund_application
    USING btree (state_id);

CREATE INDEX indx_egf_refund_payment
    ON egf_refund_application
    USING btree (tenantid, paymentid);