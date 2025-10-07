ALTER TABLE IF EXISTS public.eg_land_geolocation
    ADD CONSTRAINT pk PRIMARY KEY (id);

ALTER TABLE IF EXISTS public.eg_land_geolocation
    ADD CONSTRAINT uq_adrs_id_let_long UNIQUE (latitude, longitude, addressid);