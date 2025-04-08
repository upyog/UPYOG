ALTER TABLE public.eg_grbg_bill_tracker
ADD COLUMN ward varchar(256) DEFAULT NULL,
ADD COLUMN bill_id varchar(256) DEFAULT NULL