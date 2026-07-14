/**
 * Asset reference lookup for Existing Asset registration.
 * Matched by applicationNo entered in the search field.
 */
const assetReference = {
  Assets: [
    {
      accumulatedDepreciartion: null,
      assetAssignable: true,
      id: "0653f73c-a961-4f6d-a746-7ef0f20dc552",
      tenantId: "pg.citya",
      assetBookRefNo: "12345",
      assetName: "twin tower",
      description: "twin tower",
      assetClassification: "IMMOVABLE",
      assetParentCategory: "BUILDING",
      assetCategory: "ADMINISTRATION",
      assetSubCategory: "NA",
      department: "DEPT_2",
      applicationNo: "PG-1013-2026-B-001510",
      approvalNo: null,
      approvalDate: null,
      applicationDate: null,
      status: "INITIATED",
      addressDetails: {
        tenantId: null,
        doorNo: null,
        latitude: null,
        longitude: null,
        addressId: "e532350c-a774-49d4-a536-8d45feb187bf",
        addressNumber: null,
        type: null,
        addressLine1: null,
        addressLine2: null,
        landmark: null,
        city: "New Delhi",
        pincode: null,
        detail: null,
        buildingName: null,
        street: null,
        locality: {
          code: "JLC414",
          name: null,
          label: null,
          latitude: null,
          longitude: null,
          children: null,
          materializedPath: null,
        },
      },
      documents: [],
      auditDetails: {
        createdBy: "cd296c40-860f-4335-b582-7cf7fa94df37",
        lastModifiedBy: "cd296c40-860f-4335-b582-7cf7fa94df37",
        createdTime: 1784023168488,
        lastModifiedTime: 1784023168488,
      },
      assetAssignment: null,
      additionalDetails: {
        assetParentCategory: "BUILDING",
        modeOfPossessionOrAcquisition: {
          i18nKey: "AST_LEASED",
          code: "AST_LEASED",
          name: "modeOfPossessionOrAcquisition",
        },
        warranty: {
          i18nKey: "2_YEAR",
          code: "2_YEAR",
          name: "warranty",
        },
        buildingSno: "802",
        floorNo: "15",
        dimensions: "15x100x9",
        plotArea: "1500",
        plinthArea: "plinth area",
        howAssetBeingUsed: "asset land use",
      },
      accountId: "cd296c40-860f-4335-b582-7cf7fa94df37",
      remarks: null,
      financialYear: "2024-25",
      sourceOfFinance: "MUNICIPAL_FUNDS",
      scheme: null,
      subScheme: null,
      purchaseCost: 700000.0,
      acquisitionCost: 60000.0,
      bookValue: 760000.0,
      invoiceDate: 1783987200,
      invoiceNumber: "invoice number-1222211111",
      purchaseDate: 1782864000,
      purchaseOrderNumber: "12345555",
      location: "28.51464067450095, 76.97390795876001",
      oldCode: null,
      modeOfPossessionOrAcquisition: "AST_LEASED",
      unitOfMeasurement: null,
      lifeOfAsset: "802",
      assetUsage: "IN_USE",
      assetType: null,
      assetStatus: "1",
      originalBookValue: 760000.0,
      minimumValue: "0",
      islegacyData: "false",
    },
    {
      id: "1653f73c-a961-4f6d-a746-7ef0f20dc553",
      tenantId: "pg.citya",
      assetBookRefNo: "12346",
      assetName: "civic center",
      description: "civic center",
      assetClassification: "IMMOVABLE",
      assetParentCategory: "BUILDING",
      department: "DEPT_2",
      applicationNo: "PG-1013-2026-B-001511",
      status: "INITIATED",
      addressDetails: {
        city: "New Delhi",
        buildingName: null,
        locality: { code: "JLC414", name: null },
      },
      additionalDetails: {
        assetParentCategory: "BUILDING",
        buildingSno: "101",
        floorNo: "3",
        dimensions: "20x40x8",
        plotArea: "800",
      },
      assetType: null,
      assetStatus: "1",
    },
    {
      id: "2653f73c-a961-4f6d-a746-7ef0f20dc554",
      tenantId: "pg.citya",
      assetBookRefNo: "12347",
      assetName: "market complex",
      description: "market complex",
      assetClassification: "IMMOVABLE",
      assetParentCategory: "BUILDING",
      department: "DEPT_2",
      applicationNo: "PG-1013-2025-B-000902",
      status: "INITIATED",
      addressDetails: {
        city: "New Delhi",
        buildingName: null,
        locality: { code: "JLC415", name: null },
      },
      additionalDetails: {
        assetParentCategory: "BUILDING",
        buildingSno: "55",
        floorNo: "2",
        dimensions: "30x50x10",
        plotArea: "1500",
      },
      assetType: null,
      assetStatus: "1",
    },
  ],
};

export const findAssetByApplicationNo = (applicationNo = "") => {
  const query = String(applicationNo || "").trim().toLowerCase();
  if (!query) return null;
  return (
    assetReference.Assets.find(
      (asset) => String(asset.applicationNo || "").trim().toLowerCase() === query
    ) || null
  );
};

/** Prefix / contains match — used for typeahead while typing (e.g. "PG-1013"). */
export const findAssetsByApplicationPrefix = (applicationNo = "") => {
  const query = String(applicationNo || "").trim().toLowerCase();
  if (!query) return [];
  return assetReference.Assets.filter((asset) =>
    String(asset.applicationNo || "").trim().toLowerCase().startsWith(query)
  );
};

export default assetReference;
