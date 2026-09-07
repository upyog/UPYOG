/**
 * Config-driven acknowledgement section builders for EST allotment PDF.
 * Reuses the same routeConfig.form + summary helpers as DynamicCheckPage.
 */
import {
  buildSummarySections,
  mergeRouteConfig,
  rehydrateBillingCycleOption,
  resolveFieldLabelKey,
  resolveSummaryFieldValue,
} from "@nudmcdgnpm/digit-ui-react-components";
import estateAllotmentFormOverrides from "../config/Create/estateAllotmentFormOverrides";
import { normalizeAllotmentFlatData } from "./allotmentFormUtils";
import { filterEmpty, formatDate, formatDurationWithMonths, checkForNA } from "./index";
import {
  buildAllotmentAssetDisplay,
  getFileReferenceNumber,
  resolveAllotmentAsset,
} from "./estMdmsUtils";

const isEmptyAckValue = (value, t) => {
  if (value === undefined || value === null || value === "") return true;
  const na = t("NA");
  return value === na || value === "NA" || value === t("EST_NA") || value === "EST_NA";
};

/** Pull asset + allotment rows from acknowledgement payload shapes. */
export const resolveAllotmentAckContext = (application = {}) => {
  const {
    Assets = [],
    Allotments = [],
    assetData,
    Assetdata,
    AssignAssetsData,
  } = application;

  // Prefer session assetData (registered asset), then Assets[0] from ack merge.
  const asset =
    (assetData && Object.keys(assetData).length ? assetData : null) ||
    Assets[0] ||
    assetData?.Assetdata ||
    Assetdata ||
    AssignAssetsData?.asset ||
    application?.asset ||
    {};

  const allotment =
    Allotments[0] ||
    AssignAssetsData?.AllotmentData ||
    application?.allotment ||
    {};

  const routeConfig = mergeRouteConfig(
    application?.routeConfigs?.Allotments || {},
    estateAllotmentFormOverrides
  );

  return { asset, allotment, routeConfig };
};

/** extraData mirror for asset prefill fields (same idea as ESTDynamicCheckPage). */
export const buildAllotmentAckExtraData = (asset = {}, allotment = {}, t = (k) => k) =>
  buildAllotmentAssetDisplay(asset, allotment, t);

/** Normalize API/session allotment into form field names used by routeConfig. */
export const buildAllotmentAckFormValues = (allotment = {}, asset = {}, routeConfig = {}) => {
  const toFileValue = (raw) => {
    if (raw === undefined || raw === null || raw === "") return undefined;
    if (typeof raw === "object") return raw;
    const id = String(raw);
    return { filestoreId: id, fileStoreId: id };
  };

  const display = buildAllotmentAssetDisplay(asset, allotment);
  const resolvedAsset = resolveAllotmentAsset(asset, allotment);
  const displayFields = Object.fromEntries(
    Object.entries(display).filter(([, v]) => v !== undefined && v !== null && v !== "")
  );

  const base = {
    ...allotment,
    ...displayFields,
    allotmentType: allotment.allotmentType ?? allotment.propertyType,
    mobileNo: allotment.mobileNo ?? allotment.phoneNumber,
    alternateMobileNo: allotment.alternateMobileNo ?? allotment.altPhoneNumber,
    emailId: allotment.emailId ?? allotment.email,
    rentRate: allotment.rentRate ?? allotment.rate,
    // Flatten nested Allotments[].asset into form field names used by MDMS.
    buildingName:
      display.buildingName ||
      resolvedAsset.buildingName ||
      resolvedAsset.assetName ||
      "",
    locality:
      display.locality ||
      display.localityDisplay ||
      "",
    localityDisplay: display.localityDisplay || display.locality || "",
    totalFloorArea:
      display.totalFloorArea ?? resolvedAsset.totalFloorArea ?? "",
    buildingFloor: display.buildingFloor ?? display.floor ?? "",
    floor: display.floor ?? display.buildingFloor ?? "",
    assetRate: display.assetRate ?? allotment.rentRate ?? resolvedAsset.rate ?? "",
    assetRefNumber: display.assetRefNumber || "",
    // fileReferenceNumber lives in additionalDetails — expose as form field only.
    fileReferenceNumber: getFileReferenceNumber(allotment, resolvedAsset),
    citizenLetter: toFileValue(
      allotment.citizenLetter ?? allotment.citizenRequestLetter
    ),
    allotmentLetter: toFileValue(allotment.allotmentLetter),
    signedDeed: toFileValue(allotment.signedDeed),
  };
  delete base.eofficeFileNo;
  delete base.eOfficeFileNo;
  delete base.asset;
  delete base.Asset;

  const normalized = normalizeAllotmentFlatData(base, resolvedAsset, routeConfig);
  normalized.billingCycle = rehydrateBillingCycleOption(normalized, routeConfig);
  return normalized;
};

