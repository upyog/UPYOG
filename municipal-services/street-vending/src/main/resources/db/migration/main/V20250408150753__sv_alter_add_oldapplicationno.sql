ALTER TABLE eg_sv_street_vending_detail
ADD COLUMN IF NOT EXISTS old_application_no VARCHAR(64);

ALTER TABLE eg_sv_street_vending_detail_auditdetails
ADD COLUMN IF NOT EXISTS old_application_no VARCHAR(64);

ALTER TABLE public.eg_sv_street_vending_detail
DROP CONSTRAINT IF EXISTS eg_sv_street_vending_detail_certificate_no_key;


