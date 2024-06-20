ALTER TABLE public.work ADD createdby varchar(64) NULL;

ALTER TABLE public.work ADD lastmodifiedby varchar(64) NULL;

ALTER TABLE public.work ADD createdtime int8 NULL;

ALTER TABLE public.work ADD lastmodifiedtime int8 NULL;


ALTER TABLE public.work ALTER work_id TYPE varchar(64) ;