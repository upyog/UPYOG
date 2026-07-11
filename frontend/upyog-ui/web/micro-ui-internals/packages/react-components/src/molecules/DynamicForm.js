import React, { useState, useCallback, useMemo, useEffect, useRef } from "react";
import { SubmitBar, Toast, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import ActionBar from "../atoms/ActionBar";
import ButtonSelector from "../atoms/ButtonSelector";
import DynamicFormField from "./DynamicFormField";
import { validateFields, validateCrossField, calculateDuration, calculateRentByBillingCycle } from "../utilities/validators";
import { sortByOrder, buildPayload, scrollToFirstError, buildInitialData, flattenFormConfig, findFieldConfig, enrichDropdownSelection } from "../utilities/formUtils";
import useDynamicMDMS from "../utilities/useDynamicMDMS";
import { mapFormToSearchFilters } from "../utilities/searchUtils";
import styles from "../styles/dynamicForm.module.scss";

// ── Computed-field registry ────────────────────────────────────────────
// A field can declare: field: { computeFrom: ["startDate","endDate"], computeFn: "calculateDuration" }
// Whenever any of computeFrom changes, DynamicForm looks up computeFn here and
// re-derives that field's value automatically. Add more pure functions here
// as new modules need derived fields — no DynamicForm changes required.
const COMPUTE_REGISTRY = {
  calculateDuration,
  calculateRentByBillingCycle,
};

const DynamicForm = ({
  routeConfig,
  onSubmit,
  onSelect,
  config,
  isEditMode = false,
  isDisabled = false,
  updateMutation,
  editData = {},
  tenantId = "",
  persistedData = {},
  t = (k) => k,
  showCancel = false,
  cancelLabel = "CS_COMMON_CANCEL",
  onCancel,
  resetBaseline,
  showDraftButton = false,
  draftLabel = "EST_ADD_AS_DRAFT",
  draftSuccessLabel = "EST_DRAFT_SAVED",
  onSaveDraft,
  onPersistDraft,
  /** "wizard" (default) | "search" — search reuses the same fields/MDMS without wizard ActionBar */
  mode = "wizard",
}) => {
  const isSearchMode = mode === "search";
  const stateId = Digit.ULBService.getStateId();
  const payloadKey = routeConfig.payloadKey || "Assets";

  // ── Dropdown data: single generic source for ANY module's config ─────
  const { dropdownData, isLoading } = useDynamicMDMS(routeConfig.form, stateId, tenantId, t);

  // ── Raw pre-fill source ───────────────────────────────────────────────
  const rawAsset = useMemo(() => {
    const saved = persistedData?.[config?.key]?.[payloadKey];
    const persisted = (Array.isArray(saved) ? saved[0] : saved) || {};
    if (Object.keys(persisted).length > 0) return { ...editData, ...persisted };
    return editData && Object.keys(editData).length > 0 ? editData : {};
  }, [editData, persistedData, config, payloadKey]);

  // ── Build initialData generically from config + dropdownData ─────────
  const initialData = useMemo(
    () => buildInitialData(routeConfig.form, rawAsset, dropdownData, tenantId),
    [routeConfig.form, rawAsset, dropdownData, tenantId]
  );

  const [formData, setFormData] = useState(initialData);
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [crossFieldMessages, setCrossFieldMessages] = useState([]);
  const [toast, setToast] = useState(null); // { message, error } | null

  // Derived views of the config — flattened once, sorted once.
  const flatFields = useMemo(() => flattenFormConfig(routeConfig.form || []), [routeConfig.form]);
  const sortedFields = useMemo(() => sortByOrder(routeConfig.form || []), [routeConfig.form]);

  const billingCycleOptions = useMemo(() => {
    const billingField = findFieldConfig(routeConfig.form, "billingCycle");
    return (
      dropdownData?.billingCycle ||
      dropdownData?.[billingField?.key] ||
      billingField?.options ||
      []
    );
  }, [dropdownData, routeConfig.form]);

  const resolveComputeArgs = useCallback(
    (computeFrom, computeFn, data) =>
      computeFrom.map((dep) => {
        const val = data[dep];
        if (dep === "billingCycle") {
          return enrichDropdownSelection(val, billingCycleOptions);
        }
        return val;
      }),
    [billingCycleOptions]
  );

  // Re-derive any field whose computeFrom list includes a field that just changed.
  const applyComputedFields = useCallback(
    (updated, changedFieldNames) => {
      let next = updated;
      const pending = new Set(changedFieldNames);
      let iterations = 0;

      while (pending.size > 0 && iterations < 10) {
        iterations += 1;
        const round = [...pending];
        pending.clear();

        flatFields.forEach((fc) => {
          const { computeFrom, computeFn, name } = fc.field || {};
          if (!computeFrom || !computeFn) return;
          if (!computeFrom.some((dep) => round.includes(dep))) return;

          const fn = COMPUTE_REGISTRY[computeFn];
          if (!fn) return;

          const args = resolveComputeArgs(computeFrom, computeFn, next);
          const newVal =
            computeFn === "calculateRentByBillingCycle"
              ? fn(...args, billingCycleOptions)
              : fn(...args);
          if (next[name] !== newVal) {
            next = { ...next, [name]: newVal };
            pending.add(name);
          }
        });
      }

      return next;
    },
    [flatFields, billingCycleOptions, resolveComputeArgs]
  );

  // Sync formData with initialData EXACTLY ONCE, right after dropdownData
  // has finished loading. Guarded with a ref so it can never fire again,
  // regardless of how many times initialData's identity changes afterward.
  // The freshly-built initialData wins over the pre-load snapshot so options
  // resolved against REAL dropdown data replace synthesized placeholders;
  // anything the user already typed (non-empty) is preserved on top.
  const hasSyncedRef = useRef(false);

  useEffect(() => {
    if (hasSyncedRef.current || isLoading) return;
    hasSyncedRef.current = true;
    setFormData((prev) => {
      const userTouched = Object.fromEntries(
        Object.entries(prev).filter(([, v]) => v !== "" && v !== null && v !== undefined)
      );
      const merged = { ...initialData, ...userTouched };
      const allComputeDeps = flatFields.flatMap((fc) => fc.field?.computeFrom || []);
      return allComputeDeps.length ? applyComputedFields(merged, allComputeDeps) : merged;
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoading]);

  const handleChange = useCallback(
    (fieldName, value, resetFields = []) => {
      setFormData((prev) => {
        let resolvedValue = value;
        if (fieldName === "billingCycle") {
          resolvedValue = enrichDropdownSelection(value, billingCycleOptions);
        }
        let updated = { ...prev, [fieldName]: resolvedValue };
        resetFields.forEach((f) => { updated[f] = null; });
        return applyComputedFields(updated, [fieldName, ...resetFields]);
      });
      setErrors((prev) => {
        const updated = { ...prev, [fieldName]: false };
        resetFields.forEach((f) => { updated[f] = false; });
        return updated;
      });
    },
    [applyComputedFields, billingCycleOptions]
  );

  // Optional: auto-save when onPersistDraft is set and no explicit draft button.
  useEffect(() => {
    if (!onPersistDraft || showDraftButton || isLoading || !hasSyncedRef.current) return;

    const timer = setTimeout(() => {
      onPersistDraft(buildPayload(formData));
    }, 400);

    return () => clearTimeout(timer);
  }, [formData, onPersistDraft, showDraftButton, isLoading]);

  // ── File upload: owns tenantId + filestore call + error toast ────────
  // Module code defaults to "ESTATE"; override per-route via routeConfig.uploadModule.
  const handleFileUpload = useCallback(
    async (fieldName, file) => {
      if (!file) return;
      if (file.size >= 5242880) {
        setToast({ message: t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"), error: true });
        return;
      }
      try {
        const response = await Digit.UploadServices.Filestorage(
          routeConfig.uploadModule || "ESTATE",
          file,
          tenantId
        );
        const id = response?.data?.files?.[0]?.fileStoreId;
        if (id) {
          handleChange(fieldName, { filestoreId: id, documentuuid: id, documentType: fieldName });
        }
      } catch (err) {
        console.error("File upload failed:", err?.response?.data || err);
        setToast({ message: t("CS_FILE_UPLOAD_ERROR"), error: true });
      }
    },
    [routeConfig.uploadModule, tenantId, t, handleChange]
  );

  const goNext = useCallback(() => {
    if (!isSearchMode) {
      const fieldErrors = validateFields(routeConfig.form, formData);
      const { errors: crossErrors, failures } = validateCrossField(routeConfig.crossFieldValidations, formData);
      const allErrors = { ...fieldErrors, ...crossErrors };

      if (Object.keys(allErrors).length > 0) {
        setErrors(allErrors);
        setCrossFieldMessages(failures.map((f) => f.message));
        scrollToFirstError();
        return;
      }
      setCrossFieldMessages([]);
    }

    const formVal = buildPayload(formData);

    if (isSearchMode) {
      const filters = mapFormToSearchFilters(formVal, routeConfig.form);
      onSubmit?.({ payload: { ...filters, offset: 0 }, isSearch: true });
      return;
    }

    const payload = { [payloadKey]: [formVal], tenantId };

    if (isEditMode) {
      if (!updateMutation) {
        console.error("updateMutation is required in edit mode");
        return;
      }
      setIsSubmitting(true);
      const updatePayload = {
        [payloadKey]: { ...formVal, id: editData.id, tenantId, ...routeConfig.editPayloadExtras?.(editData) },
      };
      updateMutation.mutate(updatePayload, {
        onSuccess: (data) => {
          setIsSubmitting(false);
          onSubmit && onSubmit({ payload, response: data, isEditMode: true });
        },
        onError: (error) => {
          setIsSubmitting(false);
          setToast({ message: t("EST_UPDATE_FAILED"), error: true });
          console.error("Update failed:", error);
          onSubmit && onSubmit({ payload, error, isEditMode: true });
        },
      });
    } else {
      onSelect && onSelect(config?.key, { [payloadKey]: [formVal] }, false);
      onSubmit && onSubmit({ payload, isEditMode: false });
    }
  }, [
    isSearchMode,
    routeConfig,
    formData,
    isEditMode,
    updateMutation,
    editData,
    tenantId,
    onSelect,
    onSubmit,
    config,
    payloadKey,
    t,
  ]);

  const handleCancel = useCallback(() => {
    const baselineSource =
      resetBaseline && Object.keys(resetBaseline).length > 0 ? resetBaseline : rawAsset;
    const resetData = buildInitialData(routeConfig.form, baselineSource, dropdownData, tenantId);
    const allComputeDeps = flatFields.flatMap((fc) => fc.field?.computeFrom || []);
    const next = allComputeDeps.length
      ? applyComputedFields({ ...resetData }, allComputeDeps)
      : { ...resetData };
    setFormData(next);
    setErrors({});
    setCrossFieldMessages([]);
    onCancel?.();
  }, [
    routeConfig.form,
    resetBaseline,
    rawAsset,
    dropdownData,
    tenantId,
    flatFields,
    applyComputedFields,
    onCancel,
  ]);

  const handleSaveDraft = useCallback(() => {
    if (!onSaveDraft) return;
    onSaveDraft(buildPayload(formData));
    setToast({ message: t(draftSuccessLabel), error: false });
  }, [onSaveDraft, formData, draftSuccessLabel, t]);

  const buttonLabel = isEditMode
    ? routeConfig.actionButton?.text?.edit || "UPDATE"
    : routeConfig.actionButton?.text?.create || (isSearchMode ? "ES_COMMON_SEARCH" : "SAVE & NEXT");

  const clearLabel = cancelLabel || routeConfig.actionButton?.text?.clear || "ES_COMMON_CLEAR_ALL";

  if (isLoading) return <Loader />;

  return (
    <div className={styles["dynamic-form-container"]}>
      {sortedFields.map((fieldConfig) => (
        <DynamicFormField
          key={fieldConfig.key}
          fieldConfig={fieldConfig}
          formData={formData}
          onChange={handleChange}
          errors={errors}
          dropdownData={dropdownData}
          t={t}
          isDisabled={isDisabled}
          onFileUpload={handleFileUpload}
        />
      ))}

      {crossFieldMessages.map((msg, i) => (
        <p key={i} className={styles["dynamic-form-error"]}>{t(msg)}</p>
      ))}

      {!isDisabled && isSearchMode && (
        <div className={styles["dynamic-form-search-actions"]}>
          <SubmitBar
            label={t(buttonLabel)}
            onSubmit={goNext}
            className={styles["dynamic-form-search-submit"]}
          />
          <p className={styles["dynamic-form-search-clear"]} onClick={handleCancel}>
            {t(clearLabel)}
          </p>
        </div>
      )}

      {!isDisabled && !isSearchMode && (
        <ActionBar className={styles["dynamic-form-action"]}>
          {showCancel && (
            <ButtonSelector
              theme="border"
              label={t(cancelLabel)}
              onSubmit={handleCancel}
              className={styles["dynamic-form-margin-right"]}
            />
          )}
          {showDraftButton && (
            <ButtonSelector
              theme="border"
              label={t(draftLabel)}
              onSubmit={handleSaveDraft}
              className={styles["dynamic-form-margin-right"]}
            />
          )}
          <SubmitBar
            label={t(buttonLabel)}
            onSubmit={goNext}
            disabled={isSubmitting}
            className={showCancel || showDraftButton ? styles["dynamic-form-submit-flex"] : undefined}
          />
        </ActionBar>
      )}

      {toast && (
        <Toast label={toast.message} error={toast.error} onClose={() => setToast(null)} />
      )}
    </div>
  );
};

export default DynamicForm;
