-- alter table  eg_role DROP CONSTRAINT eg_roles_role_name_key;
alter table  eg_role DROP CONSTRAINT IF  EXISTS eg_roles_code_tenant ;
alter table  eg_role ADD CONSTRAINT  eg_roles_code_tenant UNIQUE (code, tenantid);