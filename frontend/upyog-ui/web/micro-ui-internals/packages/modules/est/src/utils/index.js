/**
 * utils/index.js (cleaned)
 * ------------------------
 * Pure data helpers for the EST module. No React, no API calls —
 * API calls live in services/ESTAllotmentService.js, and the
 * document-preview component now lives in ./ESTDocumentPreview.js
 * (re-exported at the bottom so existing imports keep working).
 */

import { buildDynamicAllotmentPayload } from "./allotmentPayloadUtils";
import { extractFileStoreId } from "./allotmentDocumentUtils";

import { buildDynamicAssetPayload } from "./assetPayloadUtils";
import estateFormConfig from "../config/estateFormConfig";
import { Config as registrationConfig } from "../config/Create/config";

/* ════════════════════════ Date helpers ════════════════════════
   The EST backend mixes formats: "DD-MM-YYYY" strings for agreement
   dates, epoch ms for audit fields. parseESTDate is the single
   entry point — every other date function goes through it.       */

/** Parse epoch ms/seconds, "DD-MM-YYYY", or ISO string into a Date (or null). */
export const parseESTDate = (value) => {
  if (value === null || value === undefined || value === "") return null;
  if (typeof value === "number" || /^\d+$/.test(String(value))) {
    let num = Number(value);
    if (String(num).length === 10) num *= 1000; // epoch seconds → ms
    const d = new Date(num);
    return isNaN(d.getTime()) ? null : d;
  }
  const m = String(value).match(/^(\d{2})-(\d{2})-(\d{4})$/);
  if (m) return new Date(+m[3], +m[2] - 1, +m[1]); // DD-MM-YYYY
  const d = new Date(value); // ISO or anything else parseable
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

/**
 * Backend contract wants dates as "dd-MM-yyyy" strings.
 * Built from LOCAL date parts — toISOString() shifts IST-midnight
 * dates back a day (UTC), giving off-by-one dates.
 */
export const toDDMMYYYY = (value) => {
  const d = parseESTDate(value);
  if (!d) return null;
  const dd = String(d.getDate()).padStart(2, "0");
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  return `${dd}-${mm}-${d.getFullYear()}`;
};

/** Epoch (ms or s) → DD/MM/YYYY, or "N/A". */
export const formatEpochDate = (value) => formatDate(value) || "N/A";

/**
 * Legacy epoch formatter kept for existing callers.
 * "ewst" gets dashes, everything else slashes.
 */
export const convertEpochToDate = (dateEpoch, businessService) => {
  const formatted = formatDate(dateEpoch);
  if (!formatted) return null;
  return businessService === "ewst" ? formatted.replaceAll("/", "-") : formatted;
};

/* ════════════════════════ Duration helpers ════════════════════════ */

/** Whole months between two dates (0 if invalid/negative). Single source of the math. */
export const calculateDuration = (start, end) => {
  const startDate = parseESTDate(start);
  const endDate = parseESTDate(end);
  if (!startDate || !endDate) return 0;

  let years = endDate.getFullYear() - startDate.getFullYear();
  let months = endDate.getMonth() - startDate.getMonth();
  if (months < 0) {
    years--;
    months += 12;
  }
  const totalMonths = years * 12 + months;
  return totalMonths >= 0 ? totalMonths : 0;
};

/** Months count → "1 year 6 months" (or "" for 0/invalid). */
const monthsToReadable = (totalMonths) => {
  const months = Number(totalMonths);
  if (!Number.isFinite(months) || months <= 0) return "";
  const y = Math.floor(months / 12);
  const m = months % 12;
  const parts = [];
  if (y) parts.push(`${y} ${y === 1 ? "year" : "years"}`);
  if (m) parts.push(`${m} ${m === 1 ? "month" : "months"}`);
  return parts.join(" ");
};

/** Two dates → "1 year 6 months" ("0 months" if same/invalid). */
export const calculatemonths = (start, end) =>
  monthsToReadable(calculateDuration(start, end)) || "0 months";

/** Allotment (duration in months) → "1 year 7 months". */
export const formatDurationWithMonths = (allotment = {}) =>
  monthsToReadable(allotment.duration);

/** "15 months" / "15" / 15 → 15. */
export const extractDuration = (durationStr) => {
  if (!durationStr) return 0;
  const match = durationStr.toString().match(/\d+/);
  return match ? Number(match[0]) : 0;
};

/* ════════════════════════ Value helpers ════════════════════════ */

/** First non-empty value (numeric 0 counts as a value). */
export const pick = (...values) =>
  values.find((v) => v !== null && v !== undefined && v !== "");

/** Keep rows with a meaningful value — numeric 0 is kept (Advance Payment: 0 shows). */
export const filterEmpty = (rows = []) =>
  rows.filter((r) => r && r.value !== null && r.value !== undefined && r.value !== "");

export const checkForNotNull = (value = "") =>
  value !== null && value !== undefined && value !== "";

export const checkForNA = (value = "") => (checkForNotNull(value) ? value : "EST_NA");

export const stringReplaceAll = (str = "", searcher = "", replaceWith = "") => {
  if (searcher === "") return str;
  while (str.includes(searcher)) {
    str = str.replace(searcher, replaceWith);
  }
  return str;
};

/** Replace all dots with underscores; "NA" if empty. */
export const convertDotValues = (value = "") =>
  checkForNotNull(value) ? stringReplaceAll(String(value), ".", "_") : "NA";

export const getFixedFilename = (filename = "", size = 5) =>
  filename.length <= size ? filename : `${filename.substr(0, size)}...`;

export const checkIsAnArray = (obj = []) => Array.isArray(obj);

export const checkArrayLength = (obj = [], length = 0) =>
  checkIsAnArray(obj) && obj.length > length;

/* ════════════════════════ Form / UI helpers ════════════════════════ */

/** Sort form items by "order", including a group's children. */
export const sortByOrder = (formConfig = []) => {
  const sorted = [...formConfig].sort((a, b) => (a.order ?? 0) - (b.order ?? 0));
  return sorted.map((item) =>
    item.type === "group" && Array.isArray(item.children)
      ? { ...item, children: [...item.children].sort((a, b) => (a.order ?? 0) - (b.order ?? 0)) }
      : item
  );
};

export const shouldHideBackButton = (config = []) =>
  config.some((key) => window.location.href.includes(key.screenPath)) ||
  window.location.href.includes("acknowledgement");

export const estAccess = () => true;

export const getWorkflow = () => ({
  businessService: `est`,
  moduleName: "estate-services",
});

/** Shallow-ish equality check used by edit flows. Kept as-is behaviorally. */
export const CompareTwoObjects = (ob1, ob2) => {
  let comp = 0;
  Object.keys(ob1).forEach((key) => {
    if (typeof ob1[key] === "object" && ob1[key] !== null) {
      if (key === "institution") {
        if ((ob1[key].name || ob2[key]?.name) && ob1[key]?.name !== ob2[key]?.name) comp = 1;
        else if (ob1[key]?.type?.code !== ob2[key]?.type?.code) comp = 1;
      } else if (ob1[key]?.code !== ob2[key]?.code) comp = 1;
    } else if ((ob1[key] || ob2[key]) && ob1[key] !== ob2[key]) {
      comp = 1;
    }
  });
  return comp !== 1;
};

/* ════════════════════════ Filestore helpers ════════════════════════ */

export const pdfDownloadLink = (documents = {}, fileStoreId = "") => {
  const downloadLink = documents[fileStoreId] || "";
  const differentFormats = downloadLink?.split(",") || [];
  return (
    differentFormats.find(
      (link) => !link.includes("large") && !link.includes("medium") && !link.includes("small")
    ) || ""
  );
};

export const pdfDocumentName = (documentLink = "", index = 0) =>
  decodeURIComponent(documentLink.split("?")[0].split("/").pop().slice(13)) ||
  `Document - ${index + 1}`;

export const DownloadReceipt = async (
  consumerCode,
  tenantId,
  businessService,
  pdfKey = "consolidatedreceipt"
) => {
  tenantId = tenantId || Digit.ULBService.getCurrentTenantId();
  await Digit.Utils.downloadReceipt(consumerCode, businessService, pdfKey, tenantId);
};

/* ════════════════════════ Payload builders ════════════════════════
   These SHAPE data only. Sending it is ESTAllotmentService's job
   (called from useESTAssetsAllotment) — never call APIs from here. */

const extractAssetDataFromSession = (data = {}) =>
  data?.AssignAssetsData?.asset ||
  data?.AssignAssetsData?.Asset ||
  data?.AssignAssetsData?.assetData ||
  data?.assetData ||
  data?.Assetdata?.Assetdata ||
  data?.Assetdata ||
  {};

const toFlatAssetForPayload = (assetData = {}) => ({
  ...assetData,
  buildingFloor: assetData.buildingFloor ?? assetData.floor ?? "",
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

const getAssetRouteConfig = () => {
  const step =
    registrationConfig?.[0]?.body?.find((s) => s.key === "newRegistration") || {};
  return { ...step, ...estateFormConfig };
};

export const estPayloadData = (data) => {
  const user = Digit.UserService.getUser().info;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const assetData = extractAssetDataFromSession(data);
  const flatAsset = toFlatAssetForPayload(assetData);
  const routeConfig = getAssetRouteConfig();
  const assetPayload = buildDynamicAssetPayload(routeConfig, flatAsset, tenantId);

  return {
    Assets: [
      {
        ...assetPayload,
        assetId: assetData.assetId || crypto.randomUUID(),
        estateNo: assetData.estateNo || "",
        refAssetNo: flatAsset.refAssetNo || "",
        billingCycle: assetData.billingCycle || "MONTHLY",
        additionalDetails: assetData.additionalDetails || {},
        auditDetails: {
          createdBy: user?.uuid || "",
          lastModifiedBy: user?.uuid || "",
          createdTime: Date.now(),
          lastModifiedTime: Date.now(),
        },
      },
    ],
  };
};

/** Merge API response + session data for acknowledgement display/PDF. */
export const buildAllotmentAcknowledgementData = (sessionData, apiResponse) => {
  const responseAllotment = apiResponse?.Allotments?.[0] || {};
  const sessionAllotment = sessionData?.Allotments?.Allotments?.[0] || {};
  let payloadAllotment = {};
  try {
    payloadAllotment = createAllotmentData(sessionData)?.Allotments?.[0] || {};
  } catch (e) {
    console.error("EST_ACK: failed reading payload for merge", e);
  }

  let assets = [];
  try {
    assets = estPayloadData(sessionData)?.Assets || [];
  } catch (e) {
    console.error("EST_ACK: estPayloadData threw during merge", e);
  }

  return {
    Allotments: [
      {
        ...responseAllotment,
        agreementStartDate:
          payloadAllotment.agreementStartDate ?? responseAllotment.agreementStartDate,
        agreementEndDate:
          payloadAllotment.agreementEndDate ?? responseAllotment.agreementEndDate,
        advancePaymentDate:
          payloadAllotment.advancePaymentDate ?? responseAllotment.advancePaymentDate,
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
          payloadAllotment.eofficeFileNo ?? responseAllotment.eofficeFileNo,
      },
    ],
    Assets: assets,
  };
};

export const createAllotmentData = (data, routeConfig) => {
  const user = Digit.UserService.getUser().info;
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

  const flatAllotment = {
    ...allotmentData,
    assetNo: allotmentData?.assetNo || data?.assetData?.estateNo || "",
    emailId: allotmentData?.emailId || allotmentData?.email || "",
    allotmentId:
      allotmentData?.allotmentId || data?.allotmentId || "",
    userUuid: allotmentData?.userUuid || "",
  };

  const built = buildDynamicAllotmentPayload(
    activeRouteConfig || { form: [] },
    flatAllotment,
    tenantId
  );

  const isEdit = Boolean(allotmentData?.allotmentId);
  const existingAudit = allotmentData?.auditDetails;

  return {
    Allotments: [
      {
        ...built,
        allotmentId: allotmentData?.allotmentId || "",
        userUuid: allotmentData?.userUuid || user?.uuid || "",
        billingCycle: built.billingCycle || "MONTHLY",
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

/* ════════════════════════ Re-exports ════════════════════════
   ESTDocumnetPreview moved to its own file (it's a React component,
   not a util). Re-exported here so existing imports keep working. */

export { ESTDocumnetPreview } from "./ESTDocumentPreview";
