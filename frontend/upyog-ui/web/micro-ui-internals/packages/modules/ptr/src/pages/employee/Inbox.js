/**
 * @file Inbox.js
 * @description This component renders the Inbox view for both desktop and mobile screens. 
 * It supports pagination, filtering, and sorting of data fetched from the backend.
 * 
 * @component
 * - Uses Digit's hooks (`useNewInboxGeneral` or `useInboxGeneral`) to fetch inbox data.
 * - Displays either `PTRDesktopInbox` or `MobileInbox` based on the device type.
 * - Supports sorting, filtering, and pagination of inbox data.
 * - Renders an empty result component if no data is found.
 * 
 * @props
 * @param {function} useNewInboxAPI - Hook to use new inbox API or not.
 * @param {string} parentRoute - Base route path for navigation.
 * @param {string} moduleCode - The module code (default is "PTR").
 * @param {object} initialStates - Initial state settings for the inbox (pagination, sorting, etc.).
 * @param {JSX.Element} filterComponent - Component for filtering the inbox.
 * @param {boolean} isInbox - Flag indicating if it is an inbox or a search view.
 * @param {function} rawWfHandler - Workflow handler function.
 * @param {function} rawSearchHandler - Search handler function.
 * @param {boolean} combineResponse - Flag to combine responses.
 * @param {object} wfConfig - Workflow configuration.
 * @param {object} searchConfig - Search configuration.
 * @param {function} middlewaresWf - Middleware for workflow.
 * @param {function} middlewareSearch - Middleware for search.
 * @param {JSX.Element} EmptyResultInboxComp - Component to display when no data is found.
 * 
 * @usage 
 * <Inbox 
 *   useNewInboxAPI={true} 
 *   parentRoute="/inbox" 
 *   moduleCode="PTR" 
 *   initialStates={{ pageOffset: 0, pageSize: 10 }} 
 * />
 */

import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Header } from "@upyog/digit-ui-react-components";

import PTRDesktopInbox from "../../components/PTRDesktopInbox";
import MobileInbox from "../../components/MobileInbox";

const Inbox = ({
  useNewInboxAPI,
  parentRoute,
  moduleCode = "PTR",
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
  const [TableConfig, setTableConfig] = useState(() => Digit.ComponentRegistryService?.getComponent("PTRInboxTableConfig"));
  // const [getSearchFi]
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [sortParams, setSortParams] = useState(initialStates.sortParams || [{ id: "createdTime", desc: true }]);
  const [searchParams, setSearchParams] = useState(initialStates.searchParams || {});

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: 0, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" };

  const { isFetching, isLoading: hookLoading, searchResponseKey, data, searchFields, ...rest } = useNewInboxAPI
    ? Digit.Hooks.useNewInboxGeneral({
        tenantId,
        ModuleCode: moduleCode,
        filters: { ...searchParams, ...paginationParams, sortParams },
      })
    : Digit.Hooks.useInboxGeneral({
        tenantId,
        businessService: moduleCode,
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
          tableConfig={rest?.tableConfig ? res?.tableConfig:TableConfig(t)["PTR"]}
          filterComponent={filterComponent}
          EmptyResultInboxComp={EmptyResultInboxComp}
          useNewInboxAPI={useNewInboxAPI}
        />
        // <div></div>
      );
    } else {
      return (
        <div>
          {isInbox && <Header>{t("ES_COMMON_INBOX")}</Header>}
         
          
          <PTRDesktopInbox
            moduleCode={moduleCode}
            data={data}
            
            tableConfig={TableConfig(t)["PTR"]}
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

