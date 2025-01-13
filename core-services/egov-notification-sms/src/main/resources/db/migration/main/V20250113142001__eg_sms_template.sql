DROP TABLE IF EXISTS  eg_sms_template;

CREATE TABLE public.eg_sms_template (
	id varchar(64) NOT NULL,
	template_name varchar(256) NOT NULL,
	template_id varchar(250) NOT NULL,
	sms_body text NULL,
	tenant_id varchar(250) NULL,
	is_active bool NULL,
	createdby varchar(256) NOT NULL,
	createddate int8 NOT NULL,
	lastmodifiedby varchar(256) NULL,
	lastmodifieddate int8 NULL,
	CONSTRAINT eg_sms_template_pk PRIMARY KEY (id),
	CONSTRAINT eg_sms_template_unique UNIQUE (template_name)
);