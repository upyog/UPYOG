-- Adds column to the main table
ALTER TABLE ug_cnd_application_details
ADD COLUMN created_by_usertype VARCHAR(50);

-- Adds column to the audit table
ALTER TABLE ug_cnd_application_details_audit
ADD COLUMN created_by_usertype VARCHAR(50);
