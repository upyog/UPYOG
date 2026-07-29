CREATE TABLE IF NOT EXISTS ug_grbg_monthly_rent_payment (
    id VARCHAR(64) PRIMARY KEY,
    application_id INT8,
    application_no VARCHAR(64) NOT NULL,
    rent NUMERIC,
    penalty_amount NUMERIC,
    previous_month DATE,
    payment_date DATE,
    last_date_of_payment DATE,
    due_payment_date DATE,
    payment_status VARCHAR(20),
    due_payment NUMERIC,
    validity_days INT,
    createdby VARCHAR(64),
    lastmodifiedby VARCHAR(64),
    createdtime BIGINT,
    lastmodifiedtime BIGINT,
    CONSTRAINT fk_payment_application FOREIGN KEY (application_id)
    REFERENCES eg_grbg_account (id)
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );
