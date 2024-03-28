-- DROP TABLE eg_userrole;
ALTER TABLE  IF NOT EXISTS eg_role ADD roleid bigint NOT NULL DEFAULT 0;
UPDATE  IF NOT EXISTS eg_role SET roleid = id;
-- alter table  eg_role ALTER COLUMN roleid DROP DEFAULT;
-- alter table  eg_role DROP COLUMN id;
alter table  eg_role RENAME COLUMN roleid TO id;
alter table  eg_role ADD CONSTRAINT IF NOT EXISTS eg_role_pk PRIMARY KEY (id, tenantid);

CREATE TABLE IF NOT EXISTS eg_userrole (
    roleid bigint NOT NULL,
    roleidtenantid character varying(256) NOT NULL,
    userid bigint NOT NULL,
    tenantid character varying(256) NOT NULL,
    FOREIGN KEY (roleid, roleidtenantid) REFERENCES eg_role (id, tenantid),
    FOREIGN KEY (userid, tenantid) REFERENCES eg_user (id, tenantid)
);