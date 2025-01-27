ALTER TABLE eg_chb_booking_detail ADD COLUMN additionaldetail JSONB;

ALTER TABLE eg_chb_slot_detail 
ADD COLUMN additionaldetail JSONB,
ADD COLUMN booking_to_date character varying(20) DEFAULT 'default_value' NOT NULL;

ALTER TABLE eg_chb_applicant_detail ADD COLUMN additionaldetail JSONB;

ALTER TABLE eg_chb_address_detail ADD COLUMN additionaldetail JSONB;


