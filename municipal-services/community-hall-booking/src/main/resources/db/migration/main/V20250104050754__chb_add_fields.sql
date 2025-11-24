ALTER TABLE public.eg_chb_payment_timer
ADD COLUMN timer_id serial PRIMARY KEY;

ALTER TABLE public.eg_chb_payment_timer
ADD COLUMN booking_no character varying(64) ;

ALTER TABLE public.eg_chb_payment_timer
ADD COLUMN community_hall_code character varying(64) ;

ALTER TABLE public.eg_chb_payment_timer
ADD COLUMN hall_code character varying(64) ;

ALTER TABLE public.eg_chb_payment_timer
ADD COLUMN lastmodifiedby character varying(64) ;

ALTER TABLE public.eg_chb_payment_timer
ADD COLUMN lastmodifiedtime bigint;

ALTER TABLE public.eg_chb_payment_timer
ADD COLUMN booking_date date;

ALTER TABLE public.eg_chb_payment_timer
ADD COLUMN tenant_id character varying(64);
