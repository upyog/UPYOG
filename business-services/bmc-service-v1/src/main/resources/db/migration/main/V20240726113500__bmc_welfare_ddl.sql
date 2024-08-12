ALTER TABLE IF EXISTS public.eg_bmc_aadharuser DROP COLUMN IF EXISTS aadhardob;

ALTER TABLE IF EXISTS public.eg_bmc_aadharuser
    ADD COLUMN aadhardob bigint NOT NULL defAULT 0;