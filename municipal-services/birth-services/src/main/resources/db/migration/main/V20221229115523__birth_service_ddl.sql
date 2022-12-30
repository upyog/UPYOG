ALTER TABLE public.eg_birth_father_information ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_birth_mother_information ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_birth_permanent_address ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_birth_present_address ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";

ALTER TABLE public.eg_birth_father_information_audit ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_birth_mother_information_audit ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_birth_permanent_address_audit ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_birth_present_address_audit ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";


ALTER TABLE public.eg_register_birth_father_information ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_register_birth_mother_information ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_register_birth_permanent_address ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_register_birth_present_address ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";


ALTER TABLE public.eg_register_birth_father_information_audit ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_register_birth_mother_information_audit ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_register_birth_permanent_address_audit ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";
ALTER TABLE public.eg_register_birth_present_address_audit ADD COLUMN bio_adopt character varying(64) COLLATE pg_catalog."default";