const resolveAckFieldValue = (
  fieldConfig,
  { formValues, extraData, t, formatDateFn }
) => {
  const { field } = fieldConfig;
  if (!field) return "";

  if (field.name === "duration" || field.computeFn === "calculateDuration") {
    const raw = formValues.duration ?? extraData.duration;
    if (raw === undefined || raw === null || raw === "") return "";
    const readable = formatDurationWithMonths({ duration: raw });
    if (readable) return readable;
  }

  const value = resolveSummaryFieldValue(fieldConfig, {
    formValues,
    extraData,
    formatDate: formatDateFn,
    checkNA: (val) => checkForNA(val),
    t,
  });

  return isEmptyAckValue(value, t) ? "" : value;
};

/**
 * Build PDF `details` sections from routeConfig.form (MDMS + local overrides).
 * First untitled block becomes "registered asset details" (EST_ASSET_DETAILS).
 */
export const buildAckDetailsFromRouteConfig = ({
  routeConfig,
  formValues = {},
  extraData = {},
  t = (k) => k,
  formatDateFn = formatDate,
  defaultAssetSectionCode = "EST_ASSET_DETAILS",
}) => {
  const { sections } = buildSummarySections(routeConfig?.form || []);

  const fromConfig = sections
    .map((section, index) => {
      const headerCode =
        section.headerCode || (index === 0 ? defaultAssetSectionCode : null);

      const values = filterEmpty(
        section.fields
          .map((fc) => {
            const value = resolveAckFieldValue(fc, {
              formValues,
              extraData,
              t,
              formatDateFn,
            });
            if (!value) return null;
            return {
              title: t(resolveFieldLabelKey(fc, formValues)),
              value,
            };
          })
          .filter(Boolean)
      );

      if (!values.length || !headerCode) return null;

      return {
        title: t(headerCode),
        asSectionHeader: true,
        values,
      };
    })
    .filter(Boolean);

  if (fromConfig.length) return fromConfig;

  // Fallback when routeConfig was not stored on ack state — still show register details.
  const fallbackPairs = [
    ["EST_ALLOTMENT_NUMBER", extraData.allotmentNo || formValues.allotmentNo],
    ["EST_ESTATE_NUMBER", extraData.estateNo || formValues.estateNo],
    ["FILE_REFERENCE_NUMBER", extraData.fileReferenceNumber || formValues.fileReferenceNumber],
    ["EST_BUILDING_NAME", extraData.buildingName || formValues.buildingName],
    ["EST_LOCALITY", extraData.localityDisplay || formValues.localityDisplay],
    ["EST_TOTAL_AREA", extraData.totalFloorArea || formValues.totalFloorArea],
    ["EST_FLOOR", extraData.buildingFloor || formValues.buildingFloor],
    ["EST_RATE", extraData.assetRate || formValues.assetRate || formValues.rentRate],
    ["EST_ALLOTTEE_NAME", formValues.alloteeName || formValues.allotteeName],
    ["EST_PHONE_NUMBER", formValues.mobileNo || formValues.phoneNumber],
    ["EST_EMAIL_ID", formValues.emailId || formValues.email],
    ["EST_AGREEMENT_START_DATE", formValues.agreementStartDate],
    ["EST_AGREEMENT_END_DATE", formValues.agreementEndDate],
    ["EST_DURATION", formValues.duration],
    ["EST_BILLING_CYCLE", formValues.billingCycle],
    ["EST_MONTHLY_RENT_IN_INR", formValues.monthlyRent],
    ["EST_ADVANCE_PAYMENT_IN_INR", formValues.advancePayment],
  ];

  const values = filterEmpty(
    fallbackPairs
      .map(([titleKey, raw]) => {
        if (raw === undefined || raw === null || raw === "") return null;
        let value = raw;
        if (titleKey.includes("DATE")) value = formatDateFn(raw) || raw;
        if (titleKey === "EST_DURATION") {
          value = formatDurationWithMonths({ duration: raw }) || raw;
        }
        if (typeof value === "object") {
          value = t(value.i18nKey || value.name || value.code || "");
        }
        if (!value || isEmptyAckValue(value, t)) return null;
        return { title: t(titleKey), value: String(value) };
      })
      .filter(Boolean)
  );

  return values.length
    ? [{ title: t(defaultAssetSectionCode), asSectionHeader: true, values }]
    : [];
};
