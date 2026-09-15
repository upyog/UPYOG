ALTER TABLE egf_refund_application
    ADD COLUMN gatewayrefundid VARCHAR(128),
    ADD COLUMN gatewaytransactionid VARCHAR(128),
    ADD COLUMN gatewaytransactiondate BIGINT,
    ADD COLUMN refundpaymentmode VARCHAR(50),
    ADD COLUMN refundpaymentstatus VARCHAR(50),
    ADD COLUMN paymentvouchernumber VARCHAR(50),
    ADD COLUMN paymentprocesseddate BIGINT;

CREATE UNIQUE INDEX uk_egf_refund_gateway_refund
    ON egf_refund_application (tenantid, gatewayrefundid)
    WHERE gatewayrefundid IS NOT NULL;

CREATE UNIQUE INDEX uk_egf_refund_payment_voucher
    ON egf_refund_application (tenantid, paymentvouchernumber)
    WHERE paymentvouchernumber IS NOT NULL;