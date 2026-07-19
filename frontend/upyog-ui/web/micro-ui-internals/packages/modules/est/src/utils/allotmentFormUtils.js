/**
 * Allotment form helpers for assign-assets flow.
 */
import {
  calculateRentByBillingCycle,
  findFieldConfig,
  rehydrateBillingCycleOption,
} from "@nudmcdgnpm/digit-ui-react-components";

export const getAssetIdentity = (asset = {}) =>
  asset?.estateNo || asset?.assetId || asset?.refAssetNo || "";

const hasMeaningfulFormValue = (value) => {
  if (value === null || value === undefined || value === "") return false;
  if (typeof value === "object" && !Array.isArray(value)) {
    return Boolean(
      value.filestoreId || value.fileStoreId || value.documentuuid || value.code
    );
  }
  return true;
};

export const pickMeaningfulFormValues = (values = {}) =>
  Object.fromEntries(
    Object.entries(values).filter(([, value]) => hasMeaningfulFormValue(value))
  );

export const mergeAllotmentPrefill = (
  apiData = {},
  sessionDraft = {},
  assetDisplay = {}
) => {
  const merged = {
    ...assetDisplay,
    ...(apiData || {}),
    ...pickMeaningfulFormValues(sessionDraft),
  };

  merged.allotmentId = sessionDraft?.allotmentId || apiData?.allotmentId || "";
  merged.userUuid = sessionDraft?.userUuid || apiData?.userUuid || "";
  merged.auditDetails = sessionDraft?.auditDetails || apiData?.auditDetails || null;

  if (!hasMeaningfulFormValue(merged.rentRate) && hasMeaningfulFormValue(merged.assetRate)) {
    merged.rentRate = merged.assetRate;
  }

  return merged;
};

export const isAssetAllotted = (asset = {}) => {
  const allotmentStatus = String(asset?.assetAllotmentStatus || "").toUpperCase();
  if (
    allotmentStatus &&
    allotmentStatus !== "INITIATED" &&
    allotmentStatus !== "AVAILABLE"
  ) {
    return true;
  }
  return String(asset?.assetStatus || "").toLowerCase() === "allotted";
};

export const hasExistingAllotment = (asset = {}, allottedAssetNos = null) => {
  const estateNo = asset?.estateNo;
  if (estateNo && allottedAssetNos?.has?.(estateNo)) return true;
  return isAssetAllotted(asset);
};

export const fetchAllottedAssetNos = async (assets = [], tenantId) => {
  if (!tenantId || !Array.isArray(assets) || assets.length === 0) {
    return new Set();
  }

  try {
    const response = await Digit.ESTService.allotmentSearch({
      tenantId,
      filters: { tenantId },
    });
    const searchNos = new Set(assets.map((asset) => asset.estateNo).filter(Boolean));
    return new Set(
      (response?.Allotments || [])
        .map((allotment) => allotment.assetNo)
        .filter((assetNo) => searchNos.has(assetNo))
    );
  } catch (error) {
    console.error("Error fetching allotted asset numbers:", error);
    return new Set();
  }
};

export const normalizeAllotmentFlatData = (flatData = {}, assetData = {}, routeConfig = {}) => {
  const merged = {
    ...flatData,
    totalFloorArea: flatData.totalFloorArea || assetData.totalFloorArea || "",
    rentRate: flatData.rentRate ?? assetData.rate ?? flatData.rentRate,
  };

  const billingField = findFieldConfig(routeConfig?.form, "billingCycle");
  const billingOptions = billingField?.options || [];
  const billingCycle = rehydrateBillingCycleOption(merged, routeConfig);

  const monthlyRent = calculateRentByBillingCycle(
    merged.rentRate,
    merged.totalFloorArea,
    billingCycle,
    billingOptions
  );

  if (monthlyRent) {
    merged.monthlyRent = monthlyRent;
    // Advance payment mirrors calculated rent (field is disabled on the form).
    merged.advancePayment = monthlyRent;
  }

  return merged;
};
