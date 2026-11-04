ALTER TABLE public.upyog_rs_tree_pruning_booking_detail
    ADD COLUMN IF NOT EXISTS additional_details JSONB;

-- Add additional_details before audit_timestamp so persister can use SELECT *, CURRENT_TIMESTAMP
ALTER TABLE public.upyog_rs_tree_pruning_booking_detail_audit DROP COLUMN IF EXISTS additional_details;
ALTER TABLE public.upyog_rs_tree_pruning_booking_detail_audit DROP COLUMN IF EXISTS audit_timestamp;
ALTER TABLE public.upyog_rs_tree_pruning_booking_detail_audit ADD COLUMN additional_details JSONB;
ALTER TABLE public.upyog_rs_tree_pruning_booking_detail_audit ADD COLUMN audit_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
