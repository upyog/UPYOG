ALTER TABLE public.eg_noc
ADD COLUMN nocreason varchar(512) NULL,
ADD COLUMN connectiontype varchar(64) NULL,
ADD COLUMN propertydetail jsonb NULL,
ADD COLUMN citizendetail jsonb NULL,
ADD COLUMN businessservice varchar(512) NULL;

ALTER TABLE public.eg_noc_auditdetails
ADD COLUMN nocreason varchar(512) NULL,
ADD COLUMN connectiontype varchar(64) NULL,
ADD COLUMN propertydetail jsonb NULL,
ADD COLUMN citizendetail jsonb NULL,
ADD COLUMN businessservice varchar(512) NULL;
