import React, { useCallback, useMemo } from "react";
import {
  DynamicCheckPage,
  formatCheckPageDate,
  useDynamicCheckSubmit,
  useDynamicRouteConfig,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { checkForNA, ESTDocumnetPreview } from "../../../utils";
import { buildDynamicAssetPayload, getEstateRequestInfo } from "../../../utils/assetPayloadUtils";
import { getCreateAssetPath } from "../../../utils/estRoutes";

const STEP_KEY = "newRegistration";

const ESTRegCheckPage = ({ onSubmit, onError, value = {}, config }) => {
  const { t } = useTranslation();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const tenantId = useMemo(() => Digit.ULBService.getCurrentTenantId(), []);
  const mutation = Digit.Hooks.estate.useESTCreateAPI(tenantId);

  const routeConfig = useDynamicRouteConfig(config, STEP_KEY, value);

  const flatAsset = useMemo(() => {
    const payloadKey = routeConfig?.payloadKey || "Assets";
    const saved = value?.[STEP_KEY]?.[payloadKey] || value?.[STEP_KEY]?.Assets;
    if (Array.isArray(saved)) return saved[0] || {};
    return saved || {};
  }, [value, routeConfig]);

  const buildPayload = useCallback(
    () => ({
      RequestInfo: getEstateRequestInfo({
        msgId: `${Date.now()}|en_IN`,
        plainAccessRequest: {},
        apiId: routeConfig?.apiId,
      }),
      Assets: [buildDynamicAssetPayload(routeConfig, flatAsset, tenantId)],
    }),
    [routeConfig, flatAsset, tenantId]
  );

  const { isSubmitting, handleSubmit } = useDynamicCheckSubmit({
    routeConfig,
    buildPayload,
    mutation,
    onSubmit,
    onError,
    logTag: "EST_CREATE",
  });

  const editRoute = getCreateAssetPath(modulePath);
  const editNavigationState = useMemo(() => ({ editData: flatAsset }), [flatAsset]);

  return (
    <DynamicCheckPage
      routeConfig={routeConfig}
      config={{ key: STEP_KEY }}
      value={value}
      editRoute={editRoute}
      editNavigationState={editNavigationState}
      onSubmit={handleSubmit}
      isSubmitting={isSubmitting}
      summaryHeaderCode="EST_REGISTRATION_SUMMARY"
      defaultSectionHeaderCode="EST_ASSET_DETAILS"
      t={t}
      formatDate={formatCheckPageDate}
      checkNA={checkForNA}
      DocumentPreview={ESTDocumnetPreview}
    />
  );
};

export default ESTRegCheckPage;
