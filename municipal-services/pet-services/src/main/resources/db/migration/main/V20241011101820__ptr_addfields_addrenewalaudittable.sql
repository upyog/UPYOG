-- BEGIN TRANSACTION for all table modifications
BEGIN;



-- 2. Add columns to eg_ptr_registration table
ALTER TABLE eg_ptr_registration 
ADD COLUMN IF NOT EXISTS applicationType character varying(64),
ADD COLUMN IF NOT EXISTS validityDate bigint,
ADD COLUMN IF NOT EXISTS status character varying(64),
ADD COLUMN IF NOT EXISTS expireFlag boolean,
ADD COLUMN IF NOT EXISTS petToken character varying(64),
ADD COLUMN IF NOT EXISTS previousApplicationNumber character varying(64),
ADD COLUMN IF NOT EXISTS propertyId character varying(64);

-- 3. Add columns to eg_ptr_petdetails table
ALTER TABLE eg_ptr_petdetails 
ADD COLUMN IF NOT EXISTS petColor character varying(64),
ADD COLUMN IF NOT EXISTS adoptionDate bigint,
ADD COLUMN IF NOT EXISTS birthDate bigint,
ADD COLUMN IF NOT EXISTS identificationMark character varying(256);

-- 4. Create renewalauditdetails table if it doesn't exist
CREATE TABLE IF NOT EXISTS eg_ptr_renewalauditdetails (
  id character varying(64) PRIMARY KEY,
  tokenNumber character varying(64),
  applicationNumber character varying(64),
  previousApplicationNumber character varying(64),
  expiryDate bigint,
  renewalDate bigint,
  petRegistrationId character varying(64),
  CONSTRAINT fk_eg_ptr_renewalauditdetails FOREIGN KEY (petRegistrationId) 
    REFERENCES eg_ptr_registration (id) ON UPDATE CASCADE ON DELETE CASCADE
);

-- COMMIT all changes if everything succeeds
COMMIT;