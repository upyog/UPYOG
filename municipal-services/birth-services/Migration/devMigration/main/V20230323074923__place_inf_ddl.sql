alter table public.eg_birth_place
    ADD COLUMN  ot_country character varying(200) COLLATE pg_catalog."default",
    ADD COLUMN  ot_town_village_en character varying(1000) COLLATE pg_catalog."default";

alter table public.eg_birth_place_audit
    ADD COLUMN  ot_country character varying(200) COLLATE pg_catalog."default",
    ADD COLUMN  ot_town_village_en character varying(1000) COLLATE pg_catalog."default";