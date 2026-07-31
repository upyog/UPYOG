/**
 * DynamicFormStep.js
 *
 * Shared MDMS-driven wizard step wrapper for UPYOG module create/edit flows.
 * Use this in module wizard routes instead of writing a thin per-module wrapper
 * around DynamicForm for each step.
 *
 * Responsibilities
 * ----------------
 * 1. Merge the MDMS step `config` with module-local `localOverrides` via
 *    `mergeRouteConfig` (behavior keys like staticFields / computedFields /
 *    crossFieldValidations, plus fallbacks for payloadKey, pageHeading, etc.).
 * 2. Resolve tenantId (prop or Digit.ULBService.getCurrentTenantId()).
 * 3. Render a Header from pageHeading / sectionHeading / texts.header, then
 *    delegate field rendering, validation, and submit UX to DynamicForm.
 * 4. On step select, attach the resolved routeConfig onto step data with
 *    `attachRouteConfigToStepData` so downstream check/summary pages and
 *    payload builders can read the same form metadata.
 * 5. Optionally wire draft save/clear when a `draft` object is provided
 *    (`onPersist`, `buildStepData`, `onClear`, labels).
 *
 * Data flow
 * ---------
 *   Module page (e.g. ESTAssignAssets)
 *     → DynamicFormStep (merge config, header, draft adapters)
 *       → DynamicForm (fields, validate, buildPayload)
 *         → onSelect / onSaveDraft back through DynamicFormStep
 *           → parent wizard session (+ routeConfig snapshot)
 *
 * Typical usage (wizard step component)
 * -------------------------------------
 *   <DynamicFormStep
 *     config={routeConfigFromMdms}
 *     localOverrides={{ computedFields, crossFieldValidations, ... }}
 *     onSelect={onSelect}
 *     formData={formData}
 *     isEditMode={isEditMode}
 *     editData={editData}
 *     draft={{ onPersist, buildStepData, onClear, label, successLabel }}
 *     t={t}
 *     onFieldSearch={handleFieldSearch}
 *   />
 *
 * Props
 * -----
 * @param {object}   config              MDMS (or resolved) route/step config for this wizard page.
 * @param {object}   [localOverrides]    Module-local overrides merged on top of config.
 * @param {Function} [onSelect]          Wizard onSelect(key, data, skipStep, index, isAddMultiple).
 * @param {object}   [persistedData]     Previously saved step data (fallback when formData is absent).
 * @param {object}   [formData]          Live wizard session data; preferred over persistedData.
 * @param {boolean}  [isEditMode=false]  When true, DynamicForm hydrates from editData.
 * @param {object}   [editData]          Existing application / record values for edit mode.
 * @param {*}        [resetBaseline]     Passed through to DynamicForm for form reset baselines.
 * @param {object}   [draft]             Optional draft API:
 *                                       - onPersist(stepKey, stepData)
 *                                       - buildStepData(flatOrSaved) → shaped step payload
 *                                         e.g. (flat) => ({ Allotments: [{ ...flat }] })
 *                                       - onClear(stepKey)
 *                                       - label / successLabel for the draft button
 * @param {Function} [t]                 i18n translator; defaults to identity (key → key).
 * @param {string}   [tenantId]          Tenant override; otherwise current ULB tenant.
 * @param {string}   [wrapperClassName]  Outer wrapper class (default "employeeCard").
 * @param {string}   [defaultHeaderCode] Fallback header i18n key (default "COMMON_FORM").
 * @param {Function} [onFieldSearch]     Optional field-level search handler for DynamicForm.
 *
 * @see DynamicForm
 * @see mergeRouteConfig
 * @see attachRouteConfigToStepData
 */

import React, { useMemo, useCallback } from "react";
import Header from "../atoms/Header";
import DynamicForm from "./DynamicForm";
import { attachRouteConfigToStepData, mergeRouteConfig } from "../utilities/checkPageUtils";

