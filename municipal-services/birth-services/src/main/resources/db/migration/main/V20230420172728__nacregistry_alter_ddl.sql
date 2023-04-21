alter table eg_birth_nac_registry     
 add column  dateofreport bigint,
 add column   registration_status character varying(64) ,
 add column    registration_date bigint,
 add column     ack_no character varying(64);
