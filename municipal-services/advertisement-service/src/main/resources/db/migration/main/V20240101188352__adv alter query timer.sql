ALTER TABLE public.eg_adv_payment_timer
ADD COLUMN booking_start_date date,
ADD COLUMN booking_end_date date,
ADD COLUMN tenant_id character varying(64);