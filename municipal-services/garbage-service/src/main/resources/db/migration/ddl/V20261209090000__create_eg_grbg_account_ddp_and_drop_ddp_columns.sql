CREATE TABLE IF NOT EXISTS public.eg_grbg_account_ddp (
    garbage_account_uuid VARCHAR(255) NOT NULL,
    garbage_id int8 NOT NULL,
    is_ready_for_printing boolean DEFAULT false,
    vendor_print_verified VARCHAR(20),
    ulb_verified boolean DEFAULT false,
    installation_done boolean DEFAULT false,
    ddp_latitude VARCHAR(64),
    ddp_longitude VARCHAR(64),
    ddp_printing_done boolean DEFAULT false,
    ddp_dispatched boolean DEFAULT false,
    ddp_rejection_reason VARCHAR(1000),
    remarks VARCHAR(1000),
    CONSTRAINT pk_eg_grbg_account_ddp PRIMARY KEY (garbage_account_uuid),
    CONSTRAINT uk_eg_grbg_account_ddp_garbage_id UNIQUE (garbage_id)
);

-- carry over existing DDP data (only rows that actually have some DDP state;
-- accounts without a row here are treated as "all defaults")
INSERT INTO public.eg_grbg_account_ddp (garbage_account_uuid, garbage_id, is_ready_for_printing, vendor_print_verified,
    ulb_verified, installation_done, ddp_latitude, ddp_longitude, ddp_printing_done, ddp_dispatched)
SELECT acc.uuid, acc.garbage_id, COALESCE(acc.is_ready_for_printing, false), acc.vendor_print_verified,
    COALESCE(acc.ulb_verified, false), COALESCE(acc.installation_done, false), acc.ddp_latitude, acc.ddp_longitude,
    COALESCE(acc.ddp_printing_done, false), COALESCE(acc.ddp_dispatched, false)
FROM public.eg_grbg_account acc
WHERE acc.uuid IS NOT NULL
  AND (acc.is_ready_for_printing = true
    OR acc.vendor_print_verified IS NOT NULL
    OR acc.ulb_verified = true
    OR acc.installation_done = true
    OR acc.ddp_latitude IS NOT NULL
    OR acc.ddp_longitude IS NOT NULL
    OR acc.ddp_printing_done = true
    OR acc.ddp_dispatched = true)
ON CONFLICT DO NOTHING;

ALTER TABLE public.eg_grbg_account DROP COLUMN IF EXISTS is_ready_for_printing;
ALTER TABLE public.eg_grbg_account DROP COLUMN IF EXISTS vendor_print_verified;
ALTER TABLE public.eg_grbg_account DROP COLUMN IF EXISTS ulb_verified;
ALTER TABLE public.eg_grbg_account DROP COLUMN IF EXISTS installation_done;
ALTER TABLE public.eg_grbg_account DROP COLUMN IF EXISTS ddp_latitude;
ALTER TABLE public.eg_grbg_account DROP COLUMN IF EXISTS ddp_longitude;
ALTER TABLE public.eg_grbg_account DROP COLUMN IF EXISTS ddp_printing_done;
ALTER TABLE public.eg_grbg_account DROP COLUMN IF EXISTS ddp_dispatched;
