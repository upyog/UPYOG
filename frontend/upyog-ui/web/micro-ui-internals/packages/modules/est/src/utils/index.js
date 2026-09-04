/**
 * EST module data helpers — no React, no direct API calls.
 * Document preview: ./ESTDocumentPreview.js (re-exported below).
 */

import { buildDynamicAllotmentPayload } from "./allotmentPayloadUtils";
import { extractFileStoreId } from "./allotmentDocumentUtils";
import { normalizeAllotmentFlatData } from "./allotmentFormUtils";
import { getEstateRequestInfo } from "./assetPayloadUtils";
import { parseAdditionalDetails } from "./estMdmsUtils";
import estateFormConfig from "../config/estateFormConfig";
import estateAllotmentFormOverrides from "../config/Create/estateAllotmentFormOverrides";
import getESTAllotmentAcknowledgementData from "./getESTAllotmentAcknowledgementData";

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

/** Numeric form fields (pattern ^[0-9]+$) — drop decimals / non-digits. */
const toNumericFormText = (value) => {
  if (value === undefined || value === null || value === "") return "";
  const n = Number(value);
  if (Number.isFinite(n)) return String(Math.trunc(n));
  return String(value).replace(/[^\d]/g, "");
};

/** Asset-module reference numbers look like PG-1013-2026-B-001512. */
const isAssetModuleRefNo = (value) =>
  /^PG-/i.test(String(value || "").trim());

/**
 * Resolve asset-services identity → estate Asset.refAssetNo (PG-…).
 * Prefer applicationNo (canonical asset-services id); never use estateNo (EST-…).
 */
const resolveRefAssetNo = (asset = {}) => {
  const candidates = [
    asset.applicationNo,
    asset.assetBookRefNo,
    asset.refAssetNo,
    asset.refAsset,
  ];
  const pgPrefixed = candidates.find(isAssetModuleRefNo);
  if (pgPrefixed) return String(pgPrefixed).trim();
  return String(pick(...candidates) || "").trim();
};

/**
 * Map asset-services/_search row → NewRegistration form field names.
 * Plot mapping: Total Plot Area ← dimensions, Length ← plinthArea,
 * Width ← additionalDetails.plotArea.
 *
 * estateNo  → EST-… (estate-management; empty on new create)
 * refAssetNo → PG-… (asset-services applicationNo / book ref)
 */
export const mapAssetToRegistrationPrefill = (asset = {}) => {
  const additional = parseAdditionalDetails(asset.additionalDetails);
  const address = asset.addressDetails || {};
  const locality = address.locality || {};
  const serviceTypeCode =
    typeof asset.serviceType === "string"
      ? asset.serviceType
      : asset.serviceType?.code;
  const refAssetNo = resolveRefAssetNo(asset);
  // Keep only EST-… here; never promote a PG-… asset ref into estateNo.
  const estateNoRaw = String(asset.estateNo || "").trim();
  const estateNo =
    estateNoRaw && !isAssetModuleRefNo(estateNoRaw) ? estateNoRaw : "";

  return {
    estateNo,
    buildingName:
      pick(asset.buildingName, address.buildingName, asset.assetName) || "",
    buildingNo:
      pick(asset.buildingNo, additional.buildingSno, additional.buildingNo) ||
      "",
    buildingFloor: toNumericFormText(
      pick(
        asset.buildingFloor,
        asset.floor,
        additional.floorNo,
        additional.floor
      )
    ),
    buildingBlock:
      pick(asset.buildingBlock, additional.buildingBlock, additional.block) ||
      "",
    totalFloorArea: toNumericFormText(
      pick(asset.dimensions, additional.dimensions)
    ),
    dimensionLength: toNumericFormText(
      pick(asset.plinthArea, additional.plinthArea)
    ),
    dimensionWidth: toNumericFormText(additional.plotArea),
    rate: toNumericFormText(
      pick(asset.rate, additional.rate, asset.purchaseCost)
    ),
    assetRef: refAssetNo,
    refAssetNo,
    assetType: pick(asset.assetType, asset.assetParentCategory) || "",
    serviceType:
      pick(asset.localityCode, serviceTypeCode, locality.code) || "",
    serviceTypeName:
      pick(
        asset.locality,
        asset.localityName,
        asset.serviceTypeName,
        locality.name
      ) || "",
    city: pick(asset.tenantId, asset.city) || "",
  };
};

