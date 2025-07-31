ALTER TABLE public.eg_grbg_collection_unit
	ADD COLUMN isbplunit BOOLEAN DEFAULT FALSE,
	ADD COLUMN isvariablecalculation BOOLEAN DEFAULT FALSE,
	ADD COLUMN isbulkgeneration BOOLEAN DEFAULT FALSE,
	ADD COLUMN no_of_units INTEGER DEFAULT 0;