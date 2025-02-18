CREATE TABLE public.eg_pgr_notification (
	uuid varchar(256) NOT NULL,
	servicerequestid varchar(256) NULL,
	tenantid varchar(256) NULL,
	applicationstatus varchar(128) NULL,
	recipientname varchar(300) NULL,
	emailid varchar(300) NULL,
	mobilenumber varchar(150) NULL,
	isemailsent bool NULL,
	issmssent bool NULL,
	createdby varchar(256) NOT NULL,
	createdtime int8 NOT NULL,
	lastmodifiedby varchar(256) NULL,
	lastmodifiedtime int8 NULL,
	CONSTRAINT eg_pgr_notification_pk PRIMARY KEY (uuid)
);
