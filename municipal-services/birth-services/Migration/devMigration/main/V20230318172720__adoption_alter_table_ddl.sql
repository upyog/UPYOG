ALTER TABLE eg_birth_details
 ADD COLUMN adopt_decree_order_no character varying(64),
 ADD COLUMN adopt_dateoforder_decree bigint,    
 ADD COLUMN adopt_agency_contact_person  character varying(64) ,
 ADD COLUMN adopt_agency_contact_person_mobileno character varying(12);
