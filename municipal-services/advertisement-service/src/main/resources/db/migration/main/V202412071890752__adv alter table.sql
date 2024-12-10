ALTER TABLE eg_adv_payment_timer
DROP COLUMN lastModifiedBy,
DROP COLUMN lastModifiedTime;

ALTER TABLE public.eg_adv_payment_timer
ADD COLUMN status CHARACTER VARYING(50);

ALTER TABLE public.eg_adv_payment_timer
ADD COLUMN booking_no VARCHAR(255);

ALTER TABLE eg_adv_payment_timer
ADD COLUMN lastModifiedBy CHARACTER VARYING(64),
ADD COLUMN lastModifiedTime BIGINT;
