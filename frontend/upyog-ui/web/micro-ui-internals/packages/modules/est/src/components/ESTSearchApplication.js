import React, { useCallback, useEffect, useMemo, useState } from "react";
import {
  Card,
  Loader,
  Header,
  DynamicForm,
  useIsMobile,
  useClientPagination,
} from "@nudmcdgnpm/digit-ui-react-components";
import { getCreateAssetPath } from "../utils/estRoutes";
import {
  fetchAllottedAssetNos,
  getAssetIdentity,
  hasExistingAllotment,
} from "../utils/allotmentFormUtils";
import { resolveSearchApplicationConfig } from "../utils/estMdmsUtils";
import AssetTable from "./shared/AssetTable";
import useAssetTableColumns from "./shared/useAssetTableColumns";
import styles from "../styles/ESTSearchApplication.module.scss";

const ESTSearchApplication = ({
  tenantId,
  isLoading,
  t,
  onSubmit,
  data,
  count,
  setShowToast,
  config: configOverride,
}) => {
  const { data: mdmsSearchConfig, isLoading: mdmsLoading } = Digit.Hooks.useEnabledMDMS(
    Digit.ULBService.getStateId(),
    "Estate",
    [{ name: "searchApplicationConfig" }],
    {
      select: (data) => data?.Estate?.searchApplicationConfig,
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
  const [allottedAssetNos, setAllottedAssetNos] = useState(new Set());
  const [sessionParams] = Digit.Hooks.useSessionStorage(config.sessionKey, {});

  const { setValue, paginatedData, pagination } = useClientPagination({
    defaultValues: config.paginationDefaults,
    data,
    count,
    isCleared,
  });

  useEffect(() => {
    if (!Array.isArray(data) || data.length === 0 || isCleared) {
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
  }, [data, tenantId, isCleared]);

  const navigateToAssignFlow = useCallback(
    (asset, { allotmentForm, targetStep = "info", resetSession = false } = {}) => {
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

  const handleAssetAction = useCallback(
    (asset) => {
      if (hasExistingAllotment(asset, allottedAssetNos)) return;
      handleAllotAsset(asset);
    },
    [allottedAssetNos, handleAllotAsset]
  );

  const columns = useAssetTableColumns({
    isMobile,
    modulePath,
    navigate,
    onEstateNoClick: handleAssetAction,
    showAssetRef: config.table.showAssetRef,
    actions: config.table.actions,
    onAllot: handleAllotAsset,
    allottedAssetNos,
  });

  const handleSearch = useCallback(
    ({ payload }) => {
      setIsCleared(false);
      setValue("offset", 0);
      onSubmit?.({
        ...config.paginationDefaults,
        ...payload,
        offset: 0,
      });
    },
    [onSubmit, setValue, config.paginationDefaults]
  );

  const handleClear = useCallback(() => {
    setShowToast?.(null);
    setIsCleared(true);
  }, [setShowToast]);

  return (
    <div className={styles["est-search-application"]}>
      <div className={styles["est-search-application__header"]}>
        <Header>{t(config.header)}</Header>
      </div>

      <DynamicForm
        mode="search"
        routeConfig={config.routeConfig}
        config={{ key: config.routeConfig.key }}
        tenantId={tenantId}
        t={t}
        onSubmit={handleSearch}
        onCancel={handleClear}
        cancelLabel={config.routeConfig.actionButton?.text?.clear || "ES_COMMON_CLEAR_ALL"}
      />

      {mdmsLoading && <Loader />}

      {!mdmsLoading && !isLoading && data?.display ? (
        <Card className={styles["est-search-application__empty-card"]}>
          {String(t(data.display) || "")
            .split("\\n")
            .map((text, index) => (
              <p key={index}>{text}</p>
            ))}
          <button
            type="button"
            onClick={() => navigate(getCreateAssetPath(modulePath))}
            className={styles["est-search-application__create-btn"]}
          >
            {t(config.emptyState.actionLabel)}
          </button>
        </Card>
      ) : !mdmsLoading && !isLoading && Array.isArray(data) && data.length > 0 ? (
        <AssetTable
          t={t}
          data={paginatedData}
          columns={columns}
          totalRecords={count}
          isMobile={isMobile}
          pagination={pagination}
        />
      ) : (
        (mdmsLoading || isLoading) && <Loader />
      )}
    </div>
  );
};

export default ESTSearchApplication;
