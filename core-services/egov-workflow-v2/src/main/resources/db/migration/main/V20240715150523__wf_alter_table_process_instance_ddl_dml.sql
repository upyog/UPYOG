ALTER TABLE eg_wf_processinstance_v2 ADD COLUMN IF NOT EXISTS latest BOOLEAN DEFAULT TRUE;


WITH ranked_data AS (
    SELECT 
        id, 
        businessid, 
        DENSE_RANK() OVER (PARTITION BY businessid ORDER BY lastmodifiedtime DESC) as rank
    FROM 
        eg_wf_processinstance_v2
)
UPDATE eg_wf_processinstance_v2
SET latest = CASE 
                   WHEN ranked_data.rank = 1 THEN TRUE 
                   ELSE FALSE 
                END
FROM ranked_data
WHERE eg_wf_processinstance_v2.id = ranked_data.id;