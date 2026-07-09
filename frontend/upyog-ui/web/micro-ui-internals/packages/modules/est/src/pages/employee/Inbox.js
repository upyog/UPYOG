import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Header } from "@nudmcdgnpm/digit-ui-react-components";
import ESTDesktopInbox from "../../components/ESTDesktopInbox";
import MobileInbox from "../../components/MobileInbox";

/**
 * ESTInbox
 *
 * Same pattern as other module inboxes but wired for EST module.
 *
 * Props:
 * - useNewInboxAPI (bool)
 * - parentRoute (string)
 * - moduleCode (string) default "EST"
 * - initialStates (object)
 * - filterComponent
 * - isInbox (bool)
 * - rawWfHandler, rawSearchHandler, combineResponse, wfConfig, searchConfig, middlewaresWf, middlewareSearch, EmptyResultInboxComp
 */
const ESTInbox = ({
  useNewInboxAPI,
  parentRoute,
  moduleCode = "EST",
  initialStates = {},
  filterComponent,
  isInbox,
  rawWfHandler,
  rawSearchHandler,
  combineResponse,
  wfConfig,
  searchConfig,
  middlewaresWf,
  middlewareSearch,
  EmptyResultInboxComp,
}) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();

  const [enableSearch, setEnableSearch] = useState(() => (isInbox ? {} : { enabled: false }));
  const [TableConfig] = useState(() => Digit.ComponentRegistryService?.getComponent("ESTInboxTableConfig"));

  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [sortParams, setSortParams] = useState(initialStates.sortParams || [{ id: "createdTime", desc: true }]);
  const [searchParams, setSearchParams] = useState(initialStates.searchParams || {});

  const paginationParams = isMobile
    ? { limit: 100, offset: 0, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" };

  const hookArgsNew = {
    tenantId,
    ModuleCode: moduleCode,
    filters: { ...searchParams, ...paginationParams, sortParams },
  };

  const hookArgsOld = {
    tenantId,
    businessService: moduleCode,
    isInbox,
    filters: { ...searchParams, ...paginationParams, sortParams },
    rawWfHandler,
    rawSearchHandler,
    combineResponse,
    wfConfig,
    searchConfig: { ...enableSearch, ...searchConfig },
    middlewaresWf,
    middlewareSearch,
  };

  const hookResult = useNewInboxAPI
    ? Digit.Hooks.useNewInboxGeneral(hookArgsNew)
    : Digit.Hooks.useInboxGeneral(hookArgsOld);

  const { isFetching, isLoading: hookLoading, searchResponseKey, data, searchFields, tableConfig: hookTableConfig, ...rest } =
    hookResult || {};

  useEffect(() => {
    setPageOffset(0);
  }, [searchParams]);

  const fetchNextPage = useCallback(() => setPageOffset((p) => p + pageSize), [pageSize]);
  const fetchPrevPage = useCallback(() => setPageOffset((p) => p - pageSize), []);

  const handleFilterChange = (filterParam) => {
    const keys_to_delete = filterParam.delete;
    const _new = { ...searchParams, ...filterParam };
    if (keys_to_delete) keys_to_delete.forEach((key) => delete _new[key]);
    delete filterParam.delete;
    setSearchParams({ ..._new });
    setEnableSearch({ enabled: true });
  };

  const handleSort = useCallback((args) => {
    if (!args || args.length === 0) return;
    setSortParams(args);
  }, []);

  const handlePageSizeChange = (e) => setPageSize(Number(e.target.value));

  // Prefer hook-provided tableConfig; otherwise fallback to registered ESTInboxTableConfig
  const resolvedTableConfig = hookTableConfig ? hookTableConfig : (TableConfig && TableConfig(t) && TableConfig(t)["EST"]);

  // Mobile view
  if (rest?.data?.length !== null && isMobile) {
    return (
      <MobileInbox
        data={data}
        isLoading={hookLoading}
        isSearch={!isInbox}
        searchFields={searchFields}
        onFilterChange={handleFilterChange}
        onSearch={handleFilterChange}
        onSort={handleSort}
        parentRoute={parentRoute}
        searchParams={searchParams}
        sortParams={sortParams}
        linkPrefix={`${parentRoute}/application-details/`}
        tableConfig={hookTableConfig ? hookTableConfig : resolvedTableConfig}
        filterComponent={filterComponent}
        EmptyResultInboxComp={EmptyResultInboxComp}
        useNewInboxAPI={useNewInboxAPI}
      />
    );
  }

  // Desktop view
  if (rest?.data?.length !== null) {
    return (
      <div>
        {isInbox && <Header>{t("ES_COMMON_INBOX")}</Header>}

        <ESTDesktopInbox
          moduleCode={moduleCode}
          data={data}
          tableConfig={resolvedTableConfig}
          isLoading={hookLoading}
          defaultSearchParams={initialStates.searchParams}
          isSearch={!isInbox}
          onFilterChange={handleFilterChange}
          searchFields={searchFields}
          onSearch={handleFilterChange}
          onSort={handleSort}
          onNextPage={fetchNextPage}
          onPrevPage={fetchPrevPage}
          currentPage={Math.floor(pageOffset / pageSize)}
          pageSizeLimit={pageSize}
          disableSort={false}
          onPageSizeChange={handlePageSizeChange}
          parentRoute={parentRoute}
          searchParams={searchParams}
          sortParams={sortParams}
          totalRecords={Number(data?.[0]?.totalCount)}
          filterComponent={filterComponent}
          EmptyResultInboxComp={EmptyResultInboxComp}
          useNewInboxAPI={useNewInboxAPI}
        />
      </div>
    );
  }

  return null;
};

export default ESTInbox;