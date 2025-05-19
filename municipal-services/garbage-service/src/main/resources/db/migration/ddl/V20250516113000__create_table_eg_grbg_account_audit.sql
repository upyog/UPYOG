CREATE TABLE eg_grbg_account_audit (
	auditid varchar(128) NOT NULL,
	grbg_application_no varchar(225) NULL,
	status varchar(50) NULL,
	"type" varchar(50) NULL,
	grbg_account_details jsonb NULL,
	auditcreatedtime int8 NOT NULL,
	CONSTRAINT pk_eg_grbg_account_audit PRIMARY KEY (auditid)
);

CREATE INDEX index_eg_grbg_account_audit_grbg_application_no ON eg_grbg_account_audit USING btree (grbg_application_no);
CREATE INDEX index_eg_grbg_account_audit_grbg_application_no_status ON eg_grbg_account_audit USING btree (grbg_application_no, status);
CREATE INDEX index_eg_grbg_account_audit_grbg_application_no_type ON eg_grbg_account_audit USING btree (grbg_application_no, "type");
CREATE INDEX index_eg_grbg_account_audit_grbg_application_no_status_type ON eg_grbg_account_audit USING btree (grbg_application_no, status, "type");

CREATE SEQUENCE seq_eg_grbg_account_audit start WITH 1 increment BY 1 no minvalue no maxvalue cache 1;