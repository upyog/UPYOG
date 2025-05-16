ALTER TABLE public.eg_pg_transactions
ADD COLUMN is_multi_transaction BOOLEAN DEFAULT FALSE;