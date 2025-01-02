ALTER TABLE public.eg_adv_payment_timer
ADD COLUMN add_type character varying(64) COLLATE pg_catalog."default" NOT NULL,
ADD COLUMN location character varying(64) COLLATE pg_catalog."default" NOT NULL,
ADD COLUMN face_area character varying(64) COLLATE pg_catalog."default" NOT NULL,
ADD COLUMN night_light BOOLEAN DEFAULT FALSE;