/**
 * EST module data helpers — no React, no direct API calls.
 * Document preview: ./ESTDocumentPreview.js (re-exported below).
 */

import { buildDynamicAllotmentPayload } from "./allotmentPayloadUtils";
import { extractFileStoreId } from "./allotmentDocumentUtils";
import { normalizeAllotmentFlatData } from "./allotmentFormUtils";
import { getEstateRequestInfo } from "./assetPayloadUtils";
import estateFormConfig from "../config/estateFormConfig";
import estateAllotmentFormOverrides from "../config/Create/estateAllotmentFormOverrides";

const parseESTDate = (value) => {
  if (value === null || value === undefined || value === "") return null;
  if (typeof value === "number" || /^\d+$/.test(String(value))) {
    let num = Number(value);
    if (String(num).length === 10) num *= 1000;
    const d = new Date(num);
    return isNaN(d.getTime()) ? null : d;
  }
  const m = String(value).match(/^(\d{2})-(\d{2})-(\d{4})$/);
  if (m) return new Date(+m[3], +m[2] - 1, +m[1]);
  const d = new Date(value);
  return isNaN(d.getTime()) ? null : d;
};

/** Display format DD/MM/YYYY; "" if missing/invalid. */
export const formatDate = (value) => {
  const d = parseESTDate(value);
  if (!d) return "";
  const dd = String(d.getDate()).padStart(2, "0");
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  return `${dd}/${mm}/${d.getFullYear()}`;
};

const monthsToReadable = (totalMonths) => {
  const months = Number(totalMonths);
  if (!Number.isFinite(months) || months <= 0) return "";
  if (months <= 12) return `${months} ${months === 1 ? "month" : "months"}`;
  const y = Math.floor(months / 12);
  const m = months % 12;
  const parts = [];
  if (y) parts.push(`${y} ${y === 1 ? "year" : "years"}`);
  if (m) parts.push(`${m} ${m === 1 ? "month" : "months"}`);
  return parts.join(" ");
};

export const formatDurationWithMonths = (allotment = {}) =>
  monthsToReadable(allotment.duration);

export const pick = (...values) =>
  values.find((v) => v !== null && v !== undefined && v !== "");

export const filterEmpty = (rows = []) =>
  rows.filter((r) => r && r.value !== null && r.value !== undefined && r.value !== "");

const checkForNotNull = (value = "") =>
  value !== null && value !== undefined && value !== "";

export const checkForNA = (value = "") => (checkForNotNull(value) ? value : "EST_NA");

const extractAssetDataFromSession = (data = {}) =>
  data?.AssignAssetsData?.asset ||
  data?.AssignAssetsData?.Asset ||
  data?.AssignAssetsData?.assetData ||
  data?.assetData ||
  data?.Assetdata?.Assetdata ||
  data?.Assetdata ||
  {};

const toFormTextValue = (value) =>
  value === undefined || value === null || value === "" ? "" : String(value);

const toFlatAssetForPayload = (assetData = {}) => ({
  ...assetData,
  buildingFloor: toFormTextValue(assetData.buildingFloor ?? assetData.floor),
  buildingName: assetData.buildingName || assetData.assetName || "",
  serviceType:
    assetData.localityCode ||
    (typeof assetData.serviceType === "string"
      ? assetData.serviceType
      : assetData.serviceType?.code) ||
    "",
  serviceTypeName:
    assetData.locality ||
    assetData.localityName ||
    assetData.serviceTypeName ||
    "",
  assetType: assetData.assetType || assetData.assetParentCategory || "",
  department: assetData.department || "DEPT_2",
  refAssetNo: assetData.refAssetNo || assetData.refAsset || "",
});

