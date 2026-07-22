import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  Card,
  Loader,
  Header,
  DynamicForm,
  flattenFormConfig,
  useIsMobile,
  useClientPagination,
} from "@nudmcdgnpm/digit-ui-react-components";
import { getApplicationDetailsPath, getCreateAssetPath } from "../utils/estRoutes";
import {
  fetchAllottedAssetNos,
  getAssetIdentity,
} from "../utils/allotmentFormUtils";
import { resolveSearchApplicationConfig } from "../utils/estMdmsUtils";
import AssetTable from "./shared/AssetTable";
import useAssetTableColumns from "./shared/useAssetTableColumns";
import styles from "../styles/ESTSearchApplication.module.scss";

/** Map stored API filter keys back to DynamicForm field names for editData. */
const toSearchFormEditData = (filters = {}, formConfig = []) => {
  if (!filters || typeof filters !== "object") return {};
  const edit = {};
  flattenFormConfig(formConfig).forEach((fc) => {
    const name = fc?.field?.name;
    if (!name) return;
    const apiKey = fc.apiFieldName || fc.submitKey || name;
    const val = filters[apiKey] ?? filters[name];
    if (val !== undefined && val !== null && val !== "") {
      edit[name] = val;
    }
  });
  return edit;
};

/**
 * Shared EST search page (employee + citizen).
 *
 * MDMS drives search fields via `mdmsMasterName` (default: searchApplicationConfig).
 * Local fallback lives in resolveSearchApplicationConfig (searchApplicationConfig.js).
 * Pass `renderResultItem` + resultMode "cards" for citizen card lists.
 * Pass `initialSearchFilters` / `initialFormValues` to restore after navigating away.
 */
const ESTSearchApplication = ({
  tenantId,
  isLoading,
  t,
  onSubmit,
  data,
  count,
  initialSearchFilters = {},
  initialFormValues = {},
  config: configOverride,
  mdmsMasterName = "searchApplicationConfig",
  renderResultItem,
  headerCount,
}) => {
  const { data: mdmsSearchConfig, isLoading: mdmsLoading } = Digit.Hooks.useEnabledMDMS(
    Digit.ULBService.getStateId(),
    "Estate",
    [{ name: mdmsMasterName }],
    {
      select: (mdms) => mdms?.Estate?.[mdmsMasterName],
    }
  );

  const config = useMemo(
    () => resolveSearchApplicationConfig(mdmsSearchConfig, configOverride),
    [mdmsSearchConfig, configOverride]
  );

  const navigate = Digit.Hooks.useCustomNavigate();
  const { path: modulePath } = Digit.Hooks.useModuleBasePath();
  const isMobile = useIsMobile();
  const [isCleared, setIsCleared] = useState(false);
  const [formResetKey, setFormResetKey] = useState(0);
  const [allottedAssetNos, setAllottedAssetNos] = useState(new Set());
  const [sessionParams] = Digit.Hooks.useSessionStorage(config.sessionKey, {});

  const resultMode = config.resultMode || "table";
  const showCreateAction = config.emptyState?.showCreateAction !== false;

  const searchEditData = useMemo(() => {
    if (initialFormValues && Object.keys(initialFormValues).length > 0) {
      return initialFormValues;
    }
    return toSearchFormEditData(initialSearchFilters, config.routeConfig?.form || []);
  }, [initialFormValues, initialSearchFilters, config.routeConfig?.form]);

  // Remount search form once MDMS config is ready so editData hydrates dropdowns.
  useEffect(() => {
    if (mdmsLoading) return;
    if (!searchEditData || Object.keys(searchEditData).length === 0) return;
    setFormResetKey((k) => k + 1);
    // Only when MDMS finishes loading with restored filters — not on every filter change.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [mdmsLoading]);

  const { setValue, paginatedData, pagination } = useClientPagination({
    defaultValues: config.paginationDefaults,
    data: Array.isArray(data) ? data : [],
    count,
    isCleared,
  });

  useEffect(() => {
    if (resultMode !== "table" || !Array.isArray(data) || data.length === 0 || isCleared) {
      setAllottedAssetNos(new Set());
      return;
    }

    let cancelled = false;
    fetchAllottedAssetNos(data, tenantId).then((allotted) => {
      if (!cancelled) setAllottedAssetNos(allotted);
    });
    return () => {
      cancelled = true;
    };
  }, [data, tenantId, isCleared, resultMode]);

  const navigateToAssignFlow = useCallback(
    (asset, { allotmentForm, targetStep = "info", resetSession = false } = {}) => {
      if (!config.assignRoute) return;
      navigate(`${modulePath}/${config.assignRoute}/${targetStep}`, {
        state: {
          assetData: asset,
          allotmentData: allotmentForm,
          resetSession,
        },
      });
    },
    [navigate, modulePath, config.assignRoute]
  );

  const handleAllotAsset = useCallback(
    (asset) => {
      const hasSavedDraft =
        getAssetIdentity(sessionParams?.assetData) === getAssetIdentity(asset) &&
        sessionParams?.Allotments?.Allotments?.[0];

      navigateToAssignFlow(asset, {
        targetStep: hasSavedDraft ? "assign-assets" : "info",
        resetSession: !hasSavedDraft,
      });
    },
    [sessionParams, navigateToAssignFlow]
  );

  const handleEstateNoClick = useCallback(
    (asset) => {
      const estateNo = asset?.estateNo || asset?.assetNo;
      if (!estateNo) return;
      navigate(getApplicationDetailsPath(modulePath, estateNo), {
        state: { applicationData: asset },
      });
    },
    [navigate, modulePath]
  );

  const columns = useAssetTableColumns({
    isMobile,
    modulePath,
    navigate,
    onEstateNoClick: handleEstateNoClick,
    showAssetRef: config.table?.showAssetRef,
    actions: config.table?.actions,
    onAllot: handleAllotAsset,
    allottedAssetNos,
  });

  const handleSearch = useCallback(
    ({ payload, formValues }) => {
      setIsCleared(false);
      setValue("offset", 0);
      onSubmit?.({
        ...config.paginationDefaults,
        ...payload,
        offset: 0,
        _formValues: formValues || null,
      });
    },
    [onSubmit, setValue, config.paginationDefaults]
  );

  const handleClear = useCallback(() => {
    setIsCleared(true);
    onSubmit?.(null);
    // Remount DynamicForm so Clear resets to blank (not restored session filters).
    setFormResetKey((k) => k + 1);
  }, [onSubmit]);

  const headerLabel = t(config.header);
  const headerText =
    headerCount != null ? `${headerLabel} (${headerCount})` : headerLabel;

  const hasEmptyDisplay = !mdmsLoading && !isLoading && data?.display;
  const hasResults = !mdmsLoading && !isLoading && Array.isArray(data) && data.length > 0;
  const showLoader = mdmsLoading || isLoading;

  return (
    <div className={styles["est-search-application"]}>
      <div className={styles["est-search-application__header"]}>
        <Header>{headerText}</Header>
      </div>

      <DynamicForm
        key={formResetKey}
        mode="search"
        routeConfig={config.routeConfig}
        config={{ key: config.routeConfig.key }}
        tenantId={tenantId}
        t={t}
        editData={searchEditData}
        onSubmit={handleSearch}
        onCancel={handleClear}
        cancelLabel={config.routeConfig.actionButton?.text?.clear || "ES_COMMON_CLEAR_ALL"}
      />

      {hasEmptyDisplay ? (
        <Card className={styles["est-search-application__empty-card"]}>
          {String(t(data.display) || "")
            .split("\\n")
            .map((text, index) => (
              <p key={index}>{text}</p>
            ))}
          {showCreateAction ? (
            <button
              type="button"
              onClick={() => navigate(getCreateAssetPath(modulePath))}
              className={styles["est-search-application__create-btn"]}
            >
              {t(config.emptyState.actionLabel)}
            </button>
          ) : null}
        </Card>
      ) : hasResults && resultMode === "cards" && typeof renderResultItem === "function" ? (
        <div className={styles["est-search-application__cards"]}>
          {(paginatedData?.length ? paginatedData : data).map((item, index) => (
            <React.Fragment key={item.assetId || item.estateNo || index}>
              {renderResultItem(item, index)}
            </React.Fragment>
          ))}
        </div>
      ) : hasResults && resultMode === "table" ? (
        <AssetTable
          t={t}
          data={paginatedData}
          columns={columns}
          totalRecords={count}
          isMobile={isMobile}
          pagination={pagination}
        />
      ) : (
        showLoader && <Loader />
      )}
    </div>
  );
};

export default ESTSearchApplication;
