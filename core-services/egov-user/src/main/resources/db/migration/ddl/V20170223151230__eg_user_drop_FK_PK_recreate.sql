--alter table  eg_user DROP constraint IF  EXISTS  eg_user_pkey ;
--alter table  eg_user add constraint   eg_user_pkey primary key (id,tenantid);

alter table  eg_address add column IF NOT EXISTS userid_bak bigint;
update  eg_address set userid_bak=userid;
-- alter table  eg_address drop column  userid;
alter table  eg_address add column IF NOT EXISTS userid bigint;
update   eg_address set userid=userid_bak;
-- alter table  eg_address drop column  userid_bak;


alter table  eg_userrole add column IF NOT EXISTS userid_bak bigint;
update  eg_userrole set userid_bak=userid;
-- alter table  eg_userrole drop column  userid;
alter table  eg_userrole add column IF NOT EXISTS userid bigint;
update  eg_userrole set userid=userid_bak;
-- alter table  eg_userrole drop column  userid_bak;

alter table  eg_user add column IF NOT EXISTS id_bak bigint;
update  eg_user set id_bak=id;
-- alter table  eg_user drop column  id;
alter table  eg_user add column IF NOT EXISTS id bigint;
update  eg_user set id=id_bak;
-- alter table  eg_user drop column  id_bak;
alter table  eg_user alter column id  set not null;

