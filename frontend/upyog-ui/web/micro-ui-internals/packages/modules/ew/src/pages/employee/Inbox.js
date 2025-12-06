import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Header } from "@upyog/digit-ui-react-components";

import MobileInbox from "../../components/MobileInbox";
import EWDesktopInbox from "../../components/EWDesktopInbox";

/**
 * Inbox component for E-Waste management system.
 * Provides a responsive interface for managing E-Waste applications with filtering, 
 * sorting, and pagination capabilities.
 *
 * @param {Object} props Component properties
 * @param {boolean} props.useNewInboxAPI Flag to toggle between old and new inbox APIs
 * @param {string} props.parentRoute Base route for navigation
 * @param {string} props.moduleCode Module identifier, defaults to "EW"
 * @param {Object} props.initialStates Initial pagination and filter states
 * @param {React.Component} props.filterComponent Custom filter component
 * @param {boolean} props.isInbox Whether component is used in inbox view
 * @param {Function} props.rawWfHandler Custom workflow handler
 * @param {Function} props.rawSearchHandler Custom search handler
 * @param {Function} props.combineResponse Response combination logic
 * @param {Object} props.wfConfig Workflow configuration
 * @param {Object} props.searchConfig Search configuration
 * @param {Function} props.middlewaresWf Workflow middleware
 * @param {Function} props.middlewareSearch Search middleware
 * @param {React.Component} props.EmptyResultInboxComp Component for empty results
 * @returns {JSX.Element} Responsive inbox interface
 */
const Inbox = ({
  useNewInboxAPI,
  parentRoute,
  moduleCode = "EW",
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

  /**
   * State management for inbox configuration and pagination
   */
  const [enableSarch, setEnableSearch] = useState(() => (isInbox ? {} : { enabled: false }));
  const [TableConfig, setTableConfig] = useState(() => Digit.ComponentRegistryService?.getComponent("EWInboxTableConfig"));
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [sortParams, setSortParams] = useState(initialStates.sortParams || [{ id: "createdTime", desc: true }]);
  const [searchParams, setSearchParams] = useState(initialStates.searchParams || {});

  const isMobile = window.Digit.Utils.browser.isMobile();

  /**
   * Pagination configuration for API requests
   */
  const paginationParams = isMobile
    ? { limit: 100, offset: 0, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" };

  /**
   * Data fetching hook configuration
   */
  const { isFetching, isLoading: hookLoading, searchResponseKey, data, searchFields, ...rest } = useNewInboxAPI
    ? Digit.Hooks.useNewInboxGeneral({
        tenantId,
        ModuleCode: moduleCode,
        filters: { ...searchParams, ...paginationParams, sortParams },
      })
    : Digit.Hooks.useInboxGeneral({
        tenantId,
        businessService: "ewst",
        isInbox,
        filters: { ...searchParams, ...paginationParams, sortParams },
        rawWfHandler,
        rawSearchHandler,
        combineResponse,
        wfConfig,
        searchConfig: { ...enableSarch, ...searchConfig },
        middlewaresWf,
        middlewareSearch,
      });
      

  const Session = Digit.SessionStorage.get("User");
  const uuid = Session?.info?.uuid;

  /**
   * Effect Hooks for managing search parameters and pagination
   */
  useEffect(() => {
    setSearchParams({
      ...searchParams,
      lastModifiedBy: uuid,
    });
  }, []);

  useEffect(() => {
    setPageOffset(0);
  }, [searchParams]);

  /**
   * Pagination event handlers
   */
  const fetchNextPage = () => {
    setPageOffset((prevState) => prevState + pageSize);
  };

  const fetchPrevPage = () => {
    setPageOffset((prevState) => prevState - pageSize);
  };

  /**
   * Filter and sort handlers
   */
  const handleFilterChange = (filterParam) => {
    let keys_to_delete = filterParam.delete;
    let _new = { ...searchParams, ...filterParam };
    if (keys_to_delete) keys_to_delete.forEach((key) => delete _new[key]);
    delete filterParam.delete;
    setSearchParams({ ..._new });
    setEnableSearch({ enabled: true });
  };

  const handleSort = useCallback((args) => {
    if (args.length === 0) return;
    setSortParams(args);
  }, []);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };

  if (rest?.data?.length !== null) {
    if (isMobile) {
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
          tableConfig={rest?.tableConfig ? rest?.tableConfig : TableConfig(t)["EW"]}
          filterComponent={filterComponent}
          EmptyResultInboxComp={EmptyResultInboxComp}
          useNewInboxAPI={useNewInboxAPI}
        />
      );
    }
    return (
      <div>
        {isInbox && <Header>{t("ES_COMMON_INBOX")}</Header>}
        <EWDesktopInbox
          moduleCode={moduleCode}
          data={data}
          tableConfig={TableConfig(t)["EW"]}
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

export default Inbox;

