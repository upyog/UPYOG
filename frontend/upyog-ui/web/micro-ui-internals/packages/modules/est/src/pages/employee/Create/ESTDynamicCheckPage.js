/**
 * ESTDynamicCheckPage.js
 * Unified EST check page — wraps DynamicCheckPage for registration and allotment wizards.
 * Pass flow="registration" | flow="allotment" (see estCheckPageConfig.js).
 */
import React, { useCallback, useMemo } from "react";
import {
  DynamicCheckPage,
  formatCheckPageDate,
  useDynamicCheckSubmit,
  useMergedRouteConfig,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { EST_CHECK_FLOWS } from "../../../config/estCheckPageConfig";
import estateAllotmentFormOverrides from "../../../config/Create/estateAllotmentFormOverrides";
import estateFormConfig from "../../../config/estateFormConfig";
import { checkForNA, createAllotmentData, ESTDocumnetPreview } from "../../../utils";
import { buildDynamicAssetPayload, getEstateRequestInfo } from "../../../utils/assetPayloadUtils";
import { getCreateAssetPath } from "../../../utils/estRoutes";
import { buildAllotmentAssetDisplay } from "../../../utils/estMdmsUtils";

/**
 * Unified EST check page — renders DynamicCheckPage for any wizard flow.
 * Pass flow="registration" | flow="allotment" (see estCheckPageConfig.js).
 */
const ESTDynamicCheckPage = ({
  flow,
  onSubmit,
  onError,
  value = {},
  config,
}) => {
  const flowConfig = EST_CHECK_FLOWS[flow];
  const stepKey = flowConfig?.stepKey || "newRegistration";
  const logTag = flowConfig?.logTag || "EST_CHECK";
  const summaryHeaderCode = flowConfig?.summaryHeaderCode || "EST_REGISTRATION_SUMMARY";
  const defaultSectionHeaderCode = flowConfig?.defaultSectionHeaderCode || "EST_ASSET_DETAILS";
  const isRegistration = flow === "registration";
  const isAllotment = flow === "allotment";

  const { t } = useTranslation();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const tenantId = useMemo(() => Digit.ULBService.getCurrentTenantId(), []);

  const createMutation = Digit.Hooks.estate.useESTCreateAPI(tenantId);
  const allotMutation = Digit.Hooks.estate.useESTAssetsAllotment(tenantId);
  const mutation = isRegistration ? createMutation : allotMutation;

  const routeConfig = useMergedRouteConfig(
    config,
    stepKey,
    value,
    isAllotment ? estateAllotmentFormOverrides : estateFormConfig
  );

  const flatAsset = useMemo(() => {
    if (!isRegistration) return {};
    const payloadKey = routeConfig?.payloadKey || "Assets";
    const saved = value?.[stepKey]?.[payloadKey] || value?.[stepKey]?.Assets;
    if (Array.isArray(saved)) return saved[0] || {};
    return saved || {};
  }, [isRegistration, value, routeConfig, stepKey]);

  const extraData = useMemo(() => {
    if (!isAllotment) return {};
    return buildAllotmentAssetDisplay(value?.assetData || {}, {}, t);
  }, [isAllotment, value, t]);

  const buildPayload = useCallback(() => {
    if (isRegistration) {
      return {
        RequestInfo: getEstateRequestInfo({
          msgId: `${Date.now()}|en_IN`,
          plainAccessRequest: {},
          apiId: routeConfig?.apiId,
        }),
        Assets: [buildDynamicAssetPayload(routeConfig, flatAsset, tenantId)],
      };
    }
    return createAllotmentData(value, routeConfig);
  }, [isRegistration, routeConfig, flatAsset, tenantId, value]);

  const { isSubmitting, handleSubmit } = useDynamicCheckSubmit({
    routeConfig,
    buildPayload,
    mutation,
    onSubmit,
    onError,
    logTag,
  });

  const editRoute = isRegistration
    ? getCreateAssetPath(modulePath)
    : `${modulePath}/assignassets/assign-assets`;

  const editNavigationState = useMemo(
    () => (isRegistration ? { editData: flatAsset } : undefined),
    [isRegistration, flatAsset]
  );

  if (!flowConfig) {
    console.error(`ESTDynamicCheckPage: unknown flow "${flow}"`);
    return null;
  }

  return (
    <DynamicCheckPage
      routeConfig={routeConfig}
      config={{ key: stepKey }}
      value={value}
      extraData={isAllotment ? extraData : undefined}
      editRoute={editRoute}
      editNavigationState={editNavigationState}
      onSubmit={handleSubmit}
      isSubmitting={isSubmitting}
      summaryHeaderCode={summaryHeaderCode}
      defaultSectionHeaderCode={defaultSectionHeaderCode}
      t={t}
      formatDate={formatCheckPageDate}
      checkNA={checkForNA}
      DocumentPreview={ESTDocumnetPreview}
    />
  );
};

export default ESTDynamicCheckPage;
