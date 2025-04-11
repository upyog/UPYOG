import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Header } from "@nudmcdgnpm/digit-ui-react-components";
import MobileInbox from "../../components/MobileInbox";
import CNDDesktopInbox from "../../components/CNDDesktopInbox";

/**
 * The Inbox component serves as a controller for application inbox views, handling data fetching, filtering, searching, sorting, and pagination.
 * It conditionally renders either a mobile or desktop view based on the user's device.
 * 
 * Key features:
 * Adapts between mobile and desktop views based on screen size
 * Uses a data hook to fetch inbox data with configurable parameters
 * Manages pagination, sorting, and filtering state
 * Passes appropriate props to child components
 * Supports both new and legacy inbox APIs
 */

const Inbox = ({
  useNewInboxAPI,
  parentRoute,
  moduleCode = "CND",
  initialStates = {},
  filterComponent,
  isInbox,
  EmptyResultInboxComp,
}) => {
  const tenantId = Digit.ULBService.getCitizenCurrentTenant(true) || Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const [enableSarch, setEnableSearch] = useState(() => (isInbox ? {} : { enabled: false }));
  const [TableConfig, setTableConfig] = useState(() => Digit.ComponentRegistryService?.getComponent("CNDInboxConfig"));
  // const [getSearchFi]
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [sortParams, setSortParams] = useState(initialStates.sortParams || [{ id: "createdTime", desc: true }]);
  const [searchParams, setSearchParams] = useState(initialStates.searchParams || {});

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: 0, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" };

  const { isFetching, isLoading: hookLoading, searchResponseKey, data, searchFields, ...rest } =  Digit.Hooks.useNewInboxGeneral({
        tenantId,
        ModuleCode: moduleCode,
        filters: { ...searchParams, ...paginationParams, sortParams },
      })


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
          tableConfig={rest?.tableConfig ? rest?.tableConfig:TableConfig(t)["CND"]}
          filterComponent={filterComponent}
          EmptyResultInboxComp={EmptyResultInboxComp}
          useNewInboxAPI={useNewInboxAPI}
        />
      );
    } else {
      return (
        <div>
          {isInbox && <Header>{t("ES_COMMON_INBOX")}</Header>}

          <CNDDesktopInbox
            moduleCode={moduleCode}
            data={data}
            tableConfig={TableConfig(t)["CND"]}
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

