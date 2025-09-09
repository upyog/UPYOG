ALTER TABLE public.eg_sv_vendor_detail
ADD COLUMN vendor_payment_frequency character varying(64);

ALTER TABLE public.eg_sv_vendor_detail_auditdetails
ADD COLUMN vendor_payment_frequency character varying(64);