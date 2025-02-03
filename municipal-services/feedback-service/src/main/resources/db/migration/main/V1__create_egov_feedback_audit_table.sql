CREATE TABLE egov_feedback_submission_audit (
    id SERIAL PRIMARY KEY,
    accountid VARCHAR(100),
    feedbackType VARCHAR(150),
    feedbackNumber VARCHAR(150),
    feedbackId BIGINT,
    comment TEXT NULL,
    action VARCHAR(150) NULL,
    status VARCHAR(150) NULL,
    createdBy VARCHAR(150),
    createdTime BIGINT,
    CONSTRAINT fk_feedback_submission
        FOREIGN KEY (feedbackId) REFERENCES egov_feedback_submission (id) 
        ON DELETE CASCADE
);

CREATE SEQUENCE seq_eg_feedback_audit_id 
    START WITH 1 
    INCREMENT BY 1 
    NO MINVALUE 
    NO MAXVALUE 
    CACHE 1;
