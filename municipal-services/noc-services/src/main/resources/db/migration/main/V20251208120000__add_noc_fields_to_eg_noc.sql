ALTER TABLE public.eg_noc
ADD COLUMN applicantname varchar(256) NULL,
ADD COLUMN fathername varchar(256) NULL,
ADD COLUMN mobile varchar(20) NULL,
ADD COLUMN dob varchar(256) NULL,
ADD COLUMN gender varchar(20) NULL,
ADD COLUMN email varchar(256) NULL,
ADD COLUMN address varchar(512) NULL,
ADD COLUMN nocreason varchar(512) NULL,
ADD COLUMN connectiontype varchar(64) NULL,
ADD COLUMN locationdetail jsonb NULL;

ALTER TABLE public.eg_noc_auditdetails
ADD COLUMN applicantname varchar(256) NULL,
ADD COLUMN fathername varchar(256) NULL,
ADD COLUMN mobile varchar(20) NULL,
ADD COLUMN dob varchar(256) NULL,
ADD COLUMN gender varchar(20) NULL,
ADD COLUMN email varchar(256) NULL,
ADD COLUMN address varchar(512) NULL,
ADD COLUMN nocreason varchar(512) NULL,
ADD COLUMN connectiontype varchar(64) NULL,
ADD COLUMN locationdetail jsonb NULL;
