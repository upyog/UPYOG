CREATE TABLE egov_feedback_submission (
    id SERIAL PRIMARY KEY,
    serviceId VARCHAR(100) NOT NULL,
    applicationNo VARCHAR(255) NOT NULL,
    email VARCHAR(150),
    mobileNo VARCHAR(150) NOT NULL,
    userUuid VARCHAR(150),
    rating VARCHAR(150),
    source VARCHAR(150),
    feedbackType VARCHAR(150),
    status VARCHAR(150),
    action VARCHAR(150),
    additionalDetail JSONB,
    createdBy VARCHAR(150),
    lastModifiedBy VARCHAR(150),
    createdTime BIGINT,
    lastModifiedTime BIGINT
);

CREATE SEQUENCE seq_eg_feedback_submission_no 
    START WITH 1 
    INCREMENT BY 1 
    NO MINVALUE 
    NO MAXVALUE 
    CACHE 1;