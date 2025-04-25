ALTER TABLE public.eg_user_address
ADD COLUMN status character varying(50);

ALTER TABLE public.eg_user_address
DROP CONSTRAINT IF EXISTS eg_user_address_type_unique;

ALTER TABLE public.eg_user_address
ADD CONSTRAINT eg_user_address_type_unique UNIQUE (userid, tenantid, type, createddate);