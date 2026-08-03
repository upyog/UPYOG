ALTER TABLE public.eg_ndc_details DROP COLUMN dueamount;

ALTER TABLE public.eg_ndc_details
ADD COLUMN isduepending boolean NOT NULL DEFAULT false;