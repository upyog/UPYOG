ALTER TABLE public.eg_pg_transactions ADD txn_settlement_status varchar(64) NULL;
ALTER TABLE public.eg_pg_transactions ADD settlement_response jsonb NULL;
