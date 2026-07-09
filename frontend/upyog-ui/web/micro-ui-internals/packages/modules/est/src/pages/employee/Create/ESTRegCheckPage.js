import React, { useCallback, useMemo, useState } from "react";
import { DynamicCheckPage } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { checkForNA, ESTDocumnetPreview, formatEpochDate } from "../../../utils";
import { buildDynamicAssetPayload, getEstateRequestInfo } from "../../../utils/assetPayloadUtils";
import estateFormConfig from "../../../config/estateFormConfig";
import { getCreateAssetPath } from "../../../utils/estRoutes";

const ESTRegCheckPage = ({ onSubmit, onError, value = {}, config }) => {
  const { t } = useTranslation();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();

  const tenantId = useMemo(() => Digit.ULBService.getCurrentTenantId(), []);
  const mutation = Digit.Hooks.estate.useESTCreateAPI(tenantId);

  const rawRouteConfig = useMemo(() => {
    const steps = Array.isArray(config) ? config : config?.body || [];
    return (
      steps.find?.((step) => step.key === "newRegistration") ||
      steps.find?.((step) => Array.isArray(step.form) && step.form.length > 0) ||
      {}
    );
  }, [config]);

  const routeConfig = useMemo(
    () => ({ ...rawRouteConfig, ...estateFormConfig }),
    [rawRouteConfig]
  );

  const flatAsset = useMemo(() => {
    const payloadKey = routeConfig?.payloadKey || "Assets";
    const saved = value?.newRegistration?.[payloadKey] || value?.newRegistration?.Assets;
    if (Array.isArray(saved)) return saved[0] || {};
    return saved || {};
  }, [value, routeConfig]);

  const editRoute = getCreateAssetPath(modulePath);
  const editNavigationState = useMemo(() => ({ editData: flatAsset }), [flatAsset]);

  const formatAnyDate = (v) => {
    if (!v) return "N/A";
    if (typeof v === "number" || /^\d+$/.test(String(v))) return formatEpochDate(Number(v));
    const d = new Date(v);
    return isNaN(d.getTime()) ? "N/A" : d.toLocaleDateString("en-IN");
  };

  const handleFinalSubmit = useCallback(() => {
    if (isSubmitting) return;

    if (!Array.isArray(routeConfig.form) || routeConfig.form.length === 0) {
      console.error(
        "EST_CREATE: routeConfig.form is empty — refusing to submit an incomplete Asset. config prop was:",
        config
      );
      onError && onError(new Error("EST_CREATE: empty form config"));
      return;
    }

    const assetPayload = buildDynamicAssetPayload(routeConfig, flatAsset, tenantId);
    const payload = {
      RequestInfo: getEstateRequestInfo({ msgId: `${Date.now()}|en_IN`, plainAccessRequest: {} }),
      Assets: [assetPayload],
    };

    setIsSubmitting(true);

    mutation.mutate(payload, {
      onSuccess: (response) => {
        setIsSubmitting(false);
        onSubmit && onSubmit(response);
      },
      onError: (error) => {
        console.error("EST Create error status:", error?.response?.status);
        console.error("EST Create error response:", error?.response?.data);
        setIsSubmitting(false);
        onError && onError(error);
      },
    });
  }, [isSubmitting, routeConfig, flatAsset, tenantId, mutation, onSubmit, onError, config]);

  return (
    <DynamicCheckPage
      routeConfig={routeConfig}
      config={{ key: "newRegistration" }}
      value={value}
      editRoute={editRoute}
      editNavigationState={editNavigationState}
      onSubmit={handleFinalSubmit}
      isSubmitting={isSubmitting}
      summaryHeaderCode="EST_REGISTRATION_SUMMARY"
      defaultSectionHeaderCode="EST_ASSET_DETAILS"
      t={t}
      formatDate={formatAnyDate}
      checkNA={checkForNA}
      DocumentPreview={ESTDocumnetPreview}
    />
  );
};

export default ESTRegCheckPage;