/**
 * Search immovable assets for Existing Asset registration.
 * Uses asset-services: POST /asset-services/v1/assets/_search
 * (query params via Digit.ASSETService.search).
 */
export const searchExistingEstateAssets = async (assetNumber, tenantId) => {
  const query = String(assetNumber || "").trim();
  if (!query || !tenantId) return [];

  const runSearch = async (filters = {}) => {
    const response = await Digit.ASSETService.search({
      tenantId,
      filters: {
        assetClassification: "IMMOVABLE",
        ...filters,
      },
    });
    return Array.isArray(response?.Assets) ? response.Assets : [];
  };

  const byApplicationNo = await runSearch({ applicationNo: query });
  if (byApplicationNo.length) return byApplicationNo;

  const queryLower = query.toLowerCase();
  const byBookRef = await runSearch({ assetBookRefNo: query });
  return byBookRef.filter(
    (asset) =>
      String(asset?.assetBookRefNo || "").toLowerCase() === queryLower
  );
};

/** Shape API asset rows for DynamicForm existing-asset lookup UI. */
export const mapAssetSearchToRegistrationMatch = (asset = {}) => {
  // Search card shows the asset-module number the user looked up (not estateNo).
  const label =
    pick(
      asset.applicationNo,
      asset.assetBookRefNo,
      asset.refAssetNo,
      asset.estateNo
    ) || "";
  return {
    estateNo: label,
    label,
    subtitle: pick(asset.buildingName, asset.assetName) || "",
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
      sessionAllotment.eofficeFileNo ??
      responseAllotment.eofficeFileNo,
    additionalDetails: (() => {
      const merged = {
        ...parseAdditionalDetails(responseAllotment.additionalDetails),
        ...parseAdditionalDetails(sessionAllotment.additionalDetails),
        ...parseAdditionalDetails(payloadAllotment.additionalDetails),
      };
      const fileReferenceNumber =
        merged.fileReferenceNumber ||
        sessionAllotment.fileReferenceNumber ||
        merged.FILE_REFERENCE_NUMBER ||
        "";
      const { FILE_REFERENCE_NUMBER: _legacy, ...rest } = merged;
      return {
        ...rest,
        ...(fileReferenceNumber ? { fileReferenceNumber } : {}),
      };
    })(),
    fileReferenceNumber:
      parseAdditionalDetails(payloadAllotment.additionalDetails)
        .fileReferenceNumber ||
      sessionAllotment.fileReferenceNumber ||
      parseAdditionalDetails(responseAllotment.additionalDetails)
        .fileReferenceNumber ||
      parseAdditionalDetails(responseAllotment.additionalDetails)
        .FILE_REFERENCE_NUMBER ||
      "",
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
        billingCycle: built.billingCycle,
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

export const downloadESTReceipt = async (tenantId, payments) => {
  const paymentList = Array.isArray(payments) ? payments : [payments];
  let response;
  if (paymentList[0]?.fileStoreId) {
    response = { filestoreIds: [paymentList[0].fileStoreId] };
  } else {
    response = await Digit.PaymentService.generatePdf(tenantId, { Payments: paymentList }, "est-service-receipt");
  }
  const fileStore = await Digit.PaymentService.printReciept(tenantId, { fileStoreIds: response.filestoreIds[0] });
  window.open(fileStore[response.filestoreIds[0]], "_blank");
};

export const downloadESTAcknowledgement = async (application, tenants, t) => {
  const tenantInfo =
    tenants?.find((tenant) => tenant.code === application?.tenantId) ||
    tenants?.find((tenant) => tenant.code === Digit.ULBService.getCurrentTenantId()) ||
    {};
  const ackData = await getESTAllotmentAcknowledgementData(application, tenantInfo, t);
  Digit.Utils.pdf.generate(ackData);
};

export { ESTDocumnetPreview } from "./ESTDocumentPreview";
