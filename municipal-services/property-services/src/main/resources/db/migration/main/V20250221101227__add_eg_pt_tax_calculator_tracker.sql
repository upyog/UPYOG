CREATE TABLE public.eg_pt_tax_calculator_tracker (
	uuid varchar(256) NOT NULL,
	propertyid varchar(256) NULL,
	tenantid varchar(256) NULL,
	financialyear varchar(256) NULL,
	fromdate varchar(256) NULL,
	todate varchar(256) NULL,
	propertytax numeric(10, 2) NULL,
	createdby varchar(128) NOT NULL,
	createdtime int8 NOT NULL,
	lastmodifiedby varchar(128) NULL,
	lastmodifiedtime int8 NULL,
	CONSTRAINT eg_pt_tax_calculator_tracker_pk PRIMARY KEY (uuid)
);
