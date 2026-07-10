import React, { useState, useCallback, useMemo, useEffect, useRef } from "react";
import { SubmitBar, Toast, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import ActionBar from "../atoms/ActionBar";
import ButtonSelector from "../atoms/ButtonSelector";
import DynamicFormField from "./DynamicFormField";
import { validateFields, validateCrossField, calculateDuration } from "../utilities/validators";
import { sortByOrder, buildPayload, scrollToFirstError, buildInitialData, flattenFormConfig } from "../utilities/formUtils";
import useDynamicMDMS from "../utilities/useDynamicMDMS";
import styles from "../styles/dynamicForm.module.scss";

// ── Computed-field registry ────────────────────────────────────────────
// A field can declare: field: { computeFrom: ["startDate","endDate"], computeFn: "calculateDuration" }
// Whenever any of computeFrom changes, DynamicForm looks up computeFn here and
// re-derives that field's value automatically. Add more pure functions here
// as new modules need derived fields — no DynamicForm changes required.
const COMPUTE_REGISTRY = {
  calculateDuration,
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
  onPersistDraft,
}) => {
  const stateId = Digit.ULBService.getStateId();
  const payloadKey = routeConfig.payloadKey || "Assets";

  // ── Dropdown data: single generic source for ANY module's config ─────
  const { dropdownData, isLoading } = useDynamicMDMS(routeConfig.form, stateId, tenantId, t);

  // ── Raw pre-fill source ───────────────────────────────────────────────
  // Merge, don't pick: persisted (user-entered) values win, editData (asset
  // info from router state) sits underneath so read-only prefill fields still
  // populate on first visit AND when coming back via the edit pencil.
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

  // Re-derive any field whose computeFrom list includes a field that just changed.
  const applyComputedFields = useCallback(
    (updated, changedFieldNames) => {
      let next = updated;
      flatFields.forEach((fc) => {
        const { computeFrom, computeFn, name } = fc.field || {};
        if (!computeFrom || !computeFn) return;
        if (!computeFrom.some((dep) => changedFieldNames.includes(dep))) return;
        const fn = COMPUTE_REGISTRY[computeFn];
        if (!fn) return;
        next = { ...next, [name]: fn(...computeFrom.map((dep) => next[dep])) };
      });
      return next;
    },
    [flatFields]
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
        let updated = { ...prev, [fieldName]: value };
        resetFields.forEach((f) => { updated[f] = null; });
        return applyComputedFields(updated, [fieldName, ...resetFields]);
      });
      setErrors((prev) => {
        const updated = { ...prev, [fieldName]: false };
        resetFields.forEach((f) => { updated[f] = false; });
        return updated;
      });
    },
    [applyComputedFields]
  );

  // Optional: auto-save partial progress to session while the user is still on the form.
  useEffect(() => {
    if (!onPersistDraft || isLoading || !hasSyncedRef.current) return;

    const timer = setTimeout(() => {
      onPersistDraft(buildPayload(formData));
    }, 400);

    return () => clearTimeout(timer);
  }, [formData, onPersistDraft, isLoading]);

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
      } catch {
        setToast({ message: t("CS_FILE_UPLOAD_ERROR"), error: true });
      }
    },
    [routeConfig.uploadModule, tenantId, t, handleChange]
  );

  const goNext = useCallback(() => {
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
    const formVal = buildPayload(formData);
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
          // Surface the failure — previously only console.error'd, so the
          // user saw nothing when an update silently failed.
          setToast({ message: t("EST_UPDATE_FAILED"), error: true });
          console.error("Update failed:", error);
          onSubmit && onSubmit({ payload, error, isEditMode: true });
        },
      });
    } else {
      onSelect && onSelect(config?.key, { [payloadKey]: [formVal] }, false);
      onSubmit && onSubmit({ payload, isEditMode: false });
    }
  }, [routeConfig, formData, isEditMode, updateMutation, editData, tenantId, onSelect, onSubmit, config, payloadKey, t]);

  const handleCancel = useCallback(() => {
    const resetData = buildInitialData(routeConfig.form, rawAsset, dropdownData, tenantId);
    const allComputeDeps = flatFields.flatMap((fc) => fc.field?.computeFrom || []);
    const next = allComputeDeps.length
      ? applyComputedFields({ ...resetData }, allComputeDeps)
      : { ...resetData };
    setFormData(next);
    setErrors({});
    setCrossFieldMessages([]);
    onCancel?.();
  }, [routeConfig.form, rawAsset, dropdownData, tenantId, flatFields, applyComputedFields, onCancel]);

  const buttonLabel = isEditMode
    ? routeConfig.actionButton?.text?.edit || "UPDATE"
    : routeConfig.actionButton?.text?.create || "SAVE & NEXT";

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

      {!isDisabled && (
        <ActionBar className={styles["dynamic-form-action"]}>
          {showCancel && (
            <ButtonSelector
              theme="border"
              label={t(cancelLabel)}
              onSubmit={handleCancel}
              style={{ marginRight: "16px" }}
            />
          )}
          <SubmitBar
            label={t(buttonLabel)}
            onSubmit={goNext}
            disabled={isSubmitting}
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
