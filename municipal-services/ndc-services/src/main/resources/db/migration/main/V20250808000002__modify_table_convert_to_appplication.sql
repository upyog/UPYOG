ALTER TABLE IF EXISTS eg_ndc_applicants RENAME TO eg_ndc_application;

ALTER TABLE IF EXISTS eg_ndc_application
    DROP COLUMN IF EXISTS firstname,
    DROP COLUMN IF EXISTS lastname,
    DROP COLUMN IF EXISTS mobile,
    DROP COLUMN IF EXISTS email,
    DROP COLUMN IF EXISTS address;

ALTER TABLE IF EXISTS eg_ndc_details RENAME COLUMN applicantid to applicationid;
ALTER TABLE IF EXISTS eg_ndc_documents RENAME COLUMN applicantid to applicationid;