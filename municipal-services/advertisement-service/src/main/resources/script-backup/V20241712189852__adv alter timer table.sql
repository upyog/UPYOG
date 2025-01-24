ALTER TABLE eg_adv_payment_timer 
RENAME COLUMN created_by TO createdby;

ALTER TABLE eg_adv_payment_timer 
RENAME COLUMN created_time TO createdtime;

ALTER TABLE eg_adv_payment_timer 
RENAME COLUMN last_modified_by TO lastmodifiedby;

ALTER TABLE eg_adv_payment_timer 
RENAME COLUMN last_modified_time TO lastmodifiedtime;