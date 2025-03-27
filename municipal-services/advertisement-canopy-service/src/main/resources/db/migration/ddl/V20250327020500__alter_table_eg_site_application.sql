ALTER TABLE public.eg_site_application ALTER COLUMN size_length TYPE numeric(10, 2) USING size_length::numeric(10, 2);
ALTER TABLE public.eg_site_application ALTER COLUMN size_width TYPE numeric(10, 2) USING size_width::numeric(10, 2);
