CREATE TABLE ud_user_sso (
  id SERIAL, 
  sso_id BIGINT,
  user_uuid varchar(256),
  createddate int8, 
  createdby varchar(256), 
  lastmodifieddate int8,
  lastmodifiedby varchar(256)
);
 
ALTER TABLE ud_user_sso 
ADD CONSTRAINT ud_user_sso_pkey PRIMARY KEY (id);