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
import { filterEmpty, formatDate, formatDurationWithMonths, pick, checkForNA } from "./index";
import { buildAllotmentAssetDisplay, resolveLocalityDisplay } from "./estMdmsUtils";

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
  const base = {
    ...allotment,
    mobileNo: allotment.mobileNo ?? allotment.phoneNumber,
    alternateMobileNo: allotment.alternateMobileNo ?? allotment.altPhoneNumber,
    emailId: allotment.emailId ?? allotment.email,
    rentRate: allotment.rentRate ?? allotment.rate,
    assetNo: allotment.assetNo ?? asset.estateNo ?? asset.assetNo,
    eOfficeFileNo: allotment.eofficeFileNo ?? allotment.eOfficeFileNo,
    buildingName: allotment.buildingName ?? asset.buildingName,
    totalFloorArea: allotment.totalFloorArea ?? asset.totalFloorArea,
    buildingFloor: allotment.buildingFloor ?? asset.buildingFloor ?? asset.floor,
    assetRate: allotment.rentRate ?? allotment.rate ?? asset.rate,
    localityDisplay: allotment.localityDisplay ?? resolveLocalityDisplay(asset),
  };

  const normalized = normalizeAllotmentFlatData(base, asset, routeConfig);
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
    ["EST_ASSET_NUMBER", extraData.assetNo || formValues.assetNo],
    ["EST_ASSET_REFERENCE_NUMBER", extraData.assetRefNumber || formValues.assetRefNumber],
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
