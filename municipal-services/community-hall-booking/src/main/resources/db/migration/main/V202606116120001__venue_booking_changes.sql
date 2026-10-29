ALTER TABLE eg_chb_booking_detail RENAME COLUMN community_hall_code TO venue_code;

ALTER TABLE eg_chb_slot_detail RENAME COLUMN hall_code TO unit_code;

alter table eg_chb_booking_detail add column venue_type varchar(50);


ALTER TABLE eg_chb_booking_detail_audit RENAME COLUMN community_hall_code TO venue_code;


alter table eg_chb_booking_detail_audit add column venue_type varchar(50);


alter table eg_chb_payment_timer RENAME COLUMN hall_code TO unit_code;

alter table eg_chb_payment_timer RENAME COLUMN community_hall_code TO venue_code;


alter table eg_chb_payment_timer add COLUMN start_time time ;
alter table eg_chb_payment_timer add COLUMN end_time time  ;