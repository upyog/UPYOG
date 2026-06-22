-- Store the overall booking date range on the booking header
-- so the frontend can display "Jun 1 – Jun 7" from a single record
-- instead of relying on duplicated values across cart entries.
ALTER TABLE public.eg_adv_booking_detail
ADD COLUMN IF NOT EXISTS booking_start_date date;

ALTER TABLE public.eg_adv_booking_detail
ADD COLUMN IF NOT EXISTS booking_end_date date;

ALTER TABLE public.eg_adv_booking_detail_audit
ADD COLUMN IF NOT EXISTS booking_start_date date;

ALTER TABLE public.eg_adv_booking_detail_audit
ADD COLUMN IF NOT EXISTS booking_end_date date;
