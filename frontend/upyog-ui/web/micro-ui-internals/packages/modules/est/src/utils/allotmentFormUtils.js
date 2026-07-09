/**
 * Maps allotment API records to DynamicForm field names.
 */
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

  return {
    allotmentId: allotment.allotmentId || "",
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
    citizenLetter: allotment.citizenLetter || null,
    allotmentLetter: allotment.allotmentLetter || null,
    signedDeed: allotment.signedDeed || null,
  };
};

export const getAssetIdentity = (asset = {}) =>
  asset?.estateNo || asset?.assetId || asset?.refAssetNo || "";

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
