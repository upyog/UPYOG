CREATE TABLE IF NOT EXISTS eg_user_address (
     id bigint NOT NULL,
     version numeric DEFAULT 0,
     createddate timestamp NOT NULL,
     lastmodifieddate timestamp,
     createdby bigint NOT NULL,
     lastmodifiedby bigint,
     type VARCHAR (50) NOT NULL,
     address VARCHAR(300),
     city VARCHAR(300),
     pincode VARCHAR(10),
     userid bigint NOT NULL,
     tenantid VARCHAR(256) NOT NULL
  );

--alter table  eg_user_address ADD CONSTRAINT IF NOT EXISTS eg_user_address_pkey PRIMARY KEY (id);

--alter table  eg_user_address ADD CONSTRAINT IF NOT EXISTS eg_user_address_user_fkey FOREIGN KEY (userid, tenantid)
-- REFERENCES eg_user ON DELETE CASCADE;

--alter table  eg_user_address ADD CONSTRAINT IF NOT EXISTS eg_user_address_type_unique UNIQUE (userid, tenantid, type);

CREATE SEQUENCE IF NOT EXISTS seq_eg_user_address START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;