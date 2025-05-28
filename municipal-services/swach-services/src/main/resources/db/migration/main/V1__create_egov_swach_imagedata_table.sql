-- Create the sequence for the numeric part of the ID
CREATE SEQUENCE egov_swach_imagedata_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 999999
    CACHE 1;

-- Create the table for image data
CREATE TABLE public.egov_swach_imagedata (
    id VARCHAR(20) NOT NULL,  -- Change id to VARCHAR to store the formatted ID
    tenant_id varchar(256) NOT NULL,
    "uuid" varchar(256) NOT NULL,
    latitude varchar(50) NOT NULL,
    longitude varchar(50) NOT NULL,
    locality varchar(256) NOT NULL,
    imagerurl text NOT NULL,
    created_time BIGINT NOT NULL,
    created_by varchar(256) NULL,
    last_updated_time BIGINT NOT NULL,
    last_updated_by varchar(256) NULL,
    CONSTRAINT egov_swach_imagedata_pkey PRIMARY KEY (id)
);

-- Trigger function to create the formatted ID
CREATE OR REPLACE FUNCTION generate_image_id()
RETURNS TRIGGER AS $$
BEGIN
    NEW.id := 'IMG-' || TO_CHAR(CURRENT_DATE, 'YYYYMMDD') || '-' || LPAD(nextval('egov_swach_imagedata_id_seq')::text, 6, '0');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger that fires before each insert
CREATE TRIGGER set_image_id
BEFORE INSERT ON public.egov_swach_imagedata
FOR EACH ROW
EXECUTE FUNCTION generate_image_id();
