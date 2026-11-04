/**
 * allotmentFormUtils.js
 * Allotment form helpers for assign-assets (status checks, rent recompute, prefill).
 */
import {
  calculateRentByBillingCycle,
  findFieldConfig,
  rehydrateBillingCycleOption,
} from "@nudmcdgnpm/digit-ui-react-components";

// import { useTranslation } from "react-i18next";
// const { t } = useTranslation();

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
 
/** Allot Asset is allowed only when backend status is PENDING_FOR_ALLOTMENT. */
export const isPendingForAllotment = (asset = {}) =>
  String(asset?.assetAllotmentStatus || "").toUpperCase() ===  "PENDING_FOR_ALLOTMENT";

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

  // Always mirror rent into advance (including "0" when billing cycle is unset).
  merged.monthlyRent = monthlyRent;
  merged.advancePayment = monthlyRent;

  return merged;
};
