          
CREATE TABLE IF NOT EXISTS eg_bmc_employeewardmapper
(
    id SERIAL PRIMARY KEY,
    uuid character varying(1024) NOT NULL,
    tenantid character varying(255) NOT NULL,
    createdon bigint NOT NULL,
    createdby character varying(255) NOT NULL,
    modifiedon bigint,
    modifiedby character varying(255),
    ward character varying(50) NOT NULL,
    
    CONSTRAINT fk_wardmapper_employee FOREIGN KEY (uuid)
        REFERENCES public.eg_hrms_employee (uuid) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS eg_bmc_usersubschememapping
(
  id SERIAL PRIMARY KEY,
  applicationnumber character varying(50),
  schemeid bigint,
  courseid bigint,
  createdon bigint NOT NULL,
  createdby character varying(255) NOT NULL,
  modifiedon bigint,
  modifiedby character varying(255)
  
);
  
ALTER TABLE IF EXISTS eg_bmc_usersubschememapping DROP CONSTRAINT IF EXISTS uni_user_ssm;
ALTER TABLE IF EXISTS eg_bmc_usersubschememapping
ADD CONSTRAINT  uni_user_ssm UNIQUE (applicationnumber);

