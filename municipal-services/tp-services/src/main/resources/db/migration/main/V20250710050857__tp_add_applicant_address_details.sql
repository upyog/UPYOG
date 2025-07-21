CREATE TABLE IF NOT EXISTS public.upyog_rs_tree_pruning_applicant_details (
    applicant_id         VARCHAR(64) PRIMARY KEY,
    booking_id           VARCHAR(64),
    name                 VARCHAR(100) NOT NULL,
    mobile_number        VARCHAR(12) NOT NULL,
    email_id             VARCHAR(200),
    alternate_number     VARCHAR(100),
    createdby            VARCHAR(64),
    lastmodifiedby       VARCHAR(64),
    createdtime          BIGINT NOT NULL,
    lastmodifiedtime     BIGINT
);

CREATE TABLE IF NOT EXISTS public.upyog_rs_tree_pruning_address_details (
    address_id           VARCHAR(64),
    applicant_id         VARCHAR(64),
    house_no             VARCHAR(100) NOT NULL,
    address_line_1       VARCHAR(100) NOT NULL,
    address_line_2       VARCHAR(100),
    street_name          VARCHAR(100),
    landmark             VARCHAR(100),
    city                 VARCHAR(100) NOT NULL,
    city_code            VARCHAR(10),
    locality             VARCHAR(100) NOT NULL,
    locality_code        VARCHAR(30),
    pincode              VARCHAR(12) NOT NULL,
    CONSTRAINT fk_applicant_id
        FOREIGN KEY (applicant_id)
        REFERENCES public.upyog_rs_tree_pruning_applicant_details(applicant_id)
        ON DELETE CASCADE
);
