CREATE TABLE eg_asset_maintenance (
    maintenance_id VARCHAR(64) PRIMARY KEY, -- Primary key with a maximum of 64 characters
    asset_id VARCHAR(64) NOT NULL, -- Foreign key referencing asset details with a maximum of 64 characters
    current_life_of_asset VARCHAR(255), -- Current life of the asset
    is_warranty_expired BOOLEAN, -- Whether the warranty is expired
    is_amc_expired BOOLEAN, -- Whether the AMC is expired
    warranty_status VARCHAR(64), -- IN_WARRANTY, IN_AMC, NA
    amc_details TEXT, -- Details about AMC
    maintenance_type VARCHAR(64), -- Preventive or Corrective
    payment_type VARCHAR(64), -- Warranty, AMC, To be Paid
    cost_of_maintenance NUMERIC(18, 2), -- Cost of maintenance
    vendor VARCHAR(255), -- Vendor responsible for maintenance
    maintenance_cycle VARCHAR(50), -- Maintenance cycle (e.g., Monthly, Quarterly)
    parts_added_or_replaced TEXT, -- Parts added or replaced
    -- Additional details as a JSON column
    additional_details JSONB, -- Additional dynamic details in JSON format
    -- Audit details fields
    created_by VARCHAR(64), -- User who created the record
    created_time BIGINT, -- Time when the record was created
    last_modified_by VARCHAR(64), -- User who last modified the record
    last_modified_time BIGINT, -- Time when the record was last modified

    post_condition_remarks TEXT, -- Remarks after maintenance
    pre_condition_remarks TEXT, -- Remarks before maintenance
    description TEXT, -- Detailed description of maintenance

    -- Foreign key constraint
    CONSTRAINT fk_asset FOREIGN KEY (asset_id)
        REFERENCES public.eg_asset_assetdetails (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
);
