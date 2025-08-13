ALTER TABLE public.eg_sv_street_vending_detail
DROP COLUMN enrollment_id;

ALTER TABLE public.eg_sv_street_vending_detail_auditdetails
DROP COLUMN enrollment_id;

ALTER TABLE public.eg_sv_street_vending_detail
ADD COLUMN locality character varying(64);

ALTER TABLE public.eg_sv_street_vending_detail_auditdetails
ADD COLUMN locality character varying(64);

ALTER TABLE public.eg_sv_street_vending_detail
ADD COLUMN application_created_by character varying(64);

ALTER TABLE public.eg_sv_street_vending_detail_auditdetails
ADD COLUMN application_created_by character varying(64);