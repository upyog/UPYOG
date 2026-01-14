-- -- Add columns to egf_budgetdetail table
-- ALTER TABLE egf_budgetdetail
-- ADD COLUMN minorcode character varying(20) COLLATE pg_catalog."default",
-- ADD COLUMN majorcode character varying(20) COLLATE pg_catalog."default",
-- ADD COLUMN lastyearapproved numeric(20,2),
-- ADD COLUMN currentapproved numeric(20,2),
-- ADD COLUMN percentagechange real;

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



-- Add columns to egf_budgetdetail table if they do not exist
DO $$
BEGIN
    -- Check and add minorcode column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'egf_budgetdetail' AND column_name = 'minorcode') THEN
        ALTER TABLE egf_budgetdetail ADD COLUMN minorcode character varying(20) COLLATE pg_catalog."default";
    END IF;

    -- Check and add majorcode column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'egf_budgetdetail' AND column_name = 'majorcode') THEN
        ALTER TABLE egf_budgetdetail ADD COLUMN majorcode character varying(20) COLLATE pg_catalog."default";
    END IF;

    -- Check and add lastyearapproved column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'egf_budgetdetail' AND column_name = 'lastyearapproved') THEN
        ALTER TABLE egf_budgetdetail ADD COLUMN lastyearapproved numeric(20,2);
    END IF;

    -- Check and add currentapproved column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'egf_budgetdetail' AND column_name = 'currentapproved') THEN
        ALTER TABLE egf_budgetdetail ADD COLUMN currentapproved numeric(20,2);
    END IF;

    -- Check and add percentagechange column
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'egf_budgetdetail' AND column_name = 'percentagechange') THEN
        ALTER TABLE egf_budgetdetail ADD COLUMN percentagechange real;
    END IF;
END $$;
