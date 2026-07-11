import React, { useMemo, useCallback } from "react";
import {
  Header,
  DynamicForm,
  attachRouteConfigToStepData,
  mergeRouteConfig,
} from "@nudmcdgnpm/digit-ui-react-components";
import { useTranslation } from "react-i18next";

/**
 * Generic MDMS wizard form step — merges step config with local overrides, renders DynamicForm.
 */
const ESTDynamicFormStep = ({
  config,
  localOverrides,
  onSelect,
  persistedData,
  formData,
  isEditMode = false,
  editData = {},
  resetBaseline,
  draft,
}) => {
  const { t } = useTranslation();
  const sessionData = formData ?? persistedData ?? {};

  const routeConfig = useMemo(
    () => mergeRouteConfig(config, localOverrides),
    [config, localOverrides]
  );

  const tenantId = useMemo(() => Digit.ULBService.getCurrentTenantId(), []);

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

  return (
    <div className="employeeCard">
      <Header>
        {t(
          routeConfig.pageHeading?.create ||
            config?.sectionHeading ||
            config?.texts?.header ||
            "EST_FORM"
        )}
      </Header>
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
        draftLabel={routeConfig.draftButton?.label || draft?.label || "EST_ADD_AS_DRAFT"}
        draftSuccessLabel={
          routeConfig.draftButton?.successMessage || draft?.successLabel || "EST_DRAFT_SAVED"
        }
        onSaveDraft={onSaveDraft}
      />
    </div>
  );
};

export default ESTDynamicFormStep;