/** Map a searched Asset API object onto NewRegistration form field names. */
export const mapAssetToRegistrationPrefill = (asset = {}) => {
  const flat = toFlatAssetForPayload(asset);
  return {
    buildingName: flat.buildingName || "",
    buildingNo: flat.buildingNo || "",
    buildingFloor: toFormTextValue(flat.buildingFloor ?? flat.floor),
    buildingBlock: flat.buildingBlock || "",
    totalFloorArea: flat.totalFloorArea ?? "",
    dimensionLength: flat.dimensionLength ?? "",
    dimensionWidth: flat.dimensionWidth ?? "",
    rate: flat.rate ?? "",
    assetRef: flat.refAssetNo || flat.assetRef || "",
    assetType: flat.assetType || "",
    serviceType: flat.serviceType || "",
    serviceTypeName: flat.serviceTypeName || "",
    city: flat.tenantId || flat.city || "",
  };
};

const buildAssetSearchCriteria = (tenantId, criteria = {}) => ({
  AssetSearchCriteria: {
    tenantId,
    ...criteria,
  },
});

/** Search estate assets by reference asset no, then estate no (exact match API). */
export const searchExistingEstateAssets = async (assetNumber, tenantId) => {
  const query = String(assetNumber || "").trim();
  if (!query || !tenantId) return [];

  const runSearch = async (criteria) => {
    const response = await Digit.ESTService.assetSearch({
      tenantId,
      filters: buildAssetSearchCriteria(tenantId, criteria),
    });
    return Array.isArray(response?.Assets) ? response.Assets : [];
  };

  const byRef = await runSearch({ refAssetNo: query });
  if (byRef.length) return byRef;

  return runSearch({ estateNo: query });
};

/** Shape API asset rows for DynamicForm existing-asset lookup UI. */
export const mapAssetSearchToRegistrationMatch = (asset = {}) => {
  const label = asset.refAssetNo || asset.estateNo || "";
  return {
    estateNo: label,
    label,
    subtitle: asset.buildingName || asset.assetName || "",
    prefill: mapAssetToRegistrationPrefill(asset),
  };
};

/** History state must be structured-cloneable — strip File/Date/circular junk. */
const toPlainJson = (value) => {
  try {
    return JSON.parse(
      JSON.stringify(value, (_key, v) => {
        if (typeof File !== "undefined" && v instanceof File) return undefined;
        if (typeof Blob !== "undefined" && v instanceof Blob) return undefined;
        if (v instanceof Date) return v.toISOString();
        return v;
      })
    );
  } catch (e) {
    console.warn("EST_ACK: failed to serialize ack payload", e);
    return {};
  }
};

export const buildAllotmentAcknowledgementData = (sessionData, apiResponse) => {
  const responseAllotment =
    apiResponse?.Allotments?.[0] ||
    apiResponse?.allotments?.[0] ||
    {};
  const sessionAllotment = sessionData?.Allotments?.Allotments?.[0] || {};
  const sessionAsset =
    extractAssetDataFromSession(sessionData) ||
    sessionData?.assetData ||
    {};

  // Prefer session form values for ack merge — do not rebuild a full create payload
  // (that can throw and turn a successful 201 into a failure acknowledgement).
  let payloadAllotment = {};
  try {
    const routeConfig = sessionData?.routeConfigs?.Allotments;
    if (routeConfig?.form?.length) {
      payloadAllotment =
        createAllotmentData(sessionData, routeConfig)?.Allotments?.[0] || {};
    }
  } catch (e) {
    console.error("EST_ACK: failed reading payload for merge", e);
  }

  const assets =
    sessionAsset && Object.keys(sessionAsset).length ? [sessionAsset] : [];

  const mergedAllotment = {
    ...sessionAllotment,
    ...payloadAllotment,
    ...responseAllotment,
    agreementStartDate:
      payloadAllotment.agreementStartDate ??
      sessionAllotment.agreementStartDate ??
      responseAllotment.agreementStartDate,
    agreementEndDate:
      payloadAllotment.agreementEndDate ??
      sessionAllotment.agreementEndDate ??
      responseAllotment.agreementEndDate,
    advancePaymentDate:
      payloadAllotment.advancePaymentDate ??
      sessionAllotment.advancePaymentDate ??
      responseAllotment.advancePaymentDate,
    citizenRequestLetter:
      payloadAllotment.citizenRequestLetter ??
      extractFileStoreId(sessionAllotment.citizenLetter) ??
      responseAllotment.citizenRequestLetter,
    allotmentLetter:
      payloadAllotment.allotmentLetter ??
      extractFileStoreId(sessionAllotment.allotmentLetter) ??
      responseAllotment.allotmentLetter,
    signedDeed:
      payloadAllotment.signedDeed ??
      extractFileStoreId(sessionAllotment.signedDeed) ??
      responseAllotment.signedDeed,
    eofficeFileNo:
      payloadAllotment.eofficeFileNo ??
      sessionAllotment.eOfficeFileNo ??
      sessionAllotment.eofficeFileNo ??
      responseAllotment.eofficeFileNo,
    allotmentId:
      responseAllotment.allotmentId ||
      sessionAllotment.allotmentId ||
      payloadAllotment.allotmentId ||
      "",
    assetNo:
      responseAllotment.assetNo ||
      sessionAllotment.assetNo ||
      payloadAllotment.assetNo ||
      sessionAsset.estateNo ||
      "",
  };

  return toPlainJson({
    Allotments: [mergedAllotment],
    Assets: assets,
    assetData: sessionAsset,
    // Required so PDF sections/labels match the check page (MDMS form + overrides).
    routeConfigs: sessionData?.routeConfigs || {},
    ResponseInfo: apiResponse?.ResponseInfo || apiResponse?.responseInfo || null,
  });
};

