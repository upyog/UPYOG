

ALTER TABLE IF EXISTS public.eg_address ALTER COLUMN id SET DEFAULT nextval('eg_address_id_seq'::regclass);

ALTER TABLE IF EXISTS public.eg_address DROP COLUMN IF EXISTS "zone";
ALTER TABLE IF EXISTS public.eg_address ADD COLUMN "zone" varchar(50);

ALTER TABLE IF EXISTS public.eg_address DROP COLUMN IF EXISTS "block";
ALTER TABLE IF EXISTS public.eg_address ADD COLUMN "block" varchar(50);

ALTER TABLE IF EXISTS public.eg_address DROP COLUMN IF EXISTS "ward";
ALTER TABLE IF EXISTS public.eg_address ADD COLUMN "ward" varchar(50);

