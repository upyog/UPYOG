import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Header } from "@nudmcdgnpm/digit-ui-react-components";

import GCDesktopInbox from "../../components/GCDesktopInbox";
import MobileInbox from "../../components/MobileInbox";

/**
 * Inbox Component
 * 
 * This component is responsible for rendering the inbox page for employees in the GC module.
 * It displays a list of applications or tasks, with options for filtering, sorting, and pagination.
 * 
 * Props:
 * - `useNewInboxAPI`: Boolean indicating whether to use the new inbox API.
 * - `parentRoute`: The base route for the inbox page.
 * - `moduleCode`: The module code for the inbox (default is "GC").
 * - `initialStates`: Object containing initial states for the inbox, including:
 *    - `pageOffset`: Initial page offset for pagination.
 *    - `pageSize`: Initial page size for pagination.
 *    - `sortParams`: Initial sorting parameters.
 *    - `searchParams`: Initial search parameters.
 * - `filterComponent`: Custom filter component to be displayed in the inbox.
 * - `isInbox`: Boolean indicating whether the component is used as an inbox.
 * - `rawWfHandler`: Custom handler for raw workflow data.
 * - `rawSearchHandler`: Custom handler for raw search data.
 * - `combineResponse`: Function to combine workflow and search responses.
 * - `wfConfig`: Configuration object for workflow-related settings.
 * - `searchConfig`: Configuration object for search-related settings.
 * - `middlewaresWf`: Middleware functions for workflow processing.
 * - `middlewareSearch`: Middleware functions for search processing.
 * - `EmptyResultInboxComp`: Component to display when the inbox has no results.
 * 
 * State Variables:
 * - `enableSearch`: State to enable or disable the search functionality.
 * - `TableConfig`: Configuration for the inbox table, fetched from the Digit Component Registry Service.
 * - `pageOffset`: Current page offset for pagination.
 * - `pageSize`: Current page size for pagination.
 * - `sortParams`: Current sorting parameters for the inbox.
 * - `searchParams`: Current search parameters for the inbox.
 * 
 * Variables:
 * - `tenantId`: The current tenant ID fetched using the Digit ULB Service.
 * - `isMobile`: Boolean indicating whether the application is being accessed on a mobile device.
 * - `paginationParams`: Object containing pagination parameters for the inbox.
 * 
 * Logic:
 * - Initializes state variables using the `initialStates` prop.
 * - Determines whether the application is being accessed on a mobile device.
 * - Configures the inbox table and pagination settings based on the module and tenant.
 * 
 * Returns:
 * - A desktop or mobile inbox component based on the device type, with filtering, sorting, and pagination functionality.
 */
/**
 * Inbox Component (Employee)
 * 
 * Renders the employee inbox page for the GC module, displaying a list of applications
 * or tasks with options for filtering, sorting, and pagination.
 * 
 * Features:
 * - Fetches inbox data using `useNewInboxGeneral` hook
 * - Supports both desktop (`GCDesktopInbox`) and mobile (`MobileInbox`) views
 * - Configurable filtering via `filterComponent` and search parameters
 * - Pagination with page size change, next/previous page navigation
 * - Sorting by creation date (ascending/descending)
 * - Handles filter changes, search, and state resets
 * 
 * Props: (see JSDoc above function definition for full list)
 */
const Inbox = ({
  useNewInboxAPI,
  parentRoute,
  moduleCode = "GC",
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
  const [enableSarch, setEnableSearch] = useState(() => (isInbox ? {} : { enabled: false }));
  const [TableConfig, setTableConfig] = useState(() => Digit.ComponentRegistryService?.getComponent("GCInboxTableConfig"));
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [sortParams, setSortParams] = useState(initialStates.sortParams || [{ id: "createdTime", desc: true }]);
  const [searchParams, setSearchParams] = useState(initialStates.searchParams || {});

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: 0, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" };

  const { isFetching, isLoading: hookLoading, searchResponseKey, data, searchFields, ...rest } = Digit.Hooks.useNewInboxGeneral({
    tenantId,
    ModuleCode: moduleCode,
    businessService: "garbage-service",
    filters: { ...searchParams, ...paginationParams, sortParams },
  });


     



  useEffect(() => {
    setPageOffset(0);
  }, [searchParams]);

  const fetchNextPage = () => {
    setPageOffset((prevState) => prevState + pageSize);
  };

  const fetchPrevPage = () => {
    setPageOffset((prevState) => prevState - pageSize);
  };

  const handleFilterChange = (filterParam) => {
    let keys_to_delete = filterParam.delete;
    let _new = { ...searchParams, ...filterParam };
    if (keys_to_delete) keys_to_delete.forEach((key) => delete _new[key]);
    delete filterParam.delete;
    setSearchParams({ ..._new });
    setEnableSearch({ enabled: true });
  };

  const GetTableConfig = () => (typeof TableConfig === "function" ? TableConfig(t)["GC"] : {});

  const handleSort = useCallback((args) => {
    if (args.length === 0) return;
    setSortParams(args);
  }, []);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };


const inboxData = data;


  if (data) {
    if (isMobile) {
      return (
        <MobileInbox
          data={inboxData}
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
          tableConfig={rest?.tableConfig ? rest?.tableConfig : GetTableConfig()}
          filterComponent={filterComponent}
          EmptyResultInboxComp={EmptyResultInboxComp}
          useNewInboxAPI={useNewInboxAPI}
        />
      );
    } else {
      return (
        <div>
          {isInbox && <Header>{t("ES_COMMON_INBOX")}</Header>}
         
          
          <GCDesktopInbox
            moduleCode={moduleCode}
            data={inboxData}
            
            tableConfig={rest?.tableConfig ? rest?.tableConfig : GetTableConfig()}
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
  }
};

export default Inbox;