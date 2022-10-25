ALTER TABLE eg_tl_tradelicensedetail
ADD COLUMN businesssector character varying(64),
ADD COLUMN capitalinvestment numeric(14,2),
ADD COLUMN enterprisetype character varying(64);