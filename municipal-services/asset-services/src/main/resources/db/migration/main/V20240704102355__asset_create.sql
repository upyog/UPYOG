-- Create ENUM types
--CREATE TYPE public.asset_classification AS ENUM ('MOVABLE', 'IMMOVABLE');
--CREATE TYPE public.parent_type AS ENUM ('LAND', 'BUILDING', 'SERVICE','OTHER');

CREATE TABLE IF NOT EXISTS eg_asset_assetdetails (
    id character varying(64),
    bookRefNo character varying(256) NOT NULL,
    name character varying(256) NOT NULL,
    description character varying(256),
    classification character varying(256),
    parentCategory character varying(256),
    category character varying(256),
    subcategory character varying(256),
    department character varying(256),
    applicationNo character varying(64),
    approvalNo character varying(64) DEFAULT NULL,
    tenantId character varying(256),
    status character varying(64),
    businessservice character varying(64) DEFAULT NULL,
    additionalDetails JSONB,
    createdTime bigint,
    lastModifiedTime bigint,
    approvalDate bigint,
    applicationDate bigint,
    accountid character varying(64),
    createdby character varying(256),
    lastmodifiedby character varying(256),
    remarks character varying(256),
    financialYear character varying(64),
    sourceOfFinance character varying(256),
    CONSTRAINT pk_eg_asset_assetdetails PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS eg_asset_auditdetails (
    id character varying(64),
    bookRefNo character varying(256) NOT NULL,
    name character varying(256) NOT NULL,
    description character varying(256),
    classification character varying(256),
    parentCategory character varying(256),
    category character varying(256),
    subcategory character varying(256),
    department character varying(256),
    applicationNo character varying(64),
    approvalNo character varying(64) DEFAULT NULL,
    tenantId character varying(256),
    status character varying(64),
    businessservice character varying(64) DEFAULT NULL,
    additionalDetails JSONB,
    createdTime bigint,
    lastModifiedTime bigint,
    approvalDate bigint,
    applicationDate bigint,
    accountid character varying(64),
    createdby character varying(256),
    lastmodifiedby character varying(256),
    remarks character varying(256),
    financialYear character varying(64),
    sourceOfFinance character varying(256)
);

CREATE TABLE IF NOT EXISTS eg_asset_addressDetails (
    tenantId character varying(256),
    doorNo character varying(256),
    latitude NUMERIC(10, 6),
    longitude NUMERIC(10, 6),
    addressId character varying(64),
    addressNumber character varying(256),
    type character varying(256),
    addressLine1 character varying(256),
    addressLine2 character varying(256),
    landmark character varying(256),
    city character varying(256),
    pincode VARCHAR(10),
    detail character varying(256),
    buildingName character varying(256),
    street character varying(256),
    locality_code character varying(256),
    locality_name character varying(256),
    locality_label character varying(256),
    locality_latitude NUMERIC(10, 6),
    locality_longitude NUMERIC(10, 6),
    locality_children JSONB,
    locality_materializedPath character varying(256),
    asset_id character varying(64),  -- Foreign Key
    CONSTRAINT eg_asset_addressdetails_pkey PRIMARY KEY (addressid),
    CONSTRAINT fk_eg_asset_addressdetails_eg_asset_assetdetails FOREIGN KEY (asset_id)
        REFERENCES public.eg_asset_assetdetails (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.eg_asset_document
(
    documentid character varying(64) NOT NULL,
    documenttype character varying(64),
    filestoreid character varying(64),
    documentuid character varying(64),
    docdetails jsonb,
    createdby character varying(64),
    lastmodifiedby character varying(64),
    createdtime bigint,
    lastmodifiedtime bigint,
    assetid character varying(64),
    CONSTRAINT uk_eg_asset_document PRIMARY KEY (documentid)
);

COMMENT ON TABLE eg_asset_assetdetails IS 'Table to store asset details';


