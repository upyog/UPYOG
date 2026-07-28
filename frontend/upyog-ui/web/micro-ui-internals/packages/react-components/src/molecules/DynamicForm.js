/**
 * DynamicForm.js
 *
 * Config-driven form engine for UPYOG MDMS wizard steps and search pages.
 * Typically wrapped by DynamicFormStep for wizard routes; can also be used
 * directly with mode="search" for filter UIs that reuse the same MDMS field
 * definitions.
 *
 * Responsibilities
 * ----------------
 * 1. Load dropdown / MDMS options via useDynamicMDMS (localities follow the
 *    selected city tenant; other masters use stateId / tenantId).
 * 2. Hydrate form state from editData + persisted wizard data via
 *    buildInitialData; sync once after dropdowns finish loading (preserves
 *    non-empty user input).
 * 3. Render fields from routeConfig.form (sorted by order) through
 *    DynamicFormField — visibility, uploads, and field-level search panels
 *    included.
 * 4. Re-derive computed fields from COMPUTE_REGISTRY whenever a dependency
 *    in field.computeFrom changes (e.g. calculateDuration, copyValue).
 * 5. Validate per-field and cross-field rules on submit; scroll to first error.
 * 6. Build payloads with buildPayload:
 *    - wizard create → onSelect(config.key, { [payloadKey]: [formVal] })
 *    - wizard edit   → updateMutation.mutate(...) then onSubmit
 *    - search mode   → mapFormToSearchFilters → onSubmit({ payload, isSearch })
 * 7. Optional draft: explicit Save Draft button (onSaveDraft) and/or debounced
 *    auto-persist (onPersistDraft when showDraftButton is false).
 * 8. Optional field search (onFieldSearch) for estate / asset lookup panels,
 *    with optional typeahead when field.searchTypeahead === true.
 *
 * Computed fields (routeConfig.form)
 * ----------------------------------
 * Declare on a field:
 *   field: { computeFrom: ["startDate","endDate"], computeFn: "calculateDuration" }
 * When any computeFrom dependency changes, DynamicForm looks up computeFn in
 * COMPUTE_REGISTRY and updates the field. Add new pure functions to the
 * registry — no DynamicForm control-flow changes required.
 *
 * Modes
 * -----
 * - "wizard" (default): ActionBar with Cancel / Draft / Save & Next (or Update).
 * - "search": SearchForm layout (inline row or stacked); no wizard ActionBar;
 *   submit maps values to search filters.
 *
 * Typical usage
 * -------------
 *   // Wizard (usually via DynamicFormStep)
 *   <DynamicForm
 *     routeConfig={mergedRouteConfig}
 *     config={{ key: stepKey }}
 *     onSelect={onSelect}
 *     onSubmit={handleSubmit}
 *     persistedData={sessionData}
 *     isEditMode={isEditMode}
 *     editData={editData}
 *     tenantId={tenantId}
 *     t={t}
 *     showDraftButton
 *     onSaveDraft={saveDraft}
 *     onFieldSearch={handleFieldSearch}
 *   />
 *
 *   // Search filters
 *   <DynamicForm
 *     mode="search"
 *     searchLayout="inline"
 *     routeConfig={searchRouteConfig}
 *     onSubmit={runSearch}
 *     tenantId={tenantId}
 *     t={t}
 *   />
 *
 * Props
 * -----
 * @param {object}   routeConfig           Merged MDMS route/step config (form, payloadKey,
 *                                         crossFieldValidations, actionButton, uploadModule,
 *                                         searchLayout, editPayloadExtras, etc.).
 * @param {Function} [onSubmit]            Called after successful goNext with
 *                                         { payload, response?, error?, isEditMode?, isSearch?, formValues? }.
 * @param {Function} [onSelect]            Wizard step select(key, data, skipStep) for create flow.
 * @param {object}   [config]              Step identity; config.key is passed to onSelect / draft.
 * @param {boolean}  [isEditMode=false]    Uses updateMutation instead of onSelect.
 * @param {boolean}  [isDisabled=false]    Disables field interaction and action buttons.
 * @param {object}   [updateMutation]      Required in edit mode; mutate(payload, { onSuccess, onError }).
 * @param {object}   [editData]            Existing record values for edit / prefill.
 * @param {string}   [tenantId]            Tenant for MDMS, uploads, and payloads.
 * @param {object}   [persistedData]       Wizard session data keyed by config.key / payloadKey.
 * @param {Function} [t]                   i18n translator; defaults to identity.
 * @param {boolean}  [showCancel=true]     Show Cancel / Clear in wizard ActionBar.
 * @param {string}   [cancelLabel]         i18n key for cancel (default CS_COMMON_CANCEL).
 * @param {Function} [onCancel]            Extra callback after form reset on cancel.
 * @param {object}   [resetBaseline]       Preferred reset source on cancel; falls back to rawAsset.
 * @param {boolean}  [showDraftButton]     Show explicit Save Draft button.
 * @param {string}   [draftLabel]          Draft button i18n key.
 * @param {string}   [draftSuccessLabel]   Toast after draft save.
 * @param {Function} [onSaveDraft]         (flatPayload) => void for explicit draft button.
 * @param {Function} [onPersistDraft]      Debounced auto-save when no draft button is shown.
 * @param {Function} [onFieldSearch]       async (fieldName, formSnapshot) =>
 *                                         { prefill } | { matches } | { found } | { notFound } | { error }.
 * @param {string}   [mode="wizard"]       "wizard" | "search".
 * @param {string}   [searchLayout]        "inline" | "stack"; overrides routeConfig.searchLayout.
 *
 * @see DynamicFormStep
 * @see DynamicFormField
 * @see useDynamicMDMS
 * @see buildInitialData
 * @see buildPayload
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

/**
 * Adapts SearchForm's react-hook-form-style handleSubmit API.
 * Returns an event handler that prevents default and invokes `onValid`
 * (typically goNext) so SearchForm can submit without RHF installed.
 *
 * @param {Function} onValid - Callback to run on successful form submit.
 * @returns {(e?: Event) => void}
 */
