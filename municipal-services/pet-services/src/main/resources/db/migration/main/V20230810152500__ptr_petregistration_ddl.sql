CREATE TABLE eg_ptr_registration(
  id character varying(64),
  tenantId character varying(64),
  applicationNumber character varying(64),
  applicantName character varying(64),
  fatherName character varying(64),
  mobileNumber character varying(64), 
  emailId character varying(64),
  aadharNumber character varying(64),
  createdBy character varying(64),
  lastModifiedBy character varying(64),
  createdTime bigint,
  lastModifiedTime bigint,
 CONSTRAINT uk_eg_ptr_registration UNIQUE (id)
);

CREATE TABLE eg_ptr_petdetails(
		   id character varying(64),
		   petName character varying(64),
		   petType character varying(64),
		   breedType character varying(64),
		   petAge character varying(64),
		   petGender character varying(64),
		   clinicName character varying(64),
		   doctorName character varying(64),
		   lastVaccineDate character varying(64),
		   vaccinationNumber character varying(64),
		   petDetailsId character varying(64),
		   CONSTRAINT uk_eg_ptr_petdetails PRIMARY KEY (id),
		   CONSTRAINT fk_eg_ptr_petdetails FOREIGN KEY (petDetailsId) REFERENCES eg_ptr_registration (id)
		     ON UPDATE CASCADE
		     ON DELETE CASCADE
		);

CREATE TABLE eg_ptr_address(
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
   registrationId character varying(64),
   createdBy character varying(64),
   lastModifiedBy character varying(64),
   createdTime bigint,
   lastModifiedTime bigint,
   CONSTRAINT uk_eg_ptr_address PRIMARY KEY (id),
   CONSTRAINT fk_eg_ptr_address FOREIGN KEY (registrationId) REFERENCES eg_ptr_registration (id)
     ON UPDATE CASCADE
     ON DELETE CASCADE
);