ALTER TABLE public.eg_sv_vendor_detail
ALTER COLUMN mobile_no TYPE character varying(100);

ALTER TABLE public.eg_sv_bank_detail
ALTER COLUMN account_no TYPE character varying(64);

ALTER TABLE public.eg_sv_bank_detail
ALTER COLUMN ifsc_code TYPE character varying(64);