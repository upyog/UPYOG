import { Header, Loader } from "@egovernments/digit-ui-react-components";
import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DesktopList from "../../../../components/List/SOR/DesktopList";
import MobileList from  "../../../../components/List/SOR/MobileList";

const WmsSorList = ({ parentRoute, businessService = "WMS", initialStates = {}, filterComponent, isList=false }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();

  const { t } = useTranslation();
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [sortParams, setSortParams] = useState(initialStates.sortParams || [{ id: "createdTime", desc: false }]);
  const [totalRecords, setTotalReacords] = useState(undefined);
  const [searchParams, setSearchParams] = useState(() => {
    return initialStates.searchParams || {};
  });

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: pageOffset, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" };
  const isupdate = Digit.SessionStorage.get("isupdate");
  //const { SORApplications } = async () => await WMSService.SORApplications.search(tenantId, searchparams, filters);// Digit.Hooks.wms.sor.useWmsSorSearch(
const { isLoading: hookLoading, isError, error, data, ...rest } = Digit.Hooks.wms.sor.useWmsSorSearch(
    searchParams,
    tenantId,
    paginationParams,
    isupdate
  );

  useEffect(() => {
     setTotalReacords(data?.length-1);
  }, [hookLoading,data,rest]);
//  useEffect(() => {setTotalReacords(SORApplications?.length-1);}, [SORApplications]);
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
    filterParam.delete;
    delete _new.delete;
    setSearchParams({ ..._new });
  };

  const handleSort = useCallback((args) => {
    if (args.length === 0) return;
    setSortParams(args);
  }, []);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };

  const getSearchFields = () => {
    return [
      {
        label: t("WMS_SOR_NAME_LABEL"),
        name: "sor_name",
        type:"name"
      },
      {
        label: t("WMS_SOR_START_DATE_LABEL"),
        name: "start_date",
        type:"date",
      },
      {
        label: t("WMS_SOR_END_DATE_LABEL"),
        name: "end_date",
        type:"date"
      },
    ];
  };

  if (hookLoading) {
    return <Loader />;
  }
/* else
{
  if(data!=undefined)
  alert(JSON.stringify(data))
} */
  //if (SORApplications?.length !== null) {
  if (data?.length !== null) {
    if (isMobile) {
      return (
        <MobileList
          businessService={businessService}          
          data={data}
          isLoading={hookLoading}
          defaultSearchParams={initialStates.searchParams}
          isSearch={isList}
          onFilterChange={handleFilterChange}
          searchFields={getSearchFields()}
          onSearch={handleFilterChange}
          onSort={handleSort}
          onNextPage={fetchNextPage}
          tableConfig={rest?.tableConfig}
          onPrevPage={fetchPrevPage}
          currentPage={Math.floor(pageOffset / pageSize)}
          pageSizeLimit={pageSize}
          disableSort={false}
          onPageSizeChange={handlePageSizeChange}
          parentRoute={parentRoute}
          searchParams={searchParams}
          sortParams={sortParams}
          totalRecords={totalRecords}
          linkPrefix={'/upyog-ui/citizen/wms/sor-edit/'}
          filterComponent={filterComponent}
        />
      );
    } else {
      return (
        <div>
          {isList && <Header>{t("WMS_HOME_SEARCH_RESULTS_HEADING")}</Header>}
          <DesktopList
            businessService={businessService}
            data={data}
            isLoading={hookLoading}
            defaultSearchParams={initialStates.searchParams}
            isSearch={isList}
            onFilterChange={handleFilterChange}
            searchFields={getSearchFields()}
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
            totalRecords={totalRecords}
            filterComponent={filterComponent}
          />
        </div>
      );
    }
  }
};

export default WmsSorList;
