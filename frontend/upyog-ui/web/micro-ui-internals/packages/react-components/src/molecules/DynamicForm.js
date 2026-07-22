/**
 * DynamicForm — config-driven form engine used by MDMS wizard steps and search pages.
 * Renders fields from routeConfig.form, runs validators/computeFns, and builds payloads.
 */
import React, { useState, useCallback, useMemo, useEffect, useRef } from "react";
import { SubmitBar, Toast, Loader } from "@nudmcdgnpm/digit-ui-react-components";
import ActionBar from "../atoms/ActionBar";
import ButtonSelector from "../atoms/ButtonSelector";
import DynamicFormField from "./DynamicFormField";
import { validateFields, validateCrossField, calculateDuration, calculateRentByBillingCycle } from "../utilities/validators";
import { sortByOrder, buildPayload, scrollToFirstError, buildInitialData, flattenFormConfig, findFieldConfig, enrichDropdownSelection, optionCode } from "../utilities/formUtils";
import useDynamicMDMS from "../utilities/useDynamicMDMS";
import { mapFormToSearchFilters } from "../utilities/searchUtils";
import { SearchField, SearchForm } from "./SearchForm";
import styles from "../styles/dynamicForm.module.scss";

/** RHF-compatible wrapper so SearchForm can submit without react-hook-form. */
const searchFormHandleSubmit = (onValid) => (e) => {
  if (e?.preventDefault) e.preventDefault();
  onValid();
};

// ── Computed-field registry ────────────────────────────────────────────
// A field can declare: field: { computeFrom: ["startDate","endDate"], computeFn: "calculateDuration" }
// Whenever any of computeFrom changes, DynamicForm looks up computeFn here and
// re-derives that field's value automatically. Add more pure functions here
// as new modules need derived fields — no DynamicForm changes required.
const COMPUTE_REGISTRY = {
  calculateDuration,
  calculateRentByBillingCycle,
  /** Copy a single upstream field value (e.g. advancePayment ← monthlyRent). */
  copyValue: (value) =>
    value === undefined || value === null || value === "" ? "" : value,
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
  showCancel = true,
  cancelLabel = "CS_COMMON_CANCEL",
  onCancel,
  resetBaseline,
  showDraftButton = false,
  draftLabel = "EST_SAVE_AS_DRAFT",
  draftSuccessLabel = "EST_DRAFT_SAVED",
  onSaveDraft,
  onPersistDraft,
  /** Optional async search for fields with field.searchButton — returns { prefill } | { error } */
  onFieldSearch,
  /** "wizard" (default) | "search" — search reuses the same fields/MDMS without wizard ActionBar */
  mode = "wizard",
  /** search only: "inline" (row, default) | "stack" — or set routeConfig.searchLayout */
  searchLayout,
}) => {
  const isSearchMode = mode === "search";
  const resolvedSearchLayout =
    searchLayout || routeConfig?.searchLayout || (isSearchMode ? "inline" : "stack");
  const isInlineSearch = isSearchMode && resolvedSearchLayout === "inline";
  const stateId = Digit.ULBService.getStateId();
  const payloadKey = routeConfig.payloadKey || "Assets";

  // Localities are always fetched for the selected city tenant (e.g. pg.citya).
  // Starts as current tenant; updates when form city changes.
  const [cityForLocality, setCityForLocality] = useState(() => String(tenantId || "").trim());

  // ── Dropdown data: localities come from the SELECTED city tenant only ─
  const { dropdownData, isLoading } = useDynamicMDMS(routeConfig.form, stateId, tenantId, t, {
    city: cityForLocality || tenantId,
  });

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
  const [isFieldSearching, setIsFieldSearching] = useState(false);
  // Asset lookup panel: null | { fieldName, status: "found"|"notFound", estateNo, prefill? }
  const [searchPanel, setSearchPanel] = useState(null);
  // After picking a suggestion, keep the list hidden until the user types again.
  const suppressSuggestRef = useRef(false);

  // Keep locality dropdown bound to the city currently on the form.
  // Preserve original tenant casing (MDMS uses pg.citya, not PG.CITYA).
  useEffect(() => {
    const city = formData?.city;
    const fromForm =
      (city && typeof city === "object" && city.code != null
        ? String(city.code).trim()
        : "") || (typeof city === "string" ? city.trim() : "");
    const next = String(fromForm || tenantId || "").trim();
    if (next && next !== cityForLocality) {
      setCityForLocality(next);
    }
  }, [formData?.city, tenantId, cityForLocality]);

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
      if (
        !merged.showRegistrationDetails &&
        (isEditMode ||
          merged.buildingName ||
          String(merged.assetRegistrationType || "").toUpperCase() === "NEW_BUILDING")
      ) {
        merged.showRegistrationDetails = "YES";
      }
      const allComputeDeps = flatFields.flatMap((fc) => fc.field?.computeFrom || []);
      return allComputeDeps.length ? applyComputedFields(merged, allComputeDeps) : merged;
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isLoading]);

  const applyPrefill = useCallback(
    (prefill = {}, preserve = {}) => {
      setFormData((prev) => {
        const built = buildInitialData(
          routeConfig.form,
          { ...prev, ...prefill },
          dropdownData,
          tenantId
        );
        const merged = { ...built, ...preserve };
        const allComputeDeps = flatFields.flatMap((fc) => fc.field?.computeFrom || []);
        return allComputeDeps.length
          ? applyComputedFields(merged, allComputeDeps)
          : merged;
      });
    },
    [routeConfig.form, dropdownData, tenantId, flatFields, applyComputedFields]
  );

  const handleChange = useCallback(
    (fieldName, value, resetFields = []) => {
      if (fieldName === "assetRegistrationType") {
        suppressSuggestRef.current = false;
        setSearchPanel(null);
        setErrors({});
        setCrossFieldMessages([]);

        if (value === "NEW_BUILDING") {
          const blank = buildInitialData(routeConfig.form, {}, dropdownData, tenantId);
          const allComputeDeps = flatFields.flatMap((fc) => fc.field?.computeFrom || []);
          const cleared = {
            ...blank,
            assetRegistrationType: "NEW_BUILDING",
            searchEstateNo: "",
            showRegistrationDetails: "YES",
          };
          setFormData(
            allComputeDeps.length
              ? applyComputedFields(cleared, allComputeDeps)
              : cleared
          );
          return;
        }

        if (value === "EXISTING_ASSET") {
          setFormData((prev) => {
            const blank = buildInitialData(routeConfig.form, {}, dropdownData, tenantId);
            const allComputeDeps = flatFields.flatMap((fc) => fc.field?.computeFrom || []);
            const cleared = {
              ...blank,
              assetRegistrationType: "EXISTING_ASSET",
              searchEstateNo: "",
              showRegistrationDetails: "",
            };
            return allComputeDeps.length
              ? applyComputedFields(cleared, allComputeDeps)
              : cleared;
          });
          return;
        }
      }

      setFormData((prev) => {
        let resolvedValue = value;
        if (fieldName === "billingCycle") {
          resolvedValue = enrichDropdownSelection(value, billingCycleOptions);
        }
        let updated = { ...prev, [fieldName]: resolvedValue };
        resetFields.forEach((f) => {
          updated[f] = null;
        });

        // City change → clear locality; localities will reload for the new city.
        if (fieldName === "city") {
          const nextCity =
            optionCode(resolvedValue) ||
            (typeof resolvedValue === "string" ? resolvedValue : "");
          if (nextCity) setCityForLocality(String(nextCity).trim());
          updated.serviceType = null;
          updated.serviceTypeName = "";
        }

        return applyComputedFields(updated, [fieldName, ...resetFields]);
      });
      if (fieldName === "searchEstateNo") {
        suppressSuggestRef.current = false;
      }
      setErrors((prev) => {
        const updated = { ...prev, [fieldName]: false };
        resetFields.forEach((f) => {
          updated[f] = false;
        });
        if (fieldName === "city") updated.serviceType = false;
        return updated;
      });
    },
    [
      applyComputedFields,
      billingCycleOptions,
      routeConfig.form,
      dropdownData,
      tenantId,
      flatFields,
    ]
  );

  const handleFieldSearch = useCallback(
    async (fieldName, queryOverride) => {
      if (!onFieldSearch) return;
      const query = String(
        queryOverride !== undefined ? queryOverride : formData[fieldName] || ""
      ).trim();
      if (!query) {
        setSearchPanel(null);
        if (queryOverride === undefined) {
          setErrors((prev) => ({ ...prev, [fieldName]: true }));
        }
        return;
      }

      setIsFieldSearching(true);
      try {
        const result = await onFieldSearch(fieldName, {
          ...formData,
          [fieldName]: query,
        });
        if (result?.error && !result?.notFound) {
          setToast({ message: t(result.error), error: true });
          return;
        }
        if (result?.notFound || result?.error === "EST_ASSET_NOT_FOUND") {
          setSearchPanel({
            fieldName,
            status: "notFound",
            estateNo: query,
          });
          return;
        }
        if (Array.isArray(result?.matches) && result.matches.length > 0) {
          setSearchPanel({
            fieldName,
            status: "matches",
            estateNo: query,
            matches: result.matches,
          });
          return;
        }
        if (result?.found || result?.prefill) {
          setSearchPanel({
            fieldName,
            status: "found",
            estateNo: result.estateNo || query,
            prefill: result.prefill || {},
          });
          return;
        }
        setSearchPanel({
          fieldName,
          status: "notFound",
          estateNo: query,
        });
      } catch (err) {
        console.error("Field search failed:", err);
        setToast({ message: t("CS_SOMETHING_WENT_WRONG"), error: true });
      } finally {
        setIsFieldSearching(false);
      }
    },
    [onFieldSearch, formData, t]
  );

  // Optional live typeahead — off by default; estate asset search uses exact match on Enter/button.
  useEffect(() => {
    if (String(formData.assetRegistrationType || "").toUpperCase() !== "EXISTING_ASSET") {
      return undefined;
    }
    const searchField = flatFields.find((fc) => fc?.field?.name === "searchEstateNo");
    if (searchField?.field?.searchTypeahead !== true) {
      return undefined;
    }
    if (suppressSuggestRef.current) {
      return undefined;
    }
    const query = String(formData.searchEstateNo || "").trim();
    if (query.length < 3) {
      setSearchPanel((prev) =>
        prev?.fieldName === "searchEstateNo" ? null : prev
      );
      return undefined;
    }
    const timer = setTimeout(() => {
      if (suppressSuggestRef.current) return;
      handleFieldSearch("searchEstateNo", query);
    }, 250);
    return () => clearTimeout(timer);
  }, [
    formData.searchEstateNo,
    formData.assetRegistrationType,
    handleFieldSearch,
    flatFields,
  ]);

  const handleSelectSearchResult = useCallback(
    (fieldName, match) => {
      const selected =
        match ||
        (searchPanel?.fieldName === fieldName && searchPanel.status === "found"
          ? {
              estateNo: searchPanel.estateNo,
              prefill: searchPanel.prefill || {},
            }
          : null);
      if (!selected) return;

      suppressSuggestRef.current = true;
      setSearchPanel(null);
      applyPrefill(selected.prefill || {}, {
        assetRegistrationType: "EXISTING_ASSET",
        searchEstateNo: selected.estateNo || formData[fieldName],
        showRegistrationDetails: "YES",
      });
      setToast({ message: t("EST_ASSET_FOUND"), error: false });
    },
    [searchPanel, applyPrefill, formData, t]
  );

  const handleCreateNewFromSearch = useCallback(() => {
    const blank = buildInitialData(routeConfig.form, {}, dropdownData, tenantId);
    const allComputeDeps = flatFields.flatMap((fc) => fc.field?.computeFrom || []);
    const cleared = {
      ...blank,
      assetRegistrationType: "NEW_BUILDING",
      searchEstateNo: "",
      showRegistrationDetails: "YES",
    };
    setFormData(
      allComputeDeps.length
        ? applyComputedFields(cleared, allComputeDeps)
        : cleared
    );
    setErrors({});
    setCrossFieldMessages([]);
    setSearchPanel(null);
  }, [
    routeConfig.form,
    dropdownData,
    tenantId,
    flatFields,
    applyComputedFields,
  ]);

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
    setSearchPanel(null);
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

  // Only block the first paint. Remounting the form on later MDMS/locality
  // refetches wipes in-progress dropdown selections (search filters, etc.).
  if (isLoading && !hasSyncedRef.current) return <Loader />;

  // Local NewRegistration gates fields/actions behind Existing Asset / New Building.
  // MDMS Estate.Config has no such gate — always show the ActionBar in that case.
  const usesRegistrationGate = flatFields.some(
    (fc) =>
      fc?.field?.name === "assetRegistrationType" ||
      fc?.visibleWhen?.field === "showRegistrationDetails" ||
      fc?.visibleWhen?.field === "assetRegistrationType"
  );
  const showActionBar =
    !usesRegistrationGate ||
    String(formData.showRegistrationDetails || "").toUpperCase() === "YES" ||
    String(formData.assetRegistrationType || "").toUpperCase() === "NEW_BUILDING";

  const fieldNodes = sortedFields.map((fieldConfig) => (
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
      onFieldSearch={handleFieldSearch}
      isFieldSearching={isFieldSearching}
      searchPanel={searchPanel}
      onSelectSearchResult={handleSelectSearchResult}
      onCreateNewFromSearch={handleCreateNewFromSearch}
    />
  ));

  const searchActions = !isDisabled && isSearchMode && (
    <SearchField className="submit">
      <SubmitBar label={t(buttonLabel)} submit />
      <p className={styles["dynamic-form-search-clear"]} onClick={handleCancel}>
        {t(clearLabel)}
      </p>
    </SearchField>
  );

  const stackedSearchActions = !isDisabled && isSearchMode && !isInlineSearch && (
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
  );

  if (isInlineSearch) {
    return (
      <>
        <SearchForm
          onSubmit={goNext}
          handleSubmit={searchFormHandleSubmit}
          className={routeConfig.searchFormClassName || ""}
        >
          {fieldNodes.map((node, i) => (
            <SearchField key={sortedFields[i].key}>{node}</SearchField>
          ))}
          {searchActions}
        </SearchForm>

        {crossFieldMessages.map((msg, i) => (
          <p key={i} className={styles["dynamic-form-error"]}>{t(msg)}</p>
        ))}

        {toast && (
          <Toast label={toast.message} error={toast.error} onClose={() => setToast(null)} />
        )}
      </>
    );
  }

  return (
    <div className={styles["dynamic-form-container"]}>
      {fieldNodes}

      {crossFieldMessages.map((msg, i) => (
        <p key={i} className={styles["dynamic-form-error"]}>{t(msg)}</p>
      ))}

      {stackedSearchActions}

      {!isDisabled && !isSearchMode && showActionBar && (
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
