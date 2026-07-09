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

  return {
    allotmentId: allotment.allotmentId || "",
    assetNo: allotment.assetNo || "",
    alloteeName: allotment.alloteeName || "",
    phoneNumber: allotment.mobileNo || allotment.phoneNumber || "",
    altPhoneNumber: allotment.alternateMobileNo || allotment.altPhoneNumber || "",
    emailId: allotment.emailId || allotment.email || "",
    agreementStartDate: toFormDate(allotment.agreementStartDate),
    agreementEndDate: toFormDate(allotment.agreementEndDate),
    advancePaymentDate: toFormDate(allotment.advancePaymentDate),
    duration: allotment.duration != null ? String(allotment.duration) : "",
    billingCycle: allotment.billingCycle || "",
    allotmentType: allotment.allotmentType || "",
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

export const fetchAllotmentByAssetNo = async (assetNo, tenantId) => {
  if (!assetNo || !tenantId) return null;
  const response = await Digit.ESTService.allotmentSearch({
    tenantId,
    filters: { tenantId, assetNo },
  });
  return response?.Allotments?.[0] || null;
};
