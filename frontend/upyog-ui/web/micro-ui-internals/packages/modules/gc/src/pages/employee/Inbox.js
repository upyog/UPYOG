import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Header } from "@nudmcdgnpm/digit-ui-react-components";

import GCDesktopInbox from "../../components/GCDesktopInbox";
import MobileInbox from "../../components/MobileInbox";

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
 * - Client-side mobile number filtering as a fallback for backends that ignore it
 * 
 * Props:
 * - `useNewInboxAPI`: Boolean to use the new inbox API
 * - `parentRoute`: Base route for the inbox page
 * - `moduleCode`: Module code (default "GC")
 * - `initialStates`: Initial pageOffset, pageSize, sortParams, searchParams
 * - `filterComponent`: String identifier of the filter component in the registry
 * - `isInbox`: Boolean indicating inbox vs. search mode
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
    setPageOffset(0);
  };


  // The GC inbox API currently ignores `mobileNumber` in some deployments.
  // Filter the returned business objects as a fallback so the inbox search
  // still returns only applications belonging to the entered number.
  const searchedMobileNumber = String(searchParams?.mobileNumber || "").replace(/\D/g, "");
  const inboxData = searchedMobileNumber
    ? Array.isArray(data)
      ? data.filter((entry) => {
          const application = entry?.searchData || {};
          const applicants = [
            ...(Array.isArray(application?.applicantDetails) ? application.applicantDetails : []),
            ...(Array.isArray(application?.additionalDetail?.applicantDetails) ? application.additionalDetail.applicantDetails : []),
            ...(Array.isArray(application?.additionalDetails?.applicantDetails) ? application.additionalDetails.applicantDetails : []),
          ];
          const mobileNumbers = [
            application?.mobileNumber,
            application?.garbageSpecification?.phoneNumber,
            application?.grbgCollectionUnits?.[0]?.phoneNumber,
            ...applicants.map((applicant) => applicant?.mobileNumber),
          ]
            .filter(Boolean)
            .map((mobileNumber) => String(mobileNumber).replace(/\D/g, ""));

          return mobileNumbers.some((mobileNumber) => mobileNumber.includes(searchedMobileNumber));
        })
      : []
    : data;


  if (rest?.data?.length !== null) {
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
            totalRecords={searchedMobileNumber ? (inboxData?.length || 0) : Number(data?.[0]?.totalCount || 0)}
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
