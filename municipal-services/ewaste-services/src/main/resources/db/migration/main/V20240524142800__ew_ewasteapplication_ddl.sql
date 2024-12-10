CREATE TABLE eg_ew_requests (
  id character varying(64),
  tenantId character varying(64),
  requestId character varying(64),
  calculatedAmount character varying(64),
  vendorUuid character varying(64),
  transactionId character varying(64),
  pickUpDate character varying(64),
  finalAmount character varying(64),
  requestStatus character varying(64),
  createdBy character varying(64),
  lastModifiedBy character varying(64),
  createdTime bigint,
  lastModifiedTime bigint,
 CONSTRAINT uk_eg_ew_requests UNIQUE (id)
);

CREATE TABLE eg_ew_applicantdetails(
	id character varying(64),
	applicantName character varying(64),
	mobileNumber character varying(64),
	altMobileNumber character varying(64),
	emailId character varying(64),
	ewId character varying(64),
	CONSTRAINT uk_eg_ew_applicantdetails PRIMARY KEY (id),
	CONSTRAINT fk_eg_ew_applicantdetails FOREIGN KEY (ewId) REFERENCES eg_ew_requests (id)
	   ON UPDATE CASCADE
	   ON DELETE CASCADE
);

CREATE TABLE eg_ew_ewastedetails(
	id character varying(64),
	productId character varying(64),
	productName character varying(64),
	quantity character varying(64),
	price character varying(64),
	ewId character varying(64),
	CONSTRAINT uk_eg_ew_ewastedetails PRIMARY KEY (id),
	CONSTRAINT fk_eg_ew_ewastedetails FOREIGN KEY (ewId) REFERENCES eg_ew_requests (id)
	   ON UPDATE CASCADE
	   ON DELETE CASCADE
);


CREATE TABLE eg_ew_address(
   id character varying(64),
   tenantId character varying(64),
   doorNo character varying(64),
   latitude FLOAT,
   longitude FLOAT,
   buildingName character varying(64),
   addressId character varying(64),
   addressNumber character varying(64),
   type character varying(64),
   addressLine1 character varying(256),
   addressLine2 character varying(256),
   landmark character varying(64),
   street character varying(64),
   city character varying(64),
   locality character varying(64),
   pincode character varying(64),
   detail character varying(64),
   ewId character varying(64),
   createdBy character varying(64),
   lastModifiedBy character varying(64),
   createdTime bigint,
   lastModifiedTime bigint,
   CONSTRAINT uk_eg_ew_address PRIMARY KEY (id),
   CONSTRAINT fk_eg_ew_address FOREIGN KEY (ewId) REFERENCES eg_ew_requests (id)
     ON UPDATE CASCADE
     ON DELETE CASCADE
);

CREATE TABLE eg_ew_ewastedocuments(
    id character varying(64) NOT NULL,
    tenantId character varying(64) NOT NULL,
    documentType character varying(64) NOT NULL,
    filestoreId character varying(64) NOT NULL,
    documentUid CHARACTER VARYING (128),
    ewId character varying(64) NOT NULL,
    active boolean,
    createdBy character varying(64) NOT NULL,
    lastModifiedBy character varying(64),
    createdTime bigint,
    lastModifiedTime bigint,

    CONSTRAINT uk_eg_ew_ewastedocuments PRIMARY KEY (id),
    CONSTRAINT fk_eg_ew_ewastedocuments FOREIGN KEY (ewId) REFERENCES eg_ew_requests (id)
);