/**
 * Wizard step shell: merges MDMS + local overrides, renders Header + DynamicForm,
 * and bridges DynamicForm callbacks into the parent wizard (onSelect / draft).
 *
 * @param {object} props — see file-level Props section above.
 * @returns {JSX.Element}
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
  /**
   * i18n helper. Uses the `t` prop when provided; otherwise identity so missing
   * translators still render raw localization keys instead of crashing.
   *
   * @param {string} key
   * @returns {string}
   */
  const t = tProp || ((key) => key);

  /**
   * Session data fed into DynamicForm as `persistedData`.
   * Prefers live wizard `formData`, then `persistedData`, then `{}`.
   *
   * @type {object}
   */
  const sessionData = formData ?? persistedData ?? {};

  /**
   * Merges MDMS step config with module-local overrides (computedFields,
   * crossFieldValidations, staticFields, payloadKey / pageHeading fallbacks,
   * and per-field overlays via mergeFormFieldConfigs).
   * Recalculates only when `config` or `localOverrides` change.
   *
   * @returns {object} Resolved routeConfig passed to DynamicForm and step handlers.
   */
  const routeConfig = useMemo(
    () => mergeRouteConfig(config, localOverrides),
    [config, localOverrides]
  );

  /**
   * Resolves the tenant for MDMS / uploads / payloads.
   * Uses the `tenantId` prop when provided; otherwise current ULB tenant
   * from Digit.ULBService.getCurrentTenantId().
   *
   * @returns {string} Tenant id string.
   */
  const tenantId = useMemo(
    () => tenantIdProp || Digit.ULBService.getCurrentTenantId(),
    [tenantIdProp]
  );

  /**
   * DynamicForm `onSubmit` sink for this step.
   * Create flow advances via `onSelect`; here we only log submit errors
   * (e.g. edit-mode mutation failures bubbled from DynamicForm.goNext).
   *
   * @param {{ error?: Error, payload?: object, isEditMode?: boolean }} result
   */
  const handleSubmit = useCallback(({ error }) => {
    if (error) console.error("Submit error:", error);
  }, []);

  /**
   * Default wizard `onSelect` path (used when `draft.buildStepData` is absent).
   * Forwards to the parent wizard after attaching the merged routeConfig onto
   * step data (`__routeConfig` marker) so check/summary pages can read the
   * same form metadata the user saw on this step.
   *
   * @param {string}  key             Step key from config / routeConfig.
   * @param {object}  data            Step payload from DynamicForm, typically
   *                                  `{ [payloadKey]: [formVal] }`.
   * @param {boolean} [skipStep]      Wizard skip flag.
   * @param {number}  [index]         Multi-entry index when applicable.
   * @param {boolean} [isAddMultiple] Wizard "add another" flag.
   */
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

  /**
   * Wizard `onSelect` path when `draft.buildStepData` is provided (e.g. EST).
   *
   * DynamicForm already shaped data as `{ [payloadKey]: [formVal] }`. This:
   * 1. Extracts the first item (`saved`) under `routeConfig.payloadKey`
   * 2. Re-wraps it via module `buildStepData`
   *    (e.g. `(flat) => ({ Allotments: [{ ...flat }] })`)
   * 3. Attaches routeConfig and calls parent `onSelect`
   *
   * Falls back to `handleSelect` if `buildStepData` is missing at call time.
   *
   * @param {string}  key             Step key from config / routeConfig.
   * @param {object}  data            Step payload from DynamicForm.
   * @param {boolean} [skipStep]      Wizard skip flag.
   * @param {number}  [index]         Multi-entry index when applicable.
   * @param {boolean} [isAddMultiple] Wizard "add another" flag.
   */
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

  /**
   * Adapter for DynamicForm's explicit "Save Draft" button.
   * Receives the flat payload from `buildPayload(formData)`, optionally reshapes
   * it with `draft.buildStepData`, then calls
   * `draft.onPersist(stepKey, stepData)`.
   *
   * Undefined when `draft.onPersist` is not provided — DynamicForm then hides
   * the draft button (`showDraftButton={Boolean(draft?.onPersist)}`).
   *
   * @param {object} flat - Flat field map from DynamicForm.buildPayload.
   * @returns {void}
   */
  const onSaveDraft = draft?.onPersist
    ? (flat) => draft.onPersist(routeConfig.key, draft.buildStepData?.(flat) || flat)
    : undefined;

  /**
   * Header i18n key resolution order:
   * 1. routeConfig.pageHeading.create
   * 2. config.sectionHeading
   * 3. config.texts.header
   * 4. defaultHeaderCode prop (default "COMMON_FORM")
   *
   * @type {string}
   */
  const headerCode =
    routeConfig.pageHeading?.create ||
    config?.sectionHeading ||
    config?.texts?.header ||
    defaultHeaderCode;

  const translatedHeader = t(headerCode);
  const headerText =
    translatedHeader !== headerCode
      ? translatedHeader
      : routeConfig.pageHeading?.fallback || translatedHeader;

  /**
   * Clears draft for this step when the user hits Cancel in DynamicForm.
   * Invokes `draft.onClear(routeConfig.key)` when a draft API is wired;
   * no-op otherwise (DynamicForm still resets its own form state).
   *
   * @returns {void}
   */
  const handleCancel = () => draft?.onClear?.(routeConfig.key);

  return (
    <div className={wrapperClassName}>
      <Header>{headerText}</Header>
      <DynamicForm
        routeConfig={routeConfig}
        onSubmit={handleSubmit}
        // Prefer draft-aware select when buildStepData can reshape the step payload
        onSelect={draft?.buildStepData ? handleDraftSelect : handleSelect}
        config={config || { key: routeConfig.key }}
        persistedData={sessionData}
        isEditMode={isEditMode}
        editData={editData || {}}
        resetBaseline={resetBaseline}
        tenantId={tenantId}
        t={t}
        showCancel
        onCancel={handleCancel}
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
