import React, { useCallback, useMemo, useState } from "react";
import { DynamicCheckPage } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import {
  checkForNA,
  createAllotmentData,
  ESTDocumnetPreview,
  formatEpochDate,
} from "../../../utils";
import estateAllotmentFormConfig from "../../../config/Create/estateAllotmentFormConfig";

const ESTAssignAssetsCheckPage = ({ onSubmit, onError, value = {}, config = [] }) => {
  const { t } = useTranslation();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  const tenantId = useMemo(() => Digit.ULBService.getCurrentTenantId(), []);
  const mutation = Digit.Hooks.estate.useESTAssetsAllotment(tenantId);

  const rawRouteConfig = useMemo(() => {
    const steps = Array.isArray(config) ? config : [];
    return (
      steps.find((step) => step.key === "Allotments") ||
      steps.find((step) => Array.isArray(step.form) && step.form.length > 0) ||
      {}
    );
  }, [config]);

  const routeConfig = useMemo(
    () => ({ ...rawRouteConfig, ...estateAllotmentFormConfig }),
    [rawRouteConfig]
  );

  const assetData = value?.assetData || {};
  const extraData = {
    assetNo: assetData.estateNo,
    assetRefNumber: assetData.assetRefNumber,
    buildingName: assetData.buildingName,
    localityDisplay: assetData.locality,
    totalFloorArea: assetData.totalFloorArea,
    buildingFloor: assetData.buildingFloor || assetData.floor,
    assetRate: assetData.rate,
  };

  const editRoute = `${modulePath}/assignassets/assign-assets`;

  const formatAnyDate = (v) => {
    if (!v) return "N/A";
    if (typeof v === "number" || /^\d+$/.test(String(v))) return formatEpochDate(Number(v));
    const d = new Date(v);
    return isNaN(d.getTime()) ? "N/A" : d.toLocaleDateString("en-IN");
  };

  const handleFinalSubmit = useCallback(() => {
    if (isSubmitting) return;

    if (!Array.isArray(routeConfig.form) || routeConfig.form.length === 0) {
      console.error("EST_ALLOT: routeConfig.form is empty — refusing to submit");
      onError && onError(new Error("EST_ALLOT: empty form config"));
      return;
    }

    const payload = createAllotmentData(value);
    setIsSubmitting(true);

    mutation.mutate(payload, {
      onSuccess: (response) => {
        setIsSubmitting(false);
        onSubmit && onSubmit(response);
      },
      onError: (error) => {
        console.error("EST Allotment error:", error?.response?.data || error);
        setIsSubmitting(false);
        onError && onError(error);
      },
    });
  }, [isSubmitting, value, mutation, onSubmit, onError, routeConfig.form]);

  return (
    <DynamicCheckPage
      routeConfig={routeConfig}
      config={{ key: "Allotments" }}
      value={value}
      extraData={extraData}
      editRoute={editRoute}
      onSubmit={handleFinalSubmit}
      isSubmitting={isSubmitting}
      summaryHeaderCode="EST_ASSIGN_ASSETS_SUMMARY"
      defaultSectionHeaderCode="EST_ASSET_DETAILS"
      t={t}
      formatDate={formatAnyDate}
      checkNA={checkForNA}
      DocumentPreview={ESTDocumnetPreview}
    />
  );
};

export default ESTAssignAssetsCheckPage;
