ALTER TABLE COMMUNITY_HALL_BOOKING_DETAILS DROP COLUMN approval_no, DROP COLUMN  approval_date;

ALTER TABLE BOOKING_SLOT_DETAILS DROP COLUMN booking_slot_dateTime;

ALTER TABLE COMMUNITY_HALL_BOOKING_DETAILS 
ADD COLUMN application_no  character varying(64),
ADD COLUMN application_date bigint,
ADD COLUMN applicant_name  character varying(100)  NOT NULL,
ADD COLUMN community_hall_name  character varying(200),
ADD COLUMN applicant_email_id  character varying(100)  NOT NULL,
ADD COLUMN applicant_mobile_no  character varying(12),
ADD COLUMN applicant_alternate_mobile_no  character varying(12)  NOT NULL;
  
  
ALTER TABLE BOOKING_SLOT_DETAILS  
ADD COLUMN  hall_name character varying(100),
ADD COLUMN  booking_date character varying(20) NOT NULL,
ADD COLUMN  booking_from_time character varying(20) NOT NULL,
ADD COLUMN  booking_to_time character varying(20) NOT NULL;

