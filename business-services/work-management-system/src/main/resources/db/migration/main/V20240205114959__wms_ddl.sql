ALTER TABLE public.scheme_master ADD createdby varchar(64) NULL;

ALTER TABLE public.scheme_master ADD lastmodifiedby varchar(64) NULL;

ALTER TABLE public.scheme_master ADD createdtime int8 NULL;

ALTER TABLE public.scheme_master ADD lastmodifiedtime int8 NULL;

ALTER TABLE public.project_master ADD createdby varchar(64) NULL;

ALTER TABLE public.project_master ADD lastmodifiedby varchar(64) NULL;

ALTER TABLE public.project_master ADD createdtime int8 NULL;

ALTER TABLE public.project_master ADD lastmodifiedtime int8 NULL;
