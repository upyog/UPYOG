-- Add missing columns to eg_challanAddress table

ALTER TABLE eg_challanAddress
    ADD COLUMN IF NOT EXISTS addressId character varying(64),
    ADD COLUMN IF NOT EXISTS addressNumber character varying(64),
    ADD COLUMN IF NOT EXISTS type character varying(64),
    ADD COLUMN IF NOT EXISTS addressLine1 character varying(256),
    ADD COLUMN IF NOT EXISTS addressLine2 character varying(256);