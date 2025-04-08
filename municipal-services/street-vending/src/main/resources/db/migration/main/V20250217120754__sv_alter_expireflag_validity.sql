ALTER TABLE public.eg_sv_street_vending_detail 
ADD COLUMN IF NOT EXISTS validity_date DATE,
ADD COLUMN IF NOT EXISTS expire_flag BOOLEAN DEFAULT FALSE;

ALTER TABLE public.eg_sv_street_vending_detail 
ADD COLUMN IF NOT EXISTS renewal_status VARCHAR;

ALTER TABLE public.eg_sv_street_vending_detail
ADD COLUMN IF NOT EXISTS old_application_no VARCHAR;