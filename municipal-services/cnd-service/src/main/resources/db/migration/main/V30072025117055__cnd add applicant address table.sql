CREATE TABLE IF NOT EXISTS public.ug_cnd_applicant_details (
    application_id       VARCHAR(64) PRIMARY KEY,
    name_of_applicant    VARCHAR(100) NOT NULL,
    mobile_number        VARCHAR(12) NOT NULL,
    email_id             VARCHAR(200),
    alternate_mobile_number  VARCHAR(100),
    createdby            VARCHAR(64),
    lastmodifiedby       VARCHAR(64),
    createdtime          BIGINT NOT NULL,
    lastmodifiedtime     BIGINT
);

CREATE TABLE IF NOT EXISTS public.ug_cnd_address_details (
    application_id       VARCHAR(64) PRIMARY KEY,
    house_number         VARCHAR(100),
    address_line_1       VARCHAR(100) NOT NULL,
    address_line_2       VARCHAR(100),
    landmark             VARCHAR(100),
    floor_number         VARCHAR(50),
    city                 VARCHAR(100) NOT NULL,
    locality             VARCHAR(100) NOT NULL,
    pinCode              VARCHAR(12) NOT NULL,
    address_type         VARCHAR(50)
);
