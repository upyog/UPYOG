create TABLE eg_rent_lease_application(
	uuid VARCHAR(255),
	tenantid VARCHAR(255),
	mobile_no VARCHAR(255),
	start_date int8,
	end_date int8,
	month int8,
	assetId VARCHAR(255),
	applicant_detail JSONB,
	status Varchar(255),
	is_active BOOLEAN,
	createdby VARCHAR(200),
	createddate int8,
	lastmodifieddate int8,
	lastmodifiedby VARCHAR(200),
	applicationno VARCHAR(255)
);


ALTER TABLE eg_rent_lease_application ADD CONSTRAINT pk_id_eg_rent_lease_application PRIMARY KEY (uuid);