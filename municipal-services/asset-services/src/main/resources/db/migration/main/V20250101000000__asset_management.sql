-- Table: public.eg_asset_assetdetails

-- DROP TABLE IF EXISTS public.eg_asset_assetdetails;

CREATE TABLE IF NOT EXISTS public.eg_asset_assetdetails
(
    id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    name character varying(256) COLLATE pg_catalog."default" NOT NULL,
    description text COLLATE pg_catalog."default",
    classification character varying(256) COLLATE pg_catalog."default",
    parentcategory character varying(256) COLLATE pg_catalog."default",
    category character varying(256) COLLATE pg_catalog."default",
    subcategory character varying(256) COLLATE pg_catalog."default",
    applicationno character varying(64) COLLATE pg_catalog."default",
    approvalno character varying(64) COLLATE pg_catalog."default" DEFAULT NULL::character varying,
    tenantid character varying(256) COLLATE pg_catalog."default",
    status character varying(64) COLLATE pg_catalog."default",
    businessservice character varying(64) COLLATE pg_catalog."default" DEFAULT NULL::character varying,
    additionaldetails jsonb,
    createdtime bigint,
    lastmodifiedtime bigint,
    approvaldate bigint,
    applicationdate bigint,
    accountid character varying(64) COLLATE pg_catalog."default",
    createdby character varying(256) COLLATE pg_catalog."default",
    lastmodifiedby character varying(256) COLLATE pg_catalog."default",
    remarks character varying(256) COLLATE pg_catalog."default",
    unitofmeasurement bigint,
    purchasedate bigint,
    assettype character varying(256) COLLATE pg_catalog."default",
    bookrefno character varying(256) COLLATE pg_catalog."default",
    acquisitioncost bigint,
    islegacydata boolean DEFAULT false,
    minimumvalue double precision DEFAULT 0.0,
    CONSTRAINT pk_eg_asset_assetdetails PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.eg_asset_assetdetails
    OWNER to  postgres;

COMMENT ON TABLE public.eg_asset_assetdetails
    IS 'Table to store asset details';

-- Table: public.eg_asset_addressdetails

-- DROP TABLE IF EXISTS public.eg_asset_addressdetails;

CREATE TABLE IF NOT EXISTS public.eg_asset_addressdetails
(
    tenantid character varying(256) COLLATE pg_catalog."default",
    doorno character varying(256) COLLATE pg_catalog."default",
    latitude numeric(10,6),
    longitude numeric(10,6),
    addressid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    addressnumber character varying(256) COLLATE pg_catalog."default",
    type character varying(256) COLLATE pg_catalog."default",
    addressline1 character varying(256) COLLATE pg_catalog."default",
    addressline2 character varying(256) COLLATE pg_catalog."default",
    landmark character varying(256) COLLATE pg_catalog."default",
    city character varying(256) COLLATE pg_catalog."default",
    pincode character varying(10) COLLATE pg_catalog."default",
    detail character varying(256) COLLATE pg_catalog."default",
    buildingname character varying(256) COLLATE pg_catalog."default",
    street character varying(256) COLLATE pg_catalog."default",
    locality_code character varying(256) COLLATE pg_catalog."default",
    locality_name character varying(256) COLLATE pg_catalog."default",
    locality_label character varying(256) COLLATE pg_catalog."default",
    locality_latitude character varying(64) COLLATE pg_catalog."default",
    locality_longitude character varying(64) COLLATE pg_catalog."default",
    locality_children jsonb,
    locality_materializedpath character varying(256) COLLATE pg_catalog."default",
    asset_id character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT eg_asset_addressdetails_pkey PRIMARY KEY (addressid),
    CONSTRAINT fk_eg_asset_addressdetails_eg_asset_assetdetails FOREIGN KEY (asset_id)
        REFERENCES public.eg_asset_assetdetails (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE IF EXISTS public.eg_asset_addressdetails
    OWNER to  postgres;

-- Table: public.eg_asset_assetdetails_backup

-- DROP TABLE IF EXISTS public.eg_asset_assetdetails_backup;

CREATE TABLE IF NOT EXISTS public.eg_asset_assetdetails_backup
(
    id character varying(64) COLLATE pg_catalog."default",
    name character varying(256) COLLATE pg_catalog."default",
    description text COLLATE pg_catalog."default",
    classification character varying(256) COLLATE pg_catalog."default",
    parentcategory character varying(256) COLLATE pg_catalog."default",
    category character varying(256) COLLATE pg_catalog."default",
    subcategory character varying(256) COLLATE pg_catalog."default",
    applicationno character varying(64) COLLATE pg_catalog."default",
    approvalno character varying(64) COLLATE pg_catalog."default",
    tenantid character varying(256) COLLATE pg_catalog."default",
    status character varying(64) COLLATE pg_catalog."default",
    businessservice character varying(64) COLLATE pg_catalog."default",
    additionaldetails jsonb,
    createdtime bigint,
    lastmodifiedtime bigint,
    approvaldate bigint,
    applicationdate bigint,
    accountid character varying(64) COLLATE pg_catalog."default",
    createdby character varying(256) COLLATE pg_catalog."default",
    lastmodifiedby character varying(256) COLLATE pg_catalog."default",
    remarks character varying(256) COLLATE pg_catalog."default",
    unitofmeasurement bigint,
    purchasedate bigint,
    assettype character varying(256) COLLATE pg_catalog."default",
    bookrefno character varying(256) COLLATE pg_catalog."default"
);

ALTER TABLE IF EXISTS public.eg_asset_assetdetails_backup
    OWNER to  postgres;

-- Table: public.eg_asset_assignment_history

-- DROP TABLE IF EXISTS public.eg_asset_assignment_history;

CREATE TABLE IF NOT EXISTS public.eg_asset_assignment_history
(
    assignmentid character varying(255) COLLATE pg_catalog."default" NOT NULL,
    applicationno character varying(255) COLLATE pg_catalog."default",
    tenantid character varying(255) COLLATE pg_catalog."default",
    assignedusername character varying(255) COLLATE pg_catalog."default",
    designation character varying(255) COLLATE pg_catalog."default",
    department character varying(255) COLLATE pg_catalog."default",
    assigneddate bigint,
    returndate bigint,
    createdby character varying(255) COLLATE pg_catalog."default",
    lastmodifiedby character varying(255) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    assetid character varying(255) COLLATE pg_catalog."default",
    isassigned boolean,
    employeecode character varying(64) COLLATE pg_catalog."default"
);

ALTER TABLE IF EXISTS public.eg_asset_assignment_history
    OWNER to  postgres;

-- Table: public.eg_asset_assignmentdetails

-- DROP TABLE IF EXISTS public.eg_asset_assignmentdetails;

CREATE TABLE IF NOT EXISTS public.eg_asset_assignmentdetails
(
    assignmentid character varying(255) COLLATE pg_catalog."default" NOT NULL,
    applicationno character varying(255) COLLATE pg_catalog."default",
    tenantid character varying(255) COLLATE pg_catalog."default",
    assignedusername character varying(255) COLLATE pg_catalog."default",
    designation character varying(255) COLLATE pg_catalog."default",
    department character varying(255) COLLATE pg_catalog."default",
    assigneddate bigint,
    returndate bigint,
    createdby character varying(255) COLLATE pg_catalog."default",
    lastmodifiedby character varying(255) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    assetid character varying(255) COLLATE pg_catalog."default",
    isassigned boolean,
    employeecode character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT eg_asset_assignentdetails_pkey PRIMARY KEY (assignmentid),
    CONSTRAINT unique_assetid UNIQUE (assetid),
    CONSTRAINT eg_asset_assignentdetails_assetid_fkey FOREIGN KEY (assetid)
        REFERENCES public.eg_asset_assetdetails (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE IF EXISTS public.eg_asset_assignmentdetails
    OWNER to  postgres;

-- Table: public.eg_asset_auditdetails

-- DROP TABLE IF EXISTS public.eg_asset_auditdetails;

CREATE TABLE IF NOT EXISTS public.eg_asset_auditdetails
(
    id character varying(64) COLLATE pg_catalog."default",
    name character varying(256) COLLATE pg_catalog."default" NOT NULL,
    description text COLLATE pg_catalog."default",
    classification character varying(256) COLLATE pg_catalog."default",
    parentcategory character varying(256) COLLATE pg_catalog."default",
    category character varying(256) COLLATE pg_catalog."default",
    subcategory character varying(256) COLLATE pg_catalog."default",
    applicationno character varying(64) COLLATE pg_catalog."default",
    approvalno character varying(64) COLLATE pg_catalog."default" DEFAULT NULL::character varying,
    tenantid character varying(256) COLLATE pg_catalog."default",
    status character varying(64) COLLATE pg_catalog."default",
    businessservice character varying(64) COLLATE pg_catalog."default" DEFAULT NULL::character varying,
    additionaldetails jsonb,
    createdtime bigint,
    lastmodifiedtime bigint,
    approvaldate bigint,
    applicationdate bigint,
    accountid character varying(64) COLLATE pg_catalog."default",
    createdby character varying(256) COLLATE pg_catalog."default",
    lastmodifiedby character varying(256) COLLATE pg_catalog."default",
    remarks character varying(256) COLLATE pg_catalog."default",
    unitofmeasurement bigint,
    purchasedate bigint,
    assettype character varying(256) COLLATE pg_catalog."default",
    bookrefno character varying(256) COLLATE pg_catalog."default",
    acquisitioncost bigint,
    islegacydata boolean DEFAULT false,
    minimumvalue double precision DEFAULT 0.0
);

ALTER TABLE IF EXISTS public.eg_asset_auditdetails
    OWNER to  postgres;

-- Table: public.eg_asset_depreciation_details

-- DROP TABLE IF EXISTS public.eg_asset_depreciation_details;

CREATE TABLE IF NOT EXISTS public.eg_asset_depreciation_details
(
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    asset_id character varying(64) COLLATE pg_catalog."default",
    book_value double precision,
    depreciation_value double precision,
    from_date date,
    to_date date,
    rate double precision,
    old_book_value double precision DEFAULT 0.0,
    updated_by character varying(64) COLLATE pg_catalog."default",
    created_by character varying(64) COLLATE pg_catalog."default" DEFAULT 'system'::character varying,
    created_at bigint DEFAULT ((EXTRACT(epoch FROM now()) * (1000)::numeric))::bigint,
    updated_at bigint DEFAULT ((EXTRACT(epoch FROM now()) * (1000)::numeric))::bigint,
    depreciation_method character varying(50) COLLATE pg_catalog."default",
    CONSTRAINT eg_asset_depreciation_details_pkey PRIMARY KEY (id),
    CONSTRAINT unique_asset_depreciation UNIQUE (asset_id, from_date, to_date)
);

ALTER TABLE IF EXISTS public.eg_asset_depreciation_details
    OWNER to  postgres;

-- Table: public.eg_asset_disposal_details

-- DROP TABLE IF EXISTS public.eg_asset_disposal_details;

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
    asset_disposal_status character varying(50) COLLATE pg_catalog."default",
    additional_details jsonb,
    CONSTRAINT eg_asset_disposal_details_pkey PRIMARY KEY (disposal_id),
    CONSTRAINT unique_asset_id UNIQUE (asset_id),
    CONSTRAINT fk_asset FOREIGN KEY (asset_id)
        REFERENCES public.eg_asset_assetdetails (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

ALTER TABLE IF EXISTS public.eg_asset_disposal_details
    OWNER to  postgres;

-- Table: public.eg_asset_disposal_documents

-- DROP TABLE IF EXISTS public.eg_asset_disposal_documents;

CREATE TABLE IF NOT EXISTS public.eg_asset_disposal_documents
(
    documentid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    documenttype character varying(64) COLLATE pg_catalog."default",
    filestoreid character varying(64) COLLATE pg_catalog."default",
    documentuid character varying(64) COLLATE pg_catalog."default",
    docdetails jsonb,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    disposalid character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT uk_eg_asset_disposal_documents PRIMARY KEY (documentid)
);

ALTER TABLE IF EXISTS public.eg_asset_disposal_documents
    OWNER to  postgres;

-- Table: public.eg_asset_document

-- DROP TABLE IF EXISTS public.eg_asset_document;

CREATE TABLE IF NOT EXISTS public.eg_asset_document
(
    documentid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    documenttype character varying(64) COLLATE pg_catalog."default",
    filestoreid character varying(64) COLLATE pg_catalog."default",
    documentuid character varying(64) COLLATE pg_catalog."default",
    docdetails jsonb,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    assetid character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT uk_eg_asset_document PRIMARY KEY (documentid)
);

ALTER TABLE IF EXISTS public.eg_asset_document
    OWNER to  postgres;

-- Table: public.eg_asset_maintenance

-- DROP TABLE IF EXISTS public.eg_asset_maintenance;

CREATE TABLE IF NOT EXISTS public.eg_asset_maintenance
(
    maintenance_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    asset_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    current_life_of_asset character varying(255) COLLATE pg_catalog."default",
    is_warranty_expired boolean,
    is_amc_expired boolean,
    warranty_status character varying(64) COLLATE pg_catalog."default",
    amc_details text COLLATE pg_catalog."default",
    maintenance_type character varying(64) COLLATE pg_catalog."default",
    payment_type character varying(64) COLLATE pg_catalog."default",
    cost_of_maintenance numeric(18,2),
    vendor character varying(255) COLLATE pg_catalog."default",
    maintenance_cycle character varying(50) COLLATE pg_catalog."default",
    parts_added_or_replaced text COLLATE pg_catalog."default",
    additional_details jsonb,
    created_by character varying(64) COLLATE pg_catalog."default",
    created_time bigint,
    last_modified_by character varying(64) COLLATE pg_catalog."default",
    last_modified_time bigint,
    post_condition_remarks text COLLATE pg_catalog."default",
    pre_condition_remarks text COLLATE pg_catalog."default",
    description text COLLATE pg_catalog."default",
    asset_maintenance_date bigint,
    asset_next_maintenance_date bigint,
    tenant_id character varying(64) COLLATE pg_catalog."default",
    asset_maintenance_eg_asset_assetassignment character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT eg_asset_maintenance_pkey PRIMARY KEY (maintenance_id),
    CONSTRAINT fk_asset FOREIGN KEY (asset_id)
        REFERENCES public.eg_asset_assetdetails (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);

ALTER TABLE IF EXISTS public.eg_asset_maintenance
    OWNER to  postgres;

-- Table: public.eg_asset_maintenance_documents

-- DROP TABLE IF EXISTS public.eg_asset_maintenance_documents;

CREATE TABLE IF NOT EXISTS public.eg_asset_maintenance_documents
(
    documentid character varying(64) COLLATE pg_catalog."default" NOT NULL,
    documenttype character varying(64) COLLATE pg_catalog."default",
    filestoreid character varying(64) COLLATE pg_catalog."default",
    documentuid character varying(64) COLLATE pg_catalog."default",
    docdetails jsonb,
    createdby character varying(64) COLLATE pg_catalog."default",
    lastmodifiedby character varying(64) COLLATE pg_catalog."default",
    createdtime bigint,
    lastmodifiedtime bigint,
    maintenanceid character varying(64) COLLATE pg_catalog."default",
    CONSTRAINT uk_eg_asset_maintenance_documents PRIMARY KEY (documentid)
);

ALTER TABLE IF EXISTS public.eg_asset_maintenance_documents
    OWNER to  postgres;

-- Table: public.eg_asset_inventory

-- DROP TABLE IF EXISTS public.eg_asset_inventory;

CREATE TABLE IF NOT EXISTS public.eg_asset_inventory
(
    inventory_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    asset_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    tenant_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    purchase_date bigint,
    purchase_mode character varying(64) COLLATE pg_catalog."default",
    vendor_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    delivery_date bigint,
    end_of_life bigint,
    end_of_support bigint,
    quantity integer,
    unit_price numeric(18,2),
    total_price numeric(18,2),
    created_by character varying(64) COLLATE pg_catalog."default",
    created_time bigint,
    last_modified_by character varying(64) COLLATE pg_catalog."default",
    last_modified_time bigint,
    CONSTRAINT eg_asset_inventory_pkey PRIMARY KEY (inventory_id),
    CONSTRAINT fk_asset_inventory FOREIGN KEY (asset_id)
        REFERENCES public.eg_asset_assetdetails (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT fk_inventory_vendor FOREIGN KEY (vendor_id)
        REFERENCES public.eg_asset_vendor (vendor_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE RESTRICT
);

ALTER TABLE IF EXISTS public.eg_asset_inventory
    OWNER to  postgres;

-- Table: public.eg_asset_vendor

-- DROP TABLE IF EXISTS public.eg_asset_vendor;

CREATE TABLE IF NOT EXISTS public.eg_asset_vendor
(
    vendor_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    vendor_number character varying(20) COLLATE pg_catalog."default" NOT NULL,
    vendor_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    contact_person character varying(255) COLLATE pg_catalog."default",
    contact_number character varying(20) COLLATE pg_catalog."default",
    contact_email character varying(100) COLLATE pg_catalog."default",
    gstin character varying(15) COLLATE pg_catalog."default",
    pan character varying(10) COLLATE pg_catalog."default",
    vendor_address text COLLATE pg_catalog."default",
    tenant_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    status character varying(20) COLLATE pg_catalog."default" DEFAULT 'ACTIVE'::character varying,
    created_by character varying(64) COLLATE pg_catalog."default",
    created_time bigint,
    last_modified_by character varying(64) COLLATE pg_catalog."default",
    last_modified_time bigint,
    CONSTRAINT eg_asset_vendor_pkey PRIMARY KEY (vendor_id),
    CONSTRAINT unique_vendor_number UNIQUE (vendor_number)
);

ALTER TABLE IF EXISTS public.eg_asset_vendor
    OWNER to  postgres;

COMMENT ON TABLE public.eg_asset_vendor
    IS 'Table to store vendor details for asset inventory management';

-- Table: public.eg_asset_inventory_procurement_request

-- DROP TABLE IF EXISTS public.eg_asset_inventory_procurement_request;

CREATE TABLE IF NOT EXISTS public.eg_asset_inventory_procurement_request
(
    request_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    item character varying(255) COLLATE pg_catalog."default" NOT NULL,
    item_type character varying(255) COLLATE pg_catalog."default" NOT NULL,
    quantity integer NOT NULL,
    asset_application_number character varying(64) COLLATE pg_catalog."default" NOT NULL,
    tenant_id character varying(64) COLLATE pg_catalog."default" NOT NULL,
    status character varying(50) COLLATE pg_catalog."default" DEFAULT 'PENDING'::character varying,
    created_by character varying(64) COLLATE pg_catalog."default",
    created_time bigint,
    last_modified_by character varying(64) COLLATE pg_catalog."default",
    last_modified_time bigint,
    CONSTRAINT eg_asset_inventory_procurement_request_pkey PRIMARY KEY (request_id)
);

ALTER TABLE IF EXISTS public.eg_asset_inventory_procurement_request
    OWNER to  postgres;

COMMENT ON TABLE public.eg_asset_inventory_procurement_request
    IS 'Table to store inventory procurement requests before creating actual inventory';

-- Add inventory_status column to eg_asset_inventory if not exists
ALTER TABLE public.eg_asset_inventory 
ADD COLUMN IF NOT EXISTS inventory_status character varying(20) DEFAULT 'ACTIVE';

-- Add procurement_request_id column to link inventory with approved procurement requests
ALTER TABLE public.eg_asset_inventory 
ADD COLUMN IF NOT EXISTS procurement_request_id character varying(64);

-- Add insurance_applicability column with YES/NO values
ALTER TABLE public.eg_asset_inventory 
ADD COLUMN IF NOT EXISTS insurance_applicability character varying(3) DEFAULT 'NO' CHECK (insurance_applicability IN ('YES', 'NO'));

-- Add vendor_number column and make vendor_id optional
ALTER TABLE public.eg_asset_inventory 
ADD COLUMN IF NOT EXISTS vendor_number character varying(20);

-- Make vendor_id nullable
ALTER TABLE public.eg_asset_inventory 
ALTER COLUMN vendor_id DROP NOT NULL;

-- Make vendor_id NOT NULL and enforce strict foreign key validation
ALTER TABLE public.eg_asset_inventory 
ALTER COLUMN vendor_id SET NOT NULL;

-- Drop and recreate foreign key constraint with RESTRICT
ALTER TABLE public.eg_asset_inventory 
DROP CONSTRAINT IF EXISTS fk_inventory_vendor;

ALTER TABLE public.eg_asset_inventory 
ADD CONSTRAINT fk_inventory_vendor FOREIGN KEY (vendor_id)
    REFERENCES public.eg_asset_vendor (vendor_id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE RESTRICT;

-- Add foreign key constraint for procurement request
ALTER TABLE public.eg_asset_inventory 
ADD CONSTRAINT fk_inventory_procurement_request FOREIGN KEY (procurement_request_id)
    REFERENCES public.eg_asset_inventory_procurement_request (request_id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE RESTRICT;