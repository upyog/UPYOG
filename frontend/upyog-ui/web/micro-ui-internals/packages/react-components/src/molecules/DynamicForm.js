import React, { useState, useCallback, useMemo, useEffect, useRef } from "react";
import { SubmitBar, Toast } from "@nudmcdgnpm/digit-ui-react-components";
import DynamicFormField from "./DynamicFormField";
import { validateFields, validateCrossField, calculateDuration } from "../utilities/validators";
import { sortByOrder, buildPayload, scrollToFirstError, buildInitialData } from "../utilities/formUtils";
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

// Flattens group children into a single list alongside top-level fields,
// since computed fields (and their computeFrom dependencies) can live at either level.
const flattenFields = (formConfig = []) =>
  formConfig.reduce((acc, fieldConfig) => {
    if (fieldConfig.type === "group") {
      return [...acc, ...(fieldConfig.children || [])];
    }
    return [...acc, fieldConfig];
  }, []);

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
}) => {
  const stateId = Digit.ULBService.getStateId();
  const payloadKey = routeConfig.payloadKey || "Assets";

  // // ── Dropdown data: single generic source for ANY module's config ─────
  const { dropdownData, isLoading } = useDynamicMDMS(routeConfig.form, stateId, tenantId, t);




  // ── Raw pre-fill source ───────────────────────────────────────────────
  // Merge, don't pick: persisted (user-entered) values win, editData (asset
  // info from router state) sits underneath so read-only prefill fields still
  // populate on first visit AND when coming back via the edit pencil.
  const rawAsset = useMemo(() => {
    const saved = persistedData?.[config?.key]?.[payloadKey];
    const persisted = (Array.isArray(saved) ? saved[0] : saved) || {};
    const hasPersisted = Object.keys(persisted).length > 0;

    if (hasPersisted) return { ...editData, ...persisted };
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
  const [showToast, setShowToast] = useState(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastError, setToastError] = useState(false);

  const flatFields = useMemo(() => flattenFields(routeConfig.form || []), [routeConfig.form]);

  // Re-derive any field whose computeFrom list includes a field that just changed.
  const applyComputedFields = useCallback(
    (updated, changedFieldNames) => {
      let next = updated;
      flatFields.forEach((fc) => {
        const computeFrom = fc.field?.computeFrom;
        const computeFn = fc.field?.computeFn;
        if (!computeFrom || !computeFn) return;
        if (!computeFrom.some((dep) => changedFieldNames.includes(dep))) return;

        const fn = COMPUTE_REGISTRY[computeFn];
        if (!fn) return;

        const args = computeFrom.map((dep) => next[dep]);
        next = { ...next, [fc.field.name]: fn(...args) };
      });
      return next;
    },
    [flatFields]
  );

  // Sync formData with initialData EXACTLY ONCE, right after dropdownData
  // has finished loading. Guarded with a ref so it can never fire again,
  // regardless of how many times initialData's identity changes afterward.
  // Also runs computed fields once so derived values (e.g. duration) show
  // immediately on prefill/edit, not only after the user touches a date.
  const hasSyncedRef = useRef(false);

  useEffect(() => {
    if (hasSyncedRef.current) return;
    if (isLoading) return;
    hasSyncedRef.current = true;
    setFormData((prev) => {
      const merged = { ...initialData, ...prev };
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
        updated = applyComputedFields(updated, [fieldName, ...resetFields]);
        return updated;
      });
      setErrors((prev) => {
        const updated = { ...prev, [fieldName]: false };
        resetFields.forEach((f) => { updated[f] = false; });
        return updated;
      });
    },
    [applyComputedFields]
  );

  // ── File upload: owns tenantId + filestore call + error toast ────────
  // module code defaults to "ESTATE" but can be overridden per-route via routeConfig.uploadModule
  const handleFileUpload = useCallback(
    async (fieldName, file) => {
      if (!file) return;
      if (file.size >= 5242880) {
        setToastError(true);
        setToastMessage(t("CS_MAXIMUM_UPLOAD_SIZE_EXCEEDED"));
        setShowToast(true);
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
        setToastError(true);
        setToastMessage(t("CS_FILE_UPLOAD_ERROR"));
        setShowToast(true);
      }
    },
    [routeConfig.uploadModule, tenantId, t, handleChange]
  );

  const sortedFields = useMemo(() => sortByOrder(routeConfig.form || []), [routeConfig.form]);

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
          console.error("Update failed:", error);
          onSubmit && onSubmit({ payload, error, isEditMode: true });
        },
      });
    } else {
      onSelect && onSelect(config?.key, { [payloadKey]: [formVal] }, false);
      onSubmit && onSubmit({ payload, isEditMode: false });
    }
  }, [routeConfig, formData, isEditMode, updateMutation, editData, tenantId, onSelect, onSubmit, config, payloadKey]);

  const buttonLabel = isEditMode
    ? routeConfig.actionButton?.text?.edit || "UPDATE"
    : routeConfig.actionButton?.text?.create || "SAVE & NEXT";

  if (isLoading) return <p>Loading...</p>;

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
        <div className={styles["dynamic-form-action"]}>
          <SubmitBar label={t(buttonLabel)} onSubmit={goNext} disabled={isSubmitting} variant={routeConfig.actionButton?.variant || "contained"} />
        </div>
      )}

      {showToast && (
        <Toast label={toastMessage} error={toastError} onClose={() => setShowToast(false)} />
      )}
    </div>
  );
};

export default DynamicForm;
