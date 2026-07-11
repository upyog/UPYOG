import React, { useCallback, useMemo } from "react";
import {
  DynamicCheckPage,
  formatCheckPageDate,
  mergeRouteConfig,
  resolveRouteConfigFromSteps,
  useDynamicCheckSubmit,
  useDynamicRouteConfig,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { checkForNA, createAllotmentData, ESTDocumnetPreview } from "../../../utils";
import estateAllotmentFormConfig from "../../../config/Create/estateAllotmentFormConfig";

const STEP_KEY = "Allotments";

const ESTAssignAssetsCheckPage = ({ onSubmit, onError, value = {}, config = [] }) => {
  const { t } = useTranslation();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const tenantId = useMemo(() => Digit.ULBService.getCurrentTenantId(), []);
  const mutation = Digit.Hooks.estate.useESTAssetsAllotment(tenantId);

  const sessionRouteConfig = useDynamicRouteConfig(config, STEP_KEY, value);
  const routeConfig = useMemo(
    () =>
      mergeRouteConfig(resolveRouteConfigFromSteps(config, STEP_KEY), {
        ...estateAllotmentFormConfig,
        ...sessionRouteConfig,
      }),
    [config, sessionRouteConfig]
  );

  const assetData = value?.assetData || {};
  const extraData = useMemo(
    () => ({
      assetNo: assetData.estateNo,
      assetRefNumber: assetData.assetRefNumber,
      buildingName: assetData.buildingName,
      localityDisplay: assetData.locality,
      totalFloorArea: assetData.totalFloorArea,
      buildingFloor: assetData.buildingFloor || assetData.floor,
      assetRate: assetData.rate,
    }),
    [assetData]
  );

  const buildPayload = useCallback(
    () => createAllotmentData(value, routeConfig),
    [value, routeConfig]
  );

  const { isSubmitting, handleSubmit } = useDynamicCheckSubmit({
    routeConfig,
    buildPayload,
    mutation,
    onSubmit,
    onError,
    logTag: "EST_ALLOT",
  });

  const editRoute = `${modulePath}/assignassets/assign-assets`;

  return (
    <DynamicCheckPage
      routeConfig={routeConfig}
      config={{ key: STEP_KEY }}
      value={value}
      extraData={extraData}
      editRoute={editRoute}
      onSubmit={handleSubmit}
      isSubmitting={isSubmitting}
      summaryHeaderCode="EST_ASSIGN_ASSETS_SUMMARY"
      defaultSectionHeaderCode="EST_ASSET_DETAILS"
      t={t}
      formatDate={formatCheckPageDate}
      checkNA={checkForNA}
      DocumentPreview={ESTDocumnetPreview}
    />
  );
};

export default ESTAssignAssetsCheckPage;
