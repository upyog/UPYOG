CREATE SEQUENCE SEQ_EG_ASSET_ID START WITH 1 INCREMENT BY 1 MAXVALUE 999999 CYCLE;

ALTER TABLE eg_asset_assetdetails
    ADD COLUMN invoiceDate BIGINT,
    ADD COLUMN location VARCHAR(256),
    ADD COLUMN subScheme VARCHAR(256),
    ADD COLUMN invoiceNumber VARCHAR(64),
    ADD COLUMN purchaseCost DOUBLE PRECISION,
    ADD COLUMN lifeOfAsset VARCHAR(64),
    ADD COLUMN unitOfMeasurement BIGINT,
    ADD COLUMN bookValue DOUBLE PRECISION,
    ADD COLUMN assetCurrentUsage VARCHAR(256),
    ADD COLUMN purchaseDate BIGINT,
    ADD COLUMN modeOfPossessionOrAcquisition VARCHAR(256),
    ADD COLUMN acquisitionCost DOUBLE PRECISION,
    ADD COLUMN purchaseOrderNumber VARCHAR(64),
    ADD COLUMN scheme VARCHAR(256),
    ADD COLUMN assetType VARCHAR(256),
    ADD COLUMN assetUsage VARCHAR(256),
    ADD COLUMN assetStatus VARCHAR(256),
    ADD COLUMN oldCode BIGINT;


ALTER TABLE eg_asset_auditdetails
    ADD COLUMN invoiceDate BIGINT,
    ADD COLUMN location VARCHAR(256),
    ADD COLUMN subScheme VARCHAR(256),
    ADD COLUMN invoiceNumber VARCHAR(64),
    ADD COLUMN purchaseCost DOUBLE PRECISION,
    ADD COLUMN lifeOfAsset VARCHAR(64),
    ADD COLUMN unitOfMeasurement BIGINT,
    ADD COLUMN bookValue DOUBLE PRECISION,
    ADD COLUMN assetCurrentUsage VARCHAR(256),
    ADD COLUMN purchaseDate BIGINT,
    ADD COLUMN modeOfPossessionOrAcquisition VARCHAR(256),
    ADD COLUMN acquisitionCost DOUBLE PRECISION,
    ADD COLUMN purchaseOrderNumber VARCHAR(64),
    ADD COLUMN scheme VARCHAR(256),
    ADD COLUMN assetType VARCHAR(256),
    ADD COLUMN assetUsage VARCHAR(256),
    ADD COLUMN assetStatus VARCHAR(256),
    ADD COLUMN oldCode BIGINT;
