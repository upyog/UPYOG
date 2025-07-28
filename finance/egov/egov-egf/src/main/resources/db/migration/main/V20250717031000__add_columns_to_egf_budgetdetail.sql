-- Add columns to egf_budgetdetail table
ALTER TABLE egf_budgetdetail
ADD COLUMN minorcode character varying(20) COLLATE pg_catalog."default",
ADD COLUMN majorcode character varying(20) COLLATE pg_catalog."default",
ADD COLUMN lastyearapproved numeric(20,2),
ADD COLUMN currentapproved numeric(20,2),
ADD COLUMN percentagechange real;

-- Update eg_appconfig_values to include 'chartofaccounts' in value
UPDATE eg_appconfig_values
SET value = CONCAT(value, ',chartofaccounts')
WHERE key_id = (
    SELECT id FROM eg_appconfig 
    WHERE key_name = 'budgetDetail.grid.component'
      AND module = (
        SELECT id FROM eg_module WHERE name = 'EGF'
      )
)
AND value NOT LIKE '%chartofaccounts%';
