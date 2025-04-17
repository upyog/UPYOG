ALTER TABLE upyog_rs_mobile_toilet_booking_details
ADD COLUMN mobile_number character varying(12);

ALTER TABLE upyog_rs_mobile_toilet_booking_details
ADD COLUMN locality_code character varying(30);

-- Add mobile_number column
ALTER TABLE upyog_rs_mobile_toilet_booking_details_auditdetails
ADD COLUMN mobile_number character varying(12);

-- Add locality_code column
ALTER TABLE upyog_rs_mobile_toilet_booking_details_auditdetails
ADD COLUMN locality_code character varying(30);
