/**
 * EST module data helpers — no React, no direct API calls.
 * Document preview: ./ESTDocumentPreview.js (re-exported below).
 */

import { mergeRouteConfig } from "@nudmcdgnpm/digit-ui-react-components";
import { buildDynamicAllotmentPayload } from "./allotmentPayloadUtils";
import { extractFileStoreId } from "./allotmentDocumentUtils";
import { normalizeAllotmentFlatData } from "./allotmentFormUtils";
import { buildDynamicAssetPayload } from "./assetPayloadUtils";
import estateFormConfig from "../config/estateFormConfig";

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

const getAssetRouteConfig = (data = {}) => {
  const fromSession = data?.routeConfigs?.newRegistration;
  if (fromSession?.form?.length) {
    return mergeRouteConfig(fromSession, estateFormConfig);
  }
  return estateFormConfig;
};

const estPayloadData = (data) => {
  const user = Digit.UserService.getUser().info;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const assetData = extractAssetDataFromSession(data);
  const flatAsset = toFlatAssetForPayload(assetData);
  const routeConfig = getAssetRouteConfig(data);
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

export { ESTDocumnetPreview } from "./ESTDocumentPreview";
