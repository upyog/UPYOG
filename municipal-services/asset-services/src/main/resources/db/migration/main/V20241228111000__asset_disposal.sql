CREATE TABLE IF NOT EXISTS public.eg_asset_disposal_details
(
    disposal_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    asset_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    tenant_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    life_of_asset bigint,
    current_age_of_asset bigint,
    is_asset_disposed_in_facility boolean,
    disposal_date bigint NOT NULL,
    reason_for_disposal character varying(255) COLLATE pg_catalog."default",
    amount_received double precision DEFAULT 0.0,
    purchaser_name character varying(255) COLLATE pg_catalog."default",
    payment_mode character varying(50) COLLATE pg_catalog."default",
    receipt_number character varying(255) COLLATE pg_catalog."default",
    comments text COLLATE pg_catalog."default",
    gl_code character varying(255) COLLATE pg_catalog."default",
    created_at bigint,
    created_by character varying(255) COLLATE pg_catalog."default",
    updated_at bigint,
    updated_by character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT eg_asset_disposal_details_pkey PRIMARY KEY (disposal_id),
    CONSTRAINT unique_asset_id UNIQUE (asset_id),
    CONSTRAINT fk_asset FOREIGN KEY (asset_id)
        REFERENCES public.eg_asset_assetdetails (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)