const searchFormHandleSubmit = (onValid) => (e) => {
  if (e?.preventDefault) e.preventDefault();
  onValid();
};

/**
 * Named compute functions referenced by field.computeFn in MDMS / routeConfig.
 * Declare on a field: { computeFrom: ["startDate","endDate"], computeFn: "calculateDuration" }.
 * Add new pure functions here as modules need derived fields — no DynamicForm
 * control-flow changes required.
 */
const COMPUTE_REGISTRY = {
  calculateDuration,
  calculateRentByBillingCycle,
  /**
   * Copies a single upstream field value (e.g. advancePayment ← monthlyRent).
   * Empty/null/undefined inputs become "".
   *
   * @param {*} value - Upstream field value.
   * @returns {*|string}
   */
  copyValue: (value) =>
    value === undefined || value === null || value === "" ? "" : value,
};

/**
 * Config-driven form engine for wizard steps and search filter pages.
 * Loads MDMS options, hydrates/validates form state, runs computed fields,
 * and builds create / edit / search payloads.
 *
 * @param {object} props — see file-level Props section above.
 * @returns {JSX.Element}
 */
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
  draftLabel = "CS_COMMON_SAVE_DRAFT",
  draftSuccessLabel = "CS_COMMON_SAVED",
  onSaveDraft,
  onPersistDraft,
  onFieldSearch,
  mode = "wizard",
  searchLayout,
}) => {
  /** True when mode === "search" (filter UI, no wizard ActionBar). */
  const isSearchMode = mode === "search";
  /** Effective search layout: prop → routeConfig.searchLayout → inline (search) / stack. */
  const resolvedSearchLayout =
    searchLayout || routeConfig?.searchLayout || (isSearchMode ? "inline" : "stack");
  /** Inline search renders fields in a SearchForm row with submit-as-button. */
  const isInlineSearch = isSearchMode && resolvedSearchLayout === "inline";
  const stateId = Digit.ULBService.getStateId();
  /** Wizard / API array key under which form values are stored (default "Assets"). */
  const payloadKey = routeConfig.payloadKey || "Assets";

  /**
   * City tenant used to fetch localities (e.g. pg.citya).
   * Starts as current tenant; updates when the form city field changes.
   */
  const [cityForLocality, setCityForLocality] = useState(() => String(tenantId || "").trim());

  /**
   * MDMS / master dropdown options for all fields in routeConfig.form.
   * Localities are scoped to cityForLocality (selected city), not always tenantId.
   */
  const { dropdownData, isLoading } = useDynamicMDMS(routeConfig.form, stateId, tenantId, t, {
    city: cityForLocality || tenantId,
  });

  /**
   * Raw pre-fill source for buildInitialData / cancel reset.
   * Merges editData with the first persisted step item under
   * persistedData[config.key][payloadKey]; prefers persisted when present.
   *
   * @returns {object} Flat-ish record of known field values.
   */
  const rawAsset = useMemo(() => {
    const saved = persistedData?.[config?.key]?.[payloadKey];
    const persisted = (Array.isArray(saved) ? saved[0] : saved) || {};
    if (Object.keys(persisted).length > 0) return { ...editData, ...persisted };
    return editData && Object.keys(editData).length > 0 ? editData : {};
  }, [editData, persistedData, config, payloadKey]);

  /**
   * Hydrated form snapshot from config + rawAsset + live dropdownData.
   * Dropdown codes are resolved to option objects where possible.
   *
   * @returns {object} Initial formData shape keyed by field.name.
   */
  const initialData = useMemo(
    () => buildInitialData(routeConfig.form, rawAsset, dropdownData, tenantId),
    [routeConfig.form, rawAsset, dropdownData, tenantId]
  );

  const [formData, setFormData] = useState(initialData);
  const [errors, setErrors] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [crossFieldMessages, setCrossFieldMessages] = useState([]);
  /** @type {[{ message: string, error: boolean }] | null} */
  const [toast, setToast] = useState(null);
  const [isFieldSearching, setIsFieldSearching] = useState(false);
  /**
   * Asset / estate lookup panel state.
   * null | { fieldName, status: "found"|"notFound"|"matches", estateNo, prefill?, matches? }
   */
  const [searchPanel, setSearchPanel] = useState(null);
  /** After picking a suggestion, hide typeahead until the user types again. */
  const suppressSuggestRef = useRef(false);

  /**
   * Keeps locality MDMS bound to the city currently on the form.
   * Reads formData.city as option object (.code) or string; preserves original
   * tenant casing (MDMS uses pg.citya, not PG.CITYA).
   */
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

  /**
   * Flattened leaf field configs (groups expanded) — used for compute / watch / gates.
   * @returns {object[]}
   */
  const flatFields = useMemo(() => flattenFormConfig(routeConfig.form || []), [routeConfig.form]);

  /**
   * Top-level form entries sorted by order for render.
   * @returns {object[]}
   */
  const sortedFields = useMemo(() => sortByOrder(routeConfig.form || []), [routeConfig.form]);

  /**
   * Options for the billingCycle field (MDMS dropdown, key lookup, or static options).
   * Needed so computeFns receive enriched option objects with multipliers.
   *
   * @returns {object[]}
   */
  const billingCycleOptions = useMemo(() => {
    const billingField = findFieldConfig(routeConfig.form, "billingCycle");
    return (
      dropdownData?.billingCycle ||
      dropdownData?.[billingField?.key] ||
      billingField?.options ||
      []
    );
  }, [dropdownData, routeConfig.form]);

  /**
   * Maps a field's computeFrom dependency names to argument values for a computeFn.
   * Special-cases billingCycle so the arg is an enriched dropdown option.
   *
   * @param {string[]} computeFrom - Dependency field names.
   * @param {string}   computeFn   - Registry key (unused except for call sites).
   * @param {object}   data        - Current form data snapshot.
   * @returns {*[]} Positional args for COMPUTE_REGISTRY[computeFn].
   */
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

  /**
   * Re-derives any field whose computeFrom list intersects `changedFieldNames`.
   * Runs iterative rounds (max 10) so cascading computes (A→B→C) settle.
   * Looks up each field.computeFn in COMPUTE_REGISTRY; calculateRentByBillingCycle
   * also receives billingCycleOptions as a trailing argument.
   *
   * @param {object}   updated           Form data after the triggering change.
   * @param {string[]} changedFieldNames Field names that just changed (seeds the queue).
   * @returns {object} Form data with computed fields updated.
   */
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

  /**
   * Guards the one-time post-MDMS form sync so it never re-fires when
   * initialData's identity changes after the first load.
   */
  const hasSyncedRef = useRef(false);

  /**
   * Syncs formData with initialData EXACTLY ONCE after dropdownData finishes loading.
   * Fresh initialData wins over the pre-load snapshot (real options replace placeholders);
   * non-empty user-typed values are preserved on top. Also auto-sets
   * showRegistrationDetails when edit / building / NEW_BUILDING imply it, then
   * runs applyComputedFields for all compute dependencies.
   */
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

  /**
   * Merges a lookup/search prefill into form state via buildInitialData, then
   * overlays `preserve` keys (registration type, estate no, gates) and re-runs
   * computed fields.
   *
   * @param {object} [prefill]   Values from asset/estate search to apply.
   * @param {object} [preserve]  Keys that must win after buildInitialData.
   */
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

  /**
   * Primary field onChange from DynamicFormField.
   * Special cases:
   * - assetRegistrationType NEW_BUILDING / EXISTING_ASSET → wipe form to a blank
   *   baseline with the right registration gate flags.
   * - billingCycle → enrichDropdownSelection so multipliers stay on the value.
   * - city → update cityForLocality and clear serviceType.
   * Always clears errors for the changed (and reset) fields and runs applyComputedFields.
   *
   * @param {string}   fieldName     field.name that changed.
   * @param {*}        value         New value (string, option object, file meta, etc.).
   * @param {string[]} [resetFields] Sibling fields to null out (cascading clears).
   */
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

  /**
   * Runs the parent onFieldSearch for a lookup field (Enter / search button / typeahead).
   * Maps the result into searchPanel status: matches | found | notFound, or an error toast.
   * Empty query clears the panel; if the user triggered search (no queryOverride), marks the field invalid.
   *
   * @param {string}  fieldName       Field that owns the search (e.g. searchEstateNo).
   * @param {string}  [queryOverride] Optional query (typeahead); else formData[fieldName].
   * @returns {Promise<void>}
   */
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

  /**
   * Optional live typeahead for searchEstateNo when field.searchTypeahead === true
   * and assetRegistrationType is EXISTING_ASSET. Debounces 250ms; requires ≥3 chars.
   * Off by default — estate search normally uses exact match on Enter/button.
   */
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

  /**
   * User picked a match from the suggestion list or the "found" result card.
   * Prefills form via applyPrefill, locks registration to EXISTING_ASSET, and
   * suppresses further typeahead until searchEstateNo is edited again.
   *
   * @param {string} fieldName - Lookup field name.
   * @param {object} [match]   - { estateNo, prefill } from suggestions; else uses searchPanel.
   */
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
      setToast({ message: t("CS_COMMON_RECORD_FOUND"), error: false });
    },
    [searchPanel, applyPrefill, formData, t]
  );

  /**
   * "Create new" from a not-found search panel.
   * Resets the form to a blank NEW_BUILDING baseline with registration details shown.
   */
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

  /**
   * Debounced auto-draft: when onPersistDraft is set and no explicit draft button
   * is shown, persists buildPayload(formData) 400ms after formData changes
   * (only after the initial MDMS sync has completed).
   */
  useEffect(() => {
    if (!onPersistDraft || showDraftButton || isLoading || !hasSyncedRef.current) return;

    const timer = setTimeout(() => {
      onPersistDraft(buildPayload(formData));
    }, 400);

    return () => clearTimeout(timer);
  }, [formData, onPersistDraft, showDraftButton, isLoading]);

  /**
   * Uploads a file via Digit.UploadServices.Filestorage and stores
   * { filestoreId, documentuuid, documentType } on the field.
   * Rejects files ≥ 5MB with a toast. Module defaults to "ESTATE";
   * override with routeConfig.uploadModule.
   *
   * @param {string} fieldName - File field name.
   * @param {File}   file      - Browser File from UploadFile.
   * @returns {Promise<void>}
   */
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

  /**
   * Primary submit / Save & Next / Search / Update handler.
   * Wizard: validates fields + crossFieldValidations; on success buildPayload then
   *   - create → onSelect(config.key, { [payloadKey]: [formVal] }) + onSubmit
   *   - edit   → updateMutation.mutate(...) then onSubmit with response/error
   * Search: skips wizard validation; maps filters via mapFormToSearchFilters → onSubmit.
   */
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
      onSubmit?.({ payload: { ...filters, offset: 0 }, formValues: formVal, isSearch: true });
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
          setToast({ message: t("CS_COMMON_UPDATE_FAILED"), error: true });
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

  /**
   * Cancel / Clear All: rebuilds form from resetBaseline (preferred) or rawAsset,
   * re-applies computed fields, clears errors / search panel, then calls onCancel.
   */
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

  /**
   * Explicit Save Draft button handler.
   * Passes buildPayload(formData) to onSaveDraft and shows a success toast.
   */
  const handleSaveDraft = useCallback(() => {
    if (!onSaveDraft) return;
    onSaveDraft(buildPayload(formData));
    setToast({ message: t(draftSuccessLabel), error: false });
  }, [onSaveDraft, formData, draftSuccessLabel, t]);

  /** Primary action label: Update (edit) / Search (search mode) / SAVE & NEXT (create). */
  const buttonLabel = isEditMode
    ? routeConfig.actionButton?.text?.edit || "UPDATE"
    : routeConfig.actionButton?.text?.create || (isSearchMode ? "ES_COMMON_SEARCH" : "SAVE & NEXT");

  /** Clear / cancel label for search clear link (and related copy). */
  const clearLabel = cancelLabel || routeConfig.actionButton?.text?.clear || "ES_COMMON_CLEAR_ALL";

  // Only block the first paint. Remounting the form on later MDMS/locality
  // refetches wipes in-progress dropdown selections (search filters, etc.).
  if (isLoading && !hasSyncedRef.current) return <Loader />;

  /**
   * True when this form uses the Existing Asset / New Building registration gate
   * (assetRegistrationType field or visibleWhen on that gate). MDMS Estate.Config
   * without that gate always shows the ActionBar.
   */
  const usesRegistrationGate = flatFields.some(
    (fc) =>
      fc?.field?.name === "assetRegistrationType" ||
      fc?.visibleWhen?.field === "showRegistrationDetails" ||
      fc?.visibleWhen?.field === "assetRegistrationType"
  );
  /** Show Cancel/Draft/Submit only when ungated or registration details are ready. */
  const showActionBar =
    !usesRegistrationGate ||
    String(formData.showRegistrationDetails || "").toUpperCase() === "YES" ||
    String(formData.assetRegistrationType || "").toUpperCase() === "NEW_BUILDING";

  /** One DynamicFormField node per sorted top-level form entry. */
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

  /** Inline search: SubmitBar (form submit) + clear link inside SearchField. */
  const searchActions = !isDisabled && isSearchMode && (
    <SearchField className="submit">
      <SubmitBar label={t(buttonLabel)} submit />
      <p className="dynamic-form-search-clear" onClick={handleCancel}>
        {t(clearLabel)}
      </p>
    </SearchField>
  );

  /** Stacked search: SubmitBar calling goNext + clear, below the field list. */
  const stackedSearchActions = !isDisabled && isSearchMode && !isInlineSearch && (
    <div className="dynamic-form-search-actions">
      <SubmitBar
        label={t(buttonLabel)}
        onSubmit={goNext}
        className="dynamic-form-search-submit"
      />
      <p className="dynamic-form-search-clear" onClick={handleCancel}>
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
          <p key={i} className="dynamic-form-error">{t(msg)}</p>
        ))}

        {toast && (
          <Toast label={toast.message} error={toast.error} onClose={() => setToast(null)} />
        )}
      </>
    );
  }

  return (
    <div className="dynamic-form-container">
      {fieldNodes}

      {crossFieldMessages.map((msg, i) => (
        <p key={i} className="dynamic-form-error">{t(msg)}</p>
      ))}

      {stackedSearchActions}

      {!isDisabled && !isSearchMode && showActionBar && (
        <ActionBar className="dynamic-form-action">
          {showCancel && (
            <ButtonSelector
              theme="border"
              label={t(cancelLabel)}
              onSubmit={handleCancel}
              className="dynamic-form-margin-right"
            />
          )}
          {showDraftButton && (
            <ButtonSelector
              theme="border"
              label={t(draftLabel)}
              onSubmit={handleSaveDraft}
              className="dynamic-form-margin-right"
            />
          )}
          <SubmitBar
            label={t(buttonLabel)}
            onSubmit={goNext}
            disabled={isSubmitting}
            className={showCancel || showDraftButton ? "dynamic-form-submit-flex" : undefined}
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
