/* Central Scheme */

INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'CS01','15th Finance Commission','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='CF'),now(),now(),1,1, 60) ON CONFLICT DO NOTHING;
INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'CS02','16th Finance Commission','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='CF'),now(),now(),1,1, 60) ON CONFLICT DO NOTHING;
 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'CS03','Swachh Bharat Mission (SBM)','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='CF'),now(),now(),1,1, 60) ON CONFLICT DO NOTHING;
 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'CS04','Atal Mission for Rejuvenation and Urban Transformation (AMRUT)','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='CF'),now(),now(),1,1, 60) ON CONFLICT DO NOTHING;
 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'CS05','Pradhan Mantri Awas Yojana (PMAY)','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='CF'),now(),now(),1,1, 60) ON CONFLICT DO NOTHING;
 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'CS06','Integrated Development of Small & Medium Towns (IDSMT)','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='CF'),now(),now(),1,1, 60) ON CONFLICT DO NOTHING;
 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'CS07','National Urban Livelihoods Mission (NULM)','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='CF'),now(),now(),1,1, 60) ON CONFLICT DO NOTHING;
 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'CS08','National Health Mission (NHM)','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='CF'),now(),now(),1,1, 60) ON CONFLICT DO NOTHING;
 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'CS09','Non-Lapsable Central Pool of Resources (NLCPR)','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='CF'),now(),now(),1,1, 60) ON CONFLICT DO NOTHING;
 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'CS10','National Slum Development Program (NSDP)','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='CF'),now(),now(),1,1, 60) ON CONFLICT DO NOTHING;
 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'CS11','National Urban Information System (NUIS)','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='CF'),now(),now(),1,1, 60) ON CONFLICT DO NOTHING;

 /* State Scheme */

 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'SS01','6th Assam State Finance Commission','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='SF'),now(),now(),1,1, 01) ON CONFLICT DO NOTHING;
 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'SS02','State Owned Priority Development (SOPD)','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='SF'),now(),now(),1,1, 01) ON CONFLICT DO NOTHING;
 INSERT INTO scheme(id, code, name, validfrom, validto, isactive, description, fundid,createddate, lastmodifieddate, createdby,lastmodifiedby,statecode) VALUES
 (nextval('seq_scheme'),'SS03','MLA Area Development Scheme (MLAAD)','04/01/2024','03/31/2030',true,NULL,(select id from fund where code='SF'),now(),now(),1,1, 01) ON CONFLICT DO NOTHING;
