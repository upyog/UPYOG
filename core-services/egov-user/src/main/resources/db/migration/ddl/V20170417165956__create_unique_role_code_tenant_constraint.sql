-- alter table  eg_role DROP CONSTRAINT eg_roles_role_name_key;

alter table  eg_role ADD CONSTRAINT IF NOT EXISTS eg_roles_code_tenant UNIQUE (code, tenantid);