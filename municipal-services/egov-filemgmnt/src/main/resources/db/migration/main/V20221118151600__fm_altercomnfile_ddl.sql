alter table eg_fm_filedetail add column businessservice character varying(45) ;
alter table eg_fm_servicedetail drop column   businessservice;
alter table eg_fm_servicedetail drop column  workflowcode;

