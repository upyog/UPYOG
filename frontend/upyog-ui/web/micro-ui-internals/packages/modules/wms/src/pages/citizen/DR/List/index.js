import { Header, Loader } from "@egovernments/digit-ui-react-components";
import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DesktopList from "../../../../components/List/DR/DesktopList";
import MobileList from  "../../../../components/List/DR/MobileList";

const WmsDrList = ({ parentRoute, businessService = "WMS", initialStates = {}, filterComponent, isList }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { isLoading: isLoading, Errors, data: res } = Digit.Hooks.wms.dr.useWmsDrCount(tenantId);

  const { t } = useTranslation();
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [drtParams, setDrtParams] = useState(initialStates.drtParams || [{ id: "createdTime", desc: false }]);
  const [totalRecords, setTotalReacords] = useState(undefined);
  const [searchParams, setSearchParams] = useState(() => {
    return initialStates.searchParams || {};
  });

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: pageOffset, drtOrder: drtParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, drtOrder: drtParams?.[0]?.desc ? "DESC" : "ASC" };
  const isupdate = Digit.SessionStorage.get("isupdate");
  //const { DRApplications } = async () => await WMSService.DRApplications.search(tenantId, searchparams, filters);// Digit.Hooks.wms.dr.useWmsDrSearch(
  //  tenantId,
  //  searchParams,
  //  paginationParams,
  //  isupdate
  //);

  const { isLoading: hookLoading, isError, error, data:DrData, ...rest } = Digit.Hooks.wms.dr.useWmsDrSearch(
    searchParams,
    tenantId,
    paginationParams,
    isupdate
  );


  useEffect(() => {

     setTotalReacords(DrData?.length-1);
  }, [DrData]);

 // useEffect(() => {}, [isLoading, DRApplications]);

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

  const handleDrt = useCallback((args) => {
    if (args.length === 0) return;
    setDrtParams(args);
  }, []);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };

  const getSearchFields = () => {
    return [
      {
        label: t("WMS_DR_PROJECT_NAME_LABEL"),
        name: "project_name",
        type:"text"
      },
      {
        label: t("WMS_DR_WORK_NAME_LABEL"),
        name: "work_name",
        type:"text",
      },
      {
        label: t("WMS_DR_ML_NAME_LABEL"),
        name: "milestone_name",
        type:"text"
      },
    ];
  };

  /* if (isLoading) {
    return <Loader />;
  }
else
{
  if(DRApplications!=undefined)
  alert(JSON.stringify(DRApplications))
} */
  if (DrData?.length !== null) {
    if (isMobile) {
      return (
        <MobileList
          businessService={businessService}
          data={DrData}
          isLoading={isLoading}
          defaultSearchParams={initialStates.searchParams}
          isSearch={isList}
          onFilterChange={handleFilterChange}
          searchFields={getSearchFields()}
          onSearch={handleFilterChange}
          onDrt={handleDrt}
          onNextPage={fetchNextPage}
          tableConfig={rest?.tableConfig}
          onPrevPage={fetchPrevPage}
          currentPage={Math.floor(pageOffset / pageSize)}
          pageSizeLimit={pageSize}
          disableDrt={false}
          onPageSizeChange={handlePageSizeChange}
          parentRoute={parentRoute}
          searchParams={searchParams}
          drtParams={drtParams}
          totalRecords={totalRecords}
          linkPrefix={'/upyog-ui/citizen/wms/dr-details/'}
          filterComponent={filterComponent}
        />
      );
    } else {
      return (
        <div>
          {isList && <Header>{t("WMS_HOME_SEARCH_RESULTS_HEADING")}</Header>}
          <DesktopList
            businessService={businessService}
            data={DrData}
            isLoading={isLoading}
            defaultSearchParams={initialStates.searchParams}
            isSearch={isList}
            onFilterChange={handleFilterChange}
            searchFields={getSearchFields()}
            onSearch={handleFilterChange}
            onDrt={handleDrt}
            onNextPage={fetchNextPage}
            onPrevPage={fetchPrevPage}
            currentPage={Math.floor(pageOffset / pageSize)}
            pageSizeLimit={pageSize}
            disableDrt={false}
            onPageSizeChange={handlePageSizeChange}
            parentRoute={parentRoute}
            searchParams={searchParams}
            drtParams={drtParams}
            totalRecords={totalRecords}
            filterComponent={filterComponent}
          />
        </div>
      );
    }
  }
};

export default WmsDrList;
