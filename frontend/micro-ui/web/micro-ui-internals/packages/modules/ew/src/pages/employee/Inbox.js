// Importing necessary components and hooks from external libraries and local files
import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next"; // Hook for translations
import { Header } from "@nudmcdgnpm/digit-ui-react-components"; // Header component for displaying titles
import MobileInbox from "../../components/MobileInbox"; // Component for rendering the inbox on mobile devices
import EWDesktopInbox from "../../components/EWDesktopInbox"; // Component for rendering the inbox on desktop devices

// Main component for the Inbox page
const Inbox = ({
  useNewInboxAPI, // Flag to determine whether to use the new inbox API
  parentRoute, // Parent route for navigation
  moduleCode = "EW", // Module code for E-Waste
  initialStates = {}, // Initial state for the inbox
  filterComponent, // Component for filtering the inbox
  isInbox, // Flag to indicate if this is an inbox
  rawWfHandler, // Workflow handler for raw data
  rawSearchHandler, // Search handler for raw data
  combineResponse, // Function to combine API responses
  wfConfig, // Workflow configuration
  searchConfig, // Search configuration
  middlewaresWf, // Middleware for workflows
  middlewareSearch, // Middleware for search
  EmptyResultInboxComp, // Component to display when no results are found
}) => {
  const tenantId = Digit.ULBService.getCurrentTenantId(); // Fetching the current tenant ID
  const { t } = useTranslation(); // Translation hook

  // State variables for managing the inbox
  const [enableSarch, setEnableSearch] = useState(() => (isInbox ? {} : { enabled: false })); // State to enable or disable search
  const [TableConfig, setTableConfig] = useState(() => Digit.ComponentRegistryService?.getComponent("EWInboxTableConfig")); // Table configuration
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0); // State for page offset
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10); // State for page size
  const [sortParams, setSortParams] = useState(initialStates.sortParams || [{ id: "createdTime", desc: true }]); // State for sort parameters
  const [searchParams, setSearchParams] = useState(initialStates.searchParams || {}); // State for search parameters

  // Check if the view is on a mobile device
  let isMobile = window.Digit.Utils.browser.isMobile();

  // Pagination parameters for API calls
  let paginationParams = isMobile
    ? { limit: 100, offset: 0, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" };

  // Fetching data using the appropriate inbox API
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

  // Fetching the current user's UUID from session storage
  const Session = Digit.SessionStorage.get("User");
  const uuid = Session?.info?.uuid;

  // Effect to set the last modified by parameter in searchParams
  useEffect(() => {
    setSearchParams({
      ...searchParams,
      lastModifiedBy: uuid,
    });
  }, []);

  // Effect to reset the page offset when search parameters change
  useEffect(() => {
    setPageOffset(0);
  }, [searchParams]);

  // Function to fetch the next page of results
  const fetchNextPage = () => {
    setPageOffset((prevState) => prevState + pageSize);
  };

  // Function to fetch the previous page of results
  const fetchPrevPage = () => {
    setPageOffset((prevState) => prevState - pageSize);
  };

  // Function to handle changes in filters
  const handleFilterChange = (filterParam) => {
    let keys_to_delete = filterParam.delete;
    let _new = { ...searchParams, ...filterParam };
    if (keys_to_delete) keys_to_delete.forEach((key) => delete _new[key]);
    delete filterParam.delete;
    setSearchParams({ ..._new });
    setEnableSearch({ enabled: true });
  };

  // Function to handle sorting
  const handleSort = useCallback((args) => {
    if (args.length === 0) return;
    setSortParams(args);
  }, []);

  // Function to handle changes in page size
  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };

  // Rendering the inbox based on the device type (mobile or desktop)
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
    } else {
      return (
        <div>
          {/* Header for the inbox */}
          {isInbox && <Header>{t("ES_COMMON_INBOX")}</Header>}

          {/* Rendering the desktop inbox */}
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
  }
};

export default Inbox; // Exporting the Inbox component