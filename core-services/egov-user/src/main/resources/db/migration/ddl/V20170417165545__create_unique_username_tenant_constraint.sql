-- alter table  eg_user DROP CONSTRAINT eg_user_user_name_key;
alter table  eg_user DROP CONSTRAINT IF  EXISTS eg_user_user_name_tenant;
alter table  eg_user ADD CONSTRAINT  eg_user_user_name_tenant UNIQUE (username, tenantid);