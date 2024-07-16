-- Create table grbg_application
CREATE TABLE grbg_application (
    uuid VARCHAR(225) PRIMARY KEY,
    application_no VARCHAR(225),
    status VARCHAR(50),
    garbage_id int8
);

-- Add foreign key constraint
ALTER TABLE grbg_application
ADD CONSTRAINT grbg_application_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES hpudd_grbg_account (garbage_id);




-- Alter table hpudd_grbg_account
ALTER TABLE hpudd_grbg_account
ADD COLUMN user_uuid VARCHAR(225),
ADD COLUMN declaration_uuid VARCHAR(225),
ADD COLUMN grbg_coll_address_uuid VARCHAR(225),
ADD COLUMN status VARCHAR(50),
DROP COLUMN parent_id;




-- Alter table hpudd_grbg_bill
ALTER TABLE hpudd_grbg_bill
ADD COLUMN is_active BOOLEAN,
ADD COLUMN bill_for VARCHAR(50);




-- Create table grbg_commercial_details
CREATE TABLE grbg_commercial_details (
    uuid VARCHAR(225) PRIMARY KEY,
    garbage_id int8,
    business_name VARCHAR(100),
    business_type VARCHAR(100),
    owner_user_uuid VARCHAR(255)
);

-- Add foreign key constraint
ALTER TABLE grbg_commercial_details
ADD CONSTRAINT grbg_commercial_details_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES hpudd_grbg_account (garbage_id);




-- Create table grbg_collection_unit
CREATE TABLE grbg_collection_unit (
    uuid VARCHAR(225) PRIMARY KEY,
    unit_name VARCHAR(225),
    unit_ward VARCHAR(225),
    ulb_name VARCHAR(225),
    type_of_ulb VARCHAR(225)
);





-- Create table grbg_collection_staff
CREATE TABLE grbg_collection_staff (
    uuid VARCHAR(225) PRIMARY KEY,
    grbg_collection_unit_uuid VARCHAR(225),
    employee_id VARCHAR(225),
    role VARCHAR(50),
    is_active BOOLEAN
);

-- Add foreign key constraint
ALTER TABLE grbg_collection_staff
ADD CONSTRAINT grbg_collection_staff_unit_uuid_fk FOREIGN KEY (grbg_collection_unit_uuid) REFERENCES grbg_collection_unit (uuid);




-- Create table grbg_document
CREATE TABLE grbg_document (
    uuid VARCHAR(225) PRIMARY KEY,
    doc_ref_id VARCHAR(225),
    doc_name VARCHAR(100),
    doc_type VARCHAR(100),
    doc_category VARCHAR(100),
    tbl_ref_uuid VARCHAR(225)
);




-- Create table grbg_charge
CREATE TABLE grbg_charge (
    uuid VARCHAR(225) PRIMARY KEY,
    category VARCHAR(100),
    type VARCHAR(225),
    amount_per_day DECIMAL(10, 2),
    amount_pm DECIMAL(10, 2),
    is_active BOOLEAN
);




-- Create table grbg_collection
CREATE TABLE grbg_collection (
    uuid VARCHAR(225) PRIMARY KEY,
    garbage_id int8,
    staff_uuid VARCHAR(225),
    collec_type VARCHAR(100),
    start_date int8,
    end_date int8,
    is_active BOOLEAN,
    createdby VARCHAR(225),
    createddate int8,
    lastmodifiedby VARCHAR(225),
    lastmodifieddate int8
);

-- Add foreign key constraints
ALTER TABLE grbg_collection
ADD CONSTRAINT grbg_collection_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES hpudd_grbg_account (garbage_id),
ADD CONSTRAINT grbg_collection_staff_uuid_fk FOREIGN KEY (staff_uuid) REFERENCES grbg_collection_staff (uuid);





-- Create table grbg_address
CREATE TABLE grbg_address (
    uuid VARCHAR(225) PRIMARY KEY,
    address_type VARCHAR(100),
    address1 VARCHAR(255),
    address2 VARCHAR(255),
    city VARCHAR(225),
    state VARCHAR(225),
    pincode VARCHAR(100),
    is_active BOOLEAN
);





-- Create table grbg_scheduled_requests
CREATE TABLE grbg_scheduled_requests (
    uuid VARCHAR(225) PRIMARY KEY,
    garbage_id int8,
    type VARCHAR(225),
    start_date int8,
    end_date int8,
    is_active BOOLEAN
);

-- Add foreign key constraint
ALTER TABLE grbg_scheduled_requests
ADD CONSTRAINT grbg_scheduled_requests_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES hpudd_grbg_account (garbage_id);





-- Create table grbg_old_details
CREATE TABLE grbg_old_details (
    uuid VARCHAR(225) PRIMARY KEY,
    garbage_id int8,
    old_garbage_id VARCHAR(225)
);

-- Add foreign key constraint
ALTER TABLE grbg_old_details
ADD CONSTRAINT grbg_old_details_garbage_id_fk FOREIGN KEY (garbage_id) REFERENCES hpudd_grbg_account (garbage_id);




-- Create table grbg_declaration
CREATE TABLE grbg_declaration (
    uuid VARCHAR(225) PRIMARY KEY,
    statement TEXT,
    is_active BOOLEAN
);

