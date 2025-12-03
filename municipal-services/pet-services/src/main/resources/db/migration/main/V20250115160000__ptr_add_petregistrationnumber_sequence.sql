-- BEGIN TRANSACTION for creating sequence for petRegistrationNumber
BEGIN;

-- Sequence for Pet Registration Number generation
-- Used by ID generation service with format: PB-PTR-REG-[cy:yyyy-MM-dd]-[SEQ_EG_PTR_REGNUM]
CREATE SEQUENCE IF NOT EXISTS "SEQ_EG_PTR_REGNUM"
	INCREMENT BY 1
	MINVALUE 1
	MAXVALUE 9223372036854775807
	START 1
	CACHE 1
	NO CYCLE;

-- COMMIT all changes if everything succeeds
COMMIT;

