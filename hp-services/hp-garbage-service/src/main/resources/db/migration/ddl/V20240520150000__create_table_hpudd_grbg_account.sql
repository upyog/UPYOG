CREATE TABLE hpudd_grbg_account (
    id int8,
    garbage_id int8 UNIQUE NOT NULL,
    property_id int8 NOT NULL,
    type VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(20) NOT NULL,
    parent_id int8,
    created_by VARCHAR(255),
    created_date int8,
    last_modified_by VARCHAR(255),
    last_modified_date int8
);

ALTER TABLE hpudd_grbg_account ADD CONSTRAINT pk_id_hpudd_grbg_account PRIMARY KEY (id);

CREATE sequence seq_id_hpudd_grbg_account start WITH 1 increment BY 1 no minvalue no maxvalue cache 1;
