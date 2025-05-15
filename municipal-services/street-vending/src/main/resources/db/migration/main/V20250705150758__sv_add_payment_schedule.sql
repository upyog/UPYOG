CREATE TABLE eg_sv_vendor_payment_schedule (
    id VARCHAR(64) NOT NULL,
    certificate_no VARCHAR(64),
    vendor_id VARCHAR(64),
    application_no VARCHAR(64),
    last_payment_date DATE,
    due_date DATE,
    payment_receipt_no VARCHAR(64),
    status VARCHAR(64),
    createdby VARCHAR(64),
    lastmodifiedby VARCHAR(64),
    createdtime BIGINT,
    lastmodifiedtime BIGINT,
    
    CONSTRAINT pk_eg_sv_vendor_payment_schedule PRIMARY KEY (id)
);

  
  CREATE TABLE eg_sv_vendor_payment_schedule_auditdetails (

    id character varying(64) NOT NULL,
    certificate_no VARCHAR(64),
    vendor_id VARCHAR(64),
    application_no VARCHAR(64),
    last_payment_date date,
    due_date date,
    payment_receipt_no VARCHAR(64),
    status VARCHAR(64),
    createdby character varying(64),
    lastmodifiedby character varying(64) ,
    createdtime bigint,
    lastmodifiedtime bigint,
    
    CONSTRAINT pk_eg_sv_vendor_payment_schedule_audit PRIMARY KEY (id)
  );
  
	ALTER TABLE public.eg_sv_vendor_detail
	DROP COLUMN vendor_payment_frequency;
	
	ALTER TABLE public.eg_sv_vendor_detail_auditdetails
	DROP COLUMN vendor_payment_frequency;
	
	ALTER TABLE public.eg_sv_street_vending_detail
	ADD COLUMN vendor_payment_frequency character varying(64);
	
	ALTER TABLE public.eg_sv_street_vending_detail_auditdetails
	ADD COLUMN vendor_payment_frequency character varying(64);