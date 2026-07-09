import React, { useMemo, useCallback } from "react";
import { Header, DynamicForm, attachRouteConfigToStepData } from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import { buildDynamicAssetPayload } from "../utils/assetPayloadUtils";
import estateFormConfig from "../config/estateFormConfig";

const NewRegistration = ({ onSelect, config, persistedData, isEditMode, editData }) => {
  const { t } = useTranslation();

  const routeConfig = useMemo(() => ({ ...config, ...estateFormConfig }), [config]);
  const tenantId = useMemo(() => Digit.ULBService.getCurrentTenantId(), []);

  // Dev-time preview only — real submit/mutation happens in ESTRegCheckPage,
  // via the same shared buildDynamicAssetPayload() so it can't drift.
  const handleSubmit = useCallback(
    ({ payload, error }) => {
      if (error) {
        console.error("Submit error:", error);
        return;
      }
      const flatAsset = payload?.Assets?.[0] || {};
      buildDynamicAssetPayload(routeConfig, flatAsset, tenantId);
    },
    [routeConfig, tenantId]
  );

  const handleSelect = useCallback(
    (key, data, skipStep, index, isAddMultiple) => {
      onSelect?.(
        key,
        attachRouteConfigToStepData(data, routeConfig),
        skipStep,
        index,
        isAddMultiple
      );
    },
    [onSelect, routeConfig]
  );

  return (
    <div className="employeeCard">
      <Header>{t(routeConfig.pageHeading?.create || "EST_COMMON_NEW_REGISTRATION")}</Header>
      <DynamicForm
        routeConfig={routeConfig}
        onSubmit={handleSubmit}
        onSelect={handleSelect}
        config={config || { key: routeConfig.key }}
        persistedData={persistedData || {}}
        isEditMode={isEditMode || false}
        editData={editData || {}}
        tenantId={tenantId}
        t={t}
      />
    </div>
  );
};

export default NewRegistration;