ALTER TABLE upyog_rs_tanker_booking_details
ADD COLUMN water_type character varying(64);

ALTER TABLE upyog_rs_tanker_booking_details_auditdetails
ADD COLUMN water_type character varying(64);