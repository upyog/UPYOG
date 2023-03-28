ALTER TABLE eg_birth_details
 ADD COLUMN adopt_decree_order_no character varying(64),
 ADD COLUMN adopt_dateoforder_decree bigint,
 ADD COLUMN adopt_agency_contact_person  character varying(64) ,
 ADD COLUMN adopt_agency_contact_person_mobileno character varying(12),
 ADD COLUMN old_birth_reg_no character varying(64),
 DROP COLUMN adopt_firstname_en,
 DROP COLUMN adopt_firstname_ml  ,    
 DROP COLUMN adopt_middlename_en   ,
 DROP COLUMN adopt_middlename_ml   ,
 DROP COLUMN adopt_lastname_en  ,
 DROP COLUMN adopt_lastname_ml  ;
 
ALTER TABLE eg_birth_details_audit
 ADD COLUMN adopt_decree_order_no character varying(64),
 ADD COLUMN adopt_dateoforder_decree bigint,
 ADD COLUMN adopt_agency_contact_person  character varying(64) ,
 ADD COLUMN adopt_agency_contact_person_mobileno character varying(12),
 ADD COLUMN old_birth_reg_no character varying(64),
 DROP COLUMN adopt_firstname_en,
 DROP COLUMN adopt_firstname_ml  ,    
 DROP COLUMN adopt_middlename_en   ,
 DROP COLUMN adopt_middlename_ml   ,
 DROP COLUMN adopt_lastname_en  ,
 DROP COLUMN adopt_lastname_ml  ;
 
 ALTER TABLE eg_register_birth_details 
 ADD COLUMN    old_birth_reg_no character varying(64),
 ADD COLUMN    adopt_deed_order_no character varying(64),
 ADD COLUMN    adopt_dateoforder_deed bigint,
 ADD COLUMN    adopt_issuing_auththority character varying(64),
 ADD COLUMN    adopt_has_agency boolean,
 ADD COLUMN    adopt_agency_name character varying(2000) ,
 ADD COLUMN    adopt_agency_address character varying(5000),
 ADD COLUMN    adopt_decree_order_no character varying(64) ,
 ADD COLUMN    adopt_dateoforder_decree bigint,
 ADD COLUMN    adopt_agency_contact_person character varying(64),
 ADD COLUMN   adopt_agency_contact_person_mobileno character varying(12) ;
	
 
 ALTER TABLE eg_register_birth_details_audit 
 ADD COLUMN    old_birth_reg_no character varying(64),
 ADD COLUMN    adopt_deed_order_no character varying(64),
 ADD COLUMN    adopt_dateoforder_deed bigint,
 ADD COLUMN    adopt_issuing_auththority character varying(64),
 ADD COLUMN    adopt_has_agency boolean,
 ADD COLUMN    adopt_agency_name character varying(2000) ,
 ADD COLUMN    adopt_agency_address character varying(5000),
 ADD COLUMN    adopt_decree_order_no character varying(64) ,
 ADD COLUMN    adopt_dateoforder_decree bigint,
 ADD COLUMN    adopt_agency_contact_person character varying(64),
 ADD COLUMN    adopt_agency_contact_person_mobileno character varying(12) ;

