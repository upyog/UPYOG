/**
 * Maps allotment API records to DynamicForm field names.
 */
import {
  calculateRentByBillingCycle,
  findFieldConfig,
  rehydrateBillingCycleOption,
} from "@nudmcdgnpm/digit-ui-react-components";
export const mapAllotmentApiToFormData = (allotment = {}) => {
  const toFormDate = (value) => {
    if (!value) return "";
    const m = String(value).match(/^(\d{2})-(\d{2})-(\d{4})$/);
    if (m) return `${m[3]}-${m[2]}-${m[1]}`;
    return value;
  };

  const normalizeOptionCode = (value) => {
    const code = String(value || "").trim().toUpperCase();
    return code || "";
  };

  const toFileRef = (value) => {
    if (!value) return null;
    if (typeof value === "object") return value;
    return { filestoreId: value, documentuuid: value };
  };

  return {
    allotmentId: allotment.allotmentId || "",
    userUuid: allotment.userUuid || "",
    assetNo: allotment.assetNo || "",
    alloteeName: allotment.alloteeName || "",
    mobileNo: allotment.mobileNo || allotment.phoneNumber || "",
    alternateMobileNo:
      allotment.alternateMobileNo || allotment.altPhoneNumber || "",
    emailId: allotment.emailId || allotment.email || "",
    agreementStartDate: toFormDate(allotment.agreementStartDate),
    agreementEndDate: toFormDate(allotment.agreementEndDate),
    advancePaymentDate: toFormDate(allotment.advancePaymentDate),
    duration: allotment.duration != null ? String(allotment.duration) : "",
    billingCycle: allotment.billingCycle || "",
    allotmentType: normalizeOptionCode(
      allotment.allotmentType || allotment.propertyType
    ),
    propertyType: normalizeOptionCode(
      allotment.propertyType || allotment.allotmentType
    ),
    rentRate: allotment.rentRate != null ? String(allotment.rentRate) : "",
    monthlyRent: allotment.monthlyRent != null ? String(allotment.monthlyRent) : "",
    advancePayment:
      allotment.advancePayment != null ? String(allotment.advancePayment) : "",
    eOfficeFileNo: allotment.eofficeFileNo || allotment.eOfficeFileNo || "",
    citizenLetter: toFileRef(allotment.citizenRequestLetter || allotment.citizenLetter),
    allotmentLetter: toFileRef(allotment.allotmentLetter),
    signedDeed: toFileRef(allotment.signedDeed),
    auditDetails: allotment.auditDetails || null,
  };
};

export const isAllotmentEdit = (data = {}) => {
  const allotment =
    data?.Allotments?.Allotments?.[0] ||
    data?.AssignAssetsData?.AllotmentData ||
    {};
  return Boolean(allotment?.allotmentId || data?.allotmentId);
};

export const getAssetIdentity = (asset = {}) =>
  asset?.estateNo || asset?.assetId || asset?.refAssetNo || "";

/** True when a form value should override API prefill (non-empty / file uploaded). */
export const hasMeaningfulFormValue = (value) => {
  if (value === null || value === undefined || value === "") return false;
  if (typeof value === "object" && !Array.isArray(value)) {
    return Boolean(
      value.filestoreId || value.fileStoreId || value.documentuuid || value.code
    );
  }
  return true;
};

/** Keep only user-entered values from a partial session draft. */
export const pickMeaningfulFormValues = (values = {}) =>
  Object.fromEntries(
    Object.entries(values).filter(([, value]) => hasMeaningfulFormValue(value))
  );

/**
 * Merge API allotment + session draft + read-only asset display fields.
 * Session wins only for fields the user actually filled; rest stay from DB.
 */
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

  // Invoice "Rate Per Sq Ft" defaults from read-only asset EST_RATE (Per sq.ft).
  if (!hasMeaningfulFormValue(merged.rentRate) && hasMeaningfulFormValue(merged.assetRate)) {
    merged.rentRate = merged.assetRate;
  }

  return merged;
};

export const hasAllotmentSessionDraft = (sessionData = {}, asset = {}) => {
  const draft = sessionData?.Allotments?.Allotments?.[0] || {};
  if (getAssetIdentity(sessionData?.assetData) !== getAssetIdentity(asset)) return false;
  return Object.keys(pickMeaningfulFormValues(draft)).length > 0;
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

export const fetchAllotmentByAssetNo = async (assetNo, tenantId) => {
  if (!assetNo || !tenantId) return null;
  const response = await Digit.ESTService.allotmentSearch({
    tenantId,
    filters: { tenantId, assetNo },
  });
  return response?.Allotments?.[0] || null;
};

/** Recompute rent from rate × area × billing cycle before API submit (never string concat). */
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
  }

  return merged;
};
