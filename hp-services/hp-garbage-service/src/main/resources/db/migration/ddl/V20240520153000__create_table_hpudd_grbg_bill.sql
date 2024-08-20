CREATE TABLE hpudd_grbg_bill (
    id int8,
    bill_ref_no VARCHAR(255),
    garbage_id int8,
    bill_amount NUMERIC(10, 2),
    arrear_amount NUMERIC(10, 2),
    panelty_amount NUMERIC(10, 2),
    discount_amount NUMERIC(10, 2),
    total_bill_amount NUMERIC(10, 2),
    total_bill_amount_after_due_date NUMERIC(10, 2),
    bill_generated_by VARCHAR(255),
    bill_generated_date int8,
    bill_due_date int8,
    bill_period VARCHAR(50),
    bank_discount_amount NUMERIC(10, 2),
    payment_id VARCHAR(255),
    payment_status VARCHAR(50),
    bill_for VARCHAR(50),
    is_active BOOLEAN,
    created_by VARCHAR(255),
    created_date int8,
    last_modified_by VARCHAR(255),
    last_modified_date int8
);

ALTER TABLE hpudd_grbg_bill ADD CONSTRAINT pk_id_hpudd_grbg_bill PRIMARY KEY (id);
ALTER TABLE hpudd_grbg_bill ADD CONSTRAINT fk_garbage_id_hpudd_grbg_bill FOREIGN KEY (garbage_id) REFERENCES hpudd_grbg_account(garbage_id);

CREATE sequence seq_id_hpudd_grbg_bill start WITH 1 increment BY 1 no minvalue no maxvalue cache 1;