export const createAllotmentData = (data, routeConfig) => {
  const user = Digit?.UserService?.getUser?.()?.info || {};
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const activeRouteConfig = routeConfig || data?.routeConfigs?.Allotments;

  if (!activeRouteConfig?.form?.length) {
    console.warn(
      "createAllotmentData: routeConfig missing — complete the assign-assets form step before submit"
    );
  }

  const allotmentData =
    data?.Allotments?.Allotments?.[0] ||
    data?.AssignAssetsData?.AllotmentData ||
    {};

  const flatAllotment = normalizeAllotmentFlatData(
    {
      ...allotmentData,
      assetNo: allotmentData?.assetNo || data?.assetData?.estateNo || "",
      emailId: allotmentData?.emailId || allotmentData?.email || "",
      allotmentId: allotmentData?.allotmentId || data?.allotmentId || "",
      userUuid: allotmentData?.userUuid || "",
    },
    data?.assetData || {},
    activeRouteConfig || {}
  );

  const built = buildDynamicAllotmentPayload(
    activeRouteConfig || { form: [] },
    flatAllotment,
    tenantId
  );

  const isEdit = Boolean(allotmentData?.allotmentId);
  const existingAudit = allotmentData?.auditDetails;
  const apiId =
    activeRouteConfig?.apiId ||
    estateAllotmentFormOverrides.apiId ||
    estateFormConfig.apiId ||
    "Rainmaker";

  return {
    RequestInfo: getEstateRequestInfo({
      apiId,
      msgId: `${Date.now()}|en_IN`,
      action: isEdit ? "update" : "create",
      plainAccessRequest: {},
    }),
    Allotments: [
      {
        ...built,
        allotmentId: allotmentData?.allotmentId || "",
        userUuid: allotmentData?.userUuid || user?.uuid || "",
        billingCycle: built.billingCycle || "MONTHLY",
        tenantId: built.tenantId || tenantId,
        auditDetails: isEdit && existingAudit?.createdTime
          ? {
              createdBy: existingAudit.createdBy || user?.uuid || "",
              lastModifiedBy: user?.uuid || "",
              createdTime: existingAudit.createdTime,
              lastModifiedTime: Date.now(),
            }
          : {
              createdBy: user?.uuid || "",
              lastModifiedBy: user?.uuid || "",
              createdTime: Date.now(),
              lastModifiedTime: Date.now(),
            },
      },
    ],
  };
};

export { ESTDocumnetPreview } from "./ESTDocumentPreview";
