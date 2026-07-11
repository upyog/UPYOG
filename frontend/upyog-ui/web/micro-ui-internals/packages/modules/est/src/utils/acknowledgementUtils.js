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
import { resolveLocalityDisplay } from "./estMdmsUtils";

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

  const asset =
    Assets[0] ||
    assetData ||
    assetData?.Assetdata ||
    Assetdata ||
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
export const buildAllotmentAckExtraData = (asset = {}, allotment = {}, t = (k) => k) => ({
  assetNo: pick(allotment.assetNo, asset.estateNo, asset.assetNo),
  assetRefNumber: pick(
    allotment.assetReferenceNo,
    allotment.assetRefNumber,
    asset.refAssetNo,
    asset.assetRef
  ),
  buildingName: pick(allotment.buildingName, asset.buildingName),
  localityDisplay: resolveLocalityDisplay(asset, t),
  totalFloorArea: pick(allotment.totalFloorArea, asset.totalFloorArea),
  buildingFloor: pick(allotment.buildingFloor, asset.buildingFloor, asset.floor),
  assetRate: pick(allotment.rentRate, allotment.rate, asset.rate),
});

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

  return sections
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
};
