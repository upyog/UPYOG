ALTER TABLE upyog_rs_mobile_toilet_booking_details
ADD COLUMN address_detail_id character varying(64);

ALTER TABLE upyog_rs_tanker_booking_details
ADD COLUMN address_detail_id character varying(64);

ALTER TABLE upyog_rs_tanker_booking_details
ADD COLUMN mobile_number character varying(12);

ALTER TABLE upyog_rs_tanker_booking_details
ADD COLUMN locality_code character varying(30);

ALTER TABLE upyog_rs_mobile_toilet_booking_details_auditdetails
ADD COLUMN address_detail_id character varying(64);

ALTER TABLE upyog_rs_tanker_booking_details_auditdetails
ADD COLUMN address_detail_id character varying(64);

ALTER TABLE upyog_rs_tanker_booking_details_auditdetails
ADD COLUMN mobile_number character varying(12);

ALTER TABLE upyog_rs_tanker_booking_details_auditdetails
ADD COLUMN locality_code character varying(30);