CREATE TABLE eg_sv_street_vending_detail (
		application_id VARCHAR(64) PRIMARY KEY,
		tenant_id VARCHAR(64) NOT NULL,
		application_no VARCHAR(64) NOT NULL,
		application_date BIGINT NOT NULL,
		certificate_no VARCHAR(64),
		approval_date BIGINT,
		application_status VARCHAR(50) NOT NULL,
		trade_license_no VARCHAR(64),
		vending_activity VARCHAR(100) NOT NULL,
		vending_zone VARCHAR(100) NOT NULL,
		cart_latitude DECIMAL(10,6) NOT NULL,
		cart_longitude DECIMAL(10,6) NOT NULL,
		vending_area INT NOT NULL,
		vending_license_certificate_id VARCHAR(64),
		local_authority_name VARCHAR(100) NOT NULL,
		disability_status VARCHAR(50),
		beneficiary_of_social_schemes VARCHAR(100),
		terms_and_condition CHAR(1) default 'Y',
		createdby character varying(64) NOT NULL,
		lastmodifiedby character varying(64),
		createdtime bigint NOT NULL,
		lastmodifiedtime bigint,
		constraint eg_sv_street_vending_detail_application_no_key UNIQUE (application_no),
		constraint eg_sv_street_vending_detail_certificate_no_key UNIQUE (certificate_no)
);

create table eg_sv_vendor_detail(
		id VARCHAR(255) PRIMARY KEY,
		application_id character varying(64) NOT NULL,
		vendor_id character varying(64),
		name character varying(100) NOT NULL,
		father_name character varying(100) NOT NULL,
		date_of_birth date NOT NULL,
		email_id character varying(200),
		mobile_no character varying(10) NOT NULL,
		gender CHAR(1),
		relationship_type character varying(30) NOT NULL,
		createdby character varying(64) NOT NULL,
		lastmodifiedby character varying(64),
		createdtime bigint NOT NULL,
		lastmodifiedtime bigint,
		constraint eg_sv_vendor_detail_vendor_id_fk
		FOREIGN KEY (vendor_id) REFERENCES eg_sv_vendor_detail (id),
		constraint eg_sv_vendor_detail_application_id_fk
		FOREIGN KEY (application_id) REFERENCES eg_sv_street_vending_detail (application_id)
		ON UPDATE NO ACTION
		ON DELETE CASCADE
);

create table eg_sv_address_detail (
		address_id character varying(64) NOT NULL,
		address_type character varying(64) NOT NULL,
		vendor_id character varying(64) NOT NULL,
		house_no character varying(100) NOT NULL,
		address_line_1 character varying(150) NOT NULL,
		address_line_2 character varying(150) NOT NULL,
		landmark character varying(150),
		city character varying(100) NOT NULL,
		city_code character varying(10) NOT NULL,
		locality character varying(100) NOT NULL,
		locality_code character varying(20) NOT NULL,
		pincode VARCHAR(12) NOT NULL,
		constraint eg_sv_address_detail_id_pk PRIMARY KEY (address_id),
		constraint eg_sv_address_detail_vendor_id_fk
		FOREIGN KEY (vendor_id) REFERENCES eg_sv_vendor_detail (id)
		ON UPDATE NO ACTION
		ON DELETE CASCADE
);

create table eg_sv_document_detail(
		document_detail_id character varying(64) NOT NULL,
		application_id character varying(64) NOT NULL,
		document_type character varying(64),
		filestore_id character varying(64) NOT NULL,
		createdby character varying(64) NOT NULL,
		lastmodifiedby character varying(64),
		createdtime bigint NOT NULL,
		lastmodifiedtime bigint,
		constraint eg_sv_document_detail_id_pk PRIMARY KEY (document_detail_id),
		constraint eg_sv_document_detail_application_id_fk
		FOREIGN KEY (application_id) REFERENCES eg_sv_street_vending_detail (application_id)
		ON UPDATE NO ACTION
		ON DELETE CASCADE
);

create table eg_sv_bank_detail (
		bank_detail_id VARCHAR(255) PRIMARY KEY,
		application_id character varying(64) NOT NULL,
		account_no character varying(200),
		ifsc_code character varying(200),
		bank_name character varying(300),
		bank_branch_name character varying(300),
		account_holder_name character varying(300),
		createdby character varying(64) NOT NULL,
		lastmodifiedby character varying(64),
		createdtime bigint NOT NULL,
		lastmodifiedtime bigint,
		constraint eg_sv_bank_detail_application_id_fk
		FOREIGN KEY (application_id) REFERENCES eg_sv_street_vending_detail (application_id)
		ON UPDATE NO ACTION
		ON DELETE CASCADE
);

create table eg_sv_operation_time_detail (
		id VARCHAR(255) PRIMARY KEY,
		application_id character varying(64) NOT NULL,
		day_of_week VARCHAR(20) NOT NULL,
		from_time TIME NOT NULL,
		to_time TIME NOT NULL,
		createdby character varying(64) NOT NULL,
		lastmodifiedby character varying(64),
		createdtime bigint NOT NULL,
		lastmodifiedtime bigint,
		constraint eg_sv_operation_time_detail_application_id_fk
		FOREIGN KEY (application_id) REFERENCES eg_sv_street_vending_detail (application_id)
		ON UPDATE NO ACTION
		ON DELETE CASCADE
);




CREATE SEQUENCE IF NOT EXISTS seq_street_vending_application_id;