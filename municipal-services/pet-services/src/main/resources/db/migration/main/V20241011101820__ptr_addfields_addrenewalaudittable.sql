-- modify_eg_ptr_registration_table.sql

ALTER TABLE eg_ptr_registration 
ADD COLUMN applicationType character varying(64),
ADD COLUMN validityDate bigint,
ADD COLUMN status character varying(64),
ADD COLUMN expireFlag boolean,
ADD COLUMN petToken character varying(64),
ADD COLUMN previousApplicationNumber character varying(64),
ADD COLUMN propertyId character varying(64);

-- modify_eg_ptr_petdetails_table.sql

ALTER TABLE eg_ptr_petdetails 
ADD COLUMN petColor character varying(64),
ADD COLUMN adoptionDate bigint,
ADD COLUMN birthDate bigint,
ADD COLUMN identificationMark character varying(256);


-- create_eg_ptr_renewalauditdetails_table.sql

CREATE TABLE eg_ptr_renewalauditdetails (
  id character varying(64) PRIMARY KEY,
  tokenNumber character varying(64),
  applicationNumber character varying(64),
  previousApplicationNumber character varying(64),
  expiryDate bigint,
  renewalDate bigint,
  petRegistrationId character varying(64),
  CONSTRAINT fk_eg_ptr_renewalauditdetails FOREIGN KEY (petRegistrationId) REFERENCES eg_ptr_registration (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);
