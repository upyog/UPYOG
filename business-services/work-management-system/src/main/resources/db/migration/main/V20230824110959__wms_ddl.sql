ALTER TABLE public.state_sor ADD createdby varchar(64) NULL;

ALTER TABLE public.state_sor ADD lastmodifiedby varchar(64) NULL;

ALTER TABLE public.state_sor ADD createdtime int8 NULL;

ALTER TABLE public.state_sor ADD lastmodifiedtime int8 NULL;

ALTER TABLE public.state_sor ALTER sor_id TYPE varchar(64) ;

