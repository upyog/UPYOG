-- DROP TABLE eg_userrole;
ALTER TABLE eg_role ADD   IF NOT EXISTS roleid bigint NOT NULL DEFAULT 0;
UPDATE  eg_role SET roleid = id;
alter table  eg_role ALTER COLUMN roleid DROP DEFAULT;
alter table  eg_role DROP COLUMN id CASCADE;
alter table  eg_role RENAME COLUMN roleid TO id;
alter table  eg_role DROP CONSTRAINT IF  EXISTS eg_role_pk;
alter table  eg_role ADD CONSTRAINT eg_role_pk PRIMARY KEY (id, tenantid);

CREATE TABLE IF NOT EXISTS eg_userrole (
    roleid bigint NOT NULL,
    roleidtenantid character varying(256) NOT NULL,
    userid bigint NOT NULL,
    tenantid character varying(256) NOT NULL,
    FOREIGN KEY (roleid, roleidtenantid) REFERENCES eg_role (id, tenantid),
    FOREIGN KEY (userid, tenantid) REFERENCES eg_user (id, tenantid)
);