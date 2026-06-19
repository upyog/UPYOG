ALTER TABLE public.eg_adv_cart_detail
ADD COLUMN IF NOT EXISTS booking_end_date date;
