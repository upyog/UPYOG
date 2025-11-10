-- Add JSONB column to main booking table (if not already present)
ALTER TABLE IF EXISTS public.eg_chb_booking_detail
    ADD COLUMN IF NOT EXISTS additional_details JSONB;

-- Add JSONB column to audit table (if not already present)
ALTER TABLE IF EXISTS public.eg_chb_booking_detail_audit
    ADD COLUMN IF NOT EXISTS additional_details JSONB;

