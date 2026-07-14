import React, { useMemo, useCallback } from "react";
import Header from "../atoms/Header";
import DynamicForm from "./DynamicForm";
import { attachRouteConfigToStepData, mergeRouteConfig } from "../utilities/checkPageUtils";

/**
 * MDMS wizard form step — merges step config with local overrides, renders DynamicForm.
 * Use in module wizard routes instead of per-module wrappers around DynamicForm.
 */
const DynamicFormStep = ({
  config,
  localOverrides = {},
  onSelect,
  persistedData,
  formData,
  isEditMode = false,
  editData = {},
  resetBaseline,
  draft,
  t: tProp,
  tenantId: tenantIdProp,
  wrapperClassName = "employeeCard",
  defaultHeaderCode = "COMMON_FORM",
  onFieldSearch,
}) => {
  const t = tProp || ((key) => key);
  const sessionData = formData ?? persistedData ?? {};

  const routeConfig = useMemo(
    () => mergeRouteConfig(config, localOverrides),
    [config, localOverrides]
  );

  const tenantId = useMemo(
    () => tenantIdProp || Digit.ULBService.getCurrentTenantId(),
    [tenantIdProp]
  );

  const handleSubmit = useCallback(({ error }) => {
    if (error) console.error("Submit error:", error);
  }, []);

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

  const handleDraftSelect = useCallback(
    (key, data, skipStep, index, isAddMultiple) => {
      if (!draft?.buildStepData) {
        handleSelect(key, data, skipStep, index, isAddMultiple);
        return;
      }
      const saved = data?.[routeConfig.payloadKey]?.[0] || {};
      onSelect?.(
        key,
        attachRouteConfigToStepData(draft.buildStepData(saved), routeConfig),
        skipStep,
        index,
        isAddMultiple
      );
    },
    [draft, handleSelect, onSelect, routeConfig]
  );

  const onSaveDraft = draft?.onPersist
    ? (flat) => draft.onPersist(routeConfig.key, draft.buildStepData?.(flat) || flat)
    : undefined;

  const headerCode =
    routeConfig.pageHeading?.create ||
    config?.sectionHeading ||
    config?.texts?.header ||
    defaultHeaderCode;

  return (
    <div className={wrapperClassName}>
      <Header>{t(headerCode)}</Header>
      <DynamicForm
        routeConfig={routeConfig}
        onSubmit={handleSubmit}
        onSelect={draft?.buildStepData ? handleDraftSelect : handleSelect}
        config={config || { key: routeConfig.key }}
        persistedData={sessionData}
        isEditMode={isEditMode}
        editData={editData || {}}
        resetBaseline={resetBaseline}
        tenantId={tenantId}
        t={t}
        showCancel={Boolean(draft?.onClear)}
        onCancel={() => draft?.onClear?.(routeConfig.key)}
        showDraftButton={Boolean(draft?.onPersist)}
        draftLabel={routeConfig.draftButton?.label || draft?.label || "CS_COMMON_SAVE_DRAFT"}
        draftSuccessLabel={
          routeConfig.draftButton?.successMessage || draft?.successLabel || "CS_COMMON_SAVED"
        }
        onSaveDraft={onSaveDraft}
        onFieldSearch={onFieldSearch}
      />
    </div>
  );
};

export default DynamicFormStep;
