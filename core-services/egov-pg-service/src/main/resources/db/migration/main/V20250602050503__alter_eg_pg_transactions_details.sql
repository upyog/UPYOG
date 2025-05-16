ALTER TABLE public.eg_pg_transactions_details
ADD COLUMN module VARCHAR(64),
ADD COLUMN module_id VARCHAR(64);