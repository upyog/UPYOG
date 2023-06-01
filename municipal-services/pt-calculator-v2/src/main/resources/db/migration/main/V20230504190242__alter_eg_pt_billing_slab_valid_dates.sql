ALTER TABLE eg_pt_billingslab_v2 ADD column validFrom varchar(255);
ALTER TABLE eg_pt_billingslab_v2 ADD column validTo varchar(255);
update eg_pt_billingslab_v2  set validfrom ='2015-04-01', validTo='2030-03-31';