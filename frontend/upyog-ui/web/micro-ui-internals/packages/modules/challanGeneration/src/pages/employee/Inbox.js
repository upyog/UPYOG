import React, { useCallback, useEffect, useState, useReducer, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { Header } from "@nudmcdgnpm/digit-ui-react-components";
import { Loader } from "../../components/Loader";

import DesktopInbox from "../../components/DesktopInbox";
import MobileInbox from "../../components/MobileInbox";
import { businessServiceList } from "../../utils";

/**
 * Inbox component:
 * - Displays challan list with search, filter, sort, and pagination
 * - Supports responsive views (mobile & desktop)
 */

const Inbox = ({
  parentRoute,
  businessService = "PT",
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
}) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { t } = useTranslation();
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [sortParams, setSortParams] = useState(initialStates.sortParams || [{ id: "createdTime", desc: false }]);
  const [searchParams, setSearchParams] = useState(initialStates.searchParams || {});
  const [isLoader, setIsLoader] = useState(false);
  const [getFilter, setFilter] = useState();

  const isMobile = window.Digit.Utils.browser.isMobile();
  const paginationParams = isMobile
    ? { limit: 100, offset: 0, sortOrder: sortParams?.[0]?.desc ? "ASC" : "DESC" }
    : { limit: pageSize, offset: pageOffset, sortOrder: sortParams?.[0]?.desc ? "ASC" : "DESC" };


  const InboxObjectInSessionStorage = Digit.SessionStorage.get("Challan.INBOX");

  const searchFormDefaultValues = {
    // add defaults if needed
  };

  const filterFormDefaultValues = {
    moduleName: "Challan_Generation", // <--- default moduleName for CHB inbox
    applicationStatus: [],
    businessService: null,
    locality: [],
    assignee: "ASSIGNED_TO_ALL",
    businessServiceArray: businessServiceList(true) || [],
  };

  const tableOrderFormDefaultValues = {
    sortBy: "",
    limit: window.Digit.Utils.browser.isMobile() ? 50 : 10,
    offset: 0,
    sortOrder: "DESC",
  };

  const { isLoading: hookLoading, data } = Digit.Hooks.challangeneration.useInbox({
    tenantId,
    filters: { ...searchParams, ...paginationParams },
  });

  const formedData = (data?.table || []).map((item) => ({
    challanNo: item?.applicationId,
    name: item?.offenderName,
    applicationStatus: item?.status,
    businessService: item?.businessService,
    totalAmount: item?.amount || 0,
    offenceName: item?.offenceTypeName,
    challanStatus: item?.challanStatus,
    date: item?.date,
    feeWaiver: item?.feeWaiver,
  }));

  useEffect(() => {
    setPageOffset(0);
  }, [searchParams]);

  const fetchNextPage = () => setPageOffset((prev) => prev + pageSize);
  const fetchPrevPage = () => setPageOffset((prev) => prev - pageSize);
  const fetchLastPage = () => setPageOffset(data?.totalCount ? Math.ceil(data.totalCount / 10) * 10 - pageSize : 0);
  const fetchFirstPage = () => setPageOffset(0);

  const handleFilterChange = (filterParam) => {
    let keys_to_delete = filterParam.delete;
    let _new = isMobile ? { ...filterParam } : { ...searchParams, ...filterParam };
    if (keys_to_delete) keys_to_delete.forEach((key) => delete _new[key]);
    delete _new.delete;
    setSearchParams(_new);
  };

  const handleSort = useCallback((args) => {
    if (args.length === 0) return;
    setSortParams(args);
  }, []);

  const handlePageSizeChange = (e) => setPageSize(Number(e.target.value));

  const getSearchFields = () => [
    { label: t("UC_CHALLAN_NO"), name: "challanNo" },
    {
      label: t("UC_MOBILE_NO_LABEL"),
      name: "mobileNumber",
      maxlength: 10,
      pattern: "[6-9][0-9]{9}",
      title: t("ES_SEARCH_APPLICATION_MOBILE_INVALID"),
      componentInFront: "+91",
    },
  ];

  if (isMobile) {
    return (
      <MobileInbox
        data={formedData}
        defaultSearchParams={initialStates.searchParams}
        isLoading={hookLoading}
        isSearch={!isInbox}
        searchFields={getSearchFields()}
        onFilterChange={handleFilterChange}
        onSearch={handleFilterChange}
        onSort={handleSort}
        parentRoute={parentRoute}
        searchParams={searchParams}
        sortParams={sortParams}
        filterComponent={filterComponent}
      />
    );
  } else {
    return (
      <div>
        {isInbox && (
          <Header>
            {t("ES_COMMON_INBOX")}
            {data?.totalCount ? <p className="inbox-count">{data?.totalCount}</p> : null}
          </Header>
        )}
        <DesktopInbox
          businessService={businessService}
          data={formedData}
          isLoading={hookLoading}
          defaultSearchParams={initialStates.searchParams}
          isSearch={!isInbox}
          onFilterChange={handleFilterChange}
          searchFields={getSearchFields()}
          onSearch={handleFilterChange}
          onSort={handleSort}
          onNextPage={fetchNextPage}
          onPrevPage={fetchPrevPage}
          onLastPage={fetchLastPage}
          onFirstPage={fetchFirstPage}
          currentPage={Math.floor(pageOffset / pageSize)}
          pageSizeLimit={pageSize}
          disableSort={false}
          onPageSizeChange={handlePageSizeChange}
          parentRoute={parentRoute}
          searchParams={searchParams}
          sortParams={sortParams}
          totalRecords={data?.totalCount}
          filterComponent={filterComponent}
          isLoader={isLoader}
          statutes={data?.statuses}
        />
        {hookLoading && <Loader page={true} />}
      </div>
    );
  }
  // }

  return null;
};

export default Inbox;
