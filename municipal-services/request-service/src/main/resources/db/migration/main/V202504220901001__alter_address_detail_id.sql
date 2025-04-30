ALTER TABLE upyog_rs_mobile_toilet_booking_details
ADD COLUMN address_detail_id character varying(64);

ALTER TABLE upyog_rs_tanker_booking_details
ADD COLUMN address_detail_id character varying(64);

ALTER TABLE upyog_rs_mobile_toilet_booking_details_auditdetails
ADD COLUMN address_detail_id character varying(64);

ALTER TABLE upyog_rs_tanker_booking_details_auditdetails
ADD COLUMN address_detail_id character varying(64);