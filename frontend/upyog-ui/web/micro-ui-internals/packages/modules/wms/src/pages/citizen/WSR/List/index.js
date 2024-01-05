import { Header, Loader } from "@egovernments/digit-ui-react-components";
import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DesktopList from "../../../../components/List/WSR/DesktopList";
import MobileList from  "../../../../components/List/WSR/MobileList";

const WmsWsrList = ({ parentRoute, businessService = "WMS", initialStates = {}, filterComponent, isList }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { isLoading: isLoading, Errors, data: res } = Digit.Hooks.wms.dr.useWmsWsrCount(tenantId);

  const { t } = useTranslation();
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [drtParams, setWsrtParams] = useState(initialStates.drtParams || [{ id: "createdTime", desc: false }]);
  const [totalRecords, setTotalReacords] = useState(undefined);
  const [searchParams, setSearchParams] = useState(() => {
    return initialStates.searchParams || {};
  });

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: pageOffset, drtOrder: drtParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, drtOrder: drtParams?.[0]?.desc ? "DESC" : "ASC" };
  const isupdate = Digit.SessionStorage.get("isupdate");
  //const { WSRApplications } = async () => await WMSService.WSRApplications.search(tenantId, searchparams, filters);// Digit.Hooks.wms.dr.useWmsWsrSearch(
  //  tenantId,
  //  searchParams,
  //  paginationParams,
  //  isupdate
  //);

  const { isLoading: hookLoading, isError, error, data:WsrData, ...rest } = Digit.Hooks.wms.dr.useWmsWsrSearch(
    searchParams,
    tenantId,
    paginationParams,
    isupdate
  );


  useEffect(() => {

     setTotalReacords(WsrData?.length-1);
  }, [WsrData]);

 // useEffect(() => {}, [isLoading, WSRApplications]);

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

  const handleWsrt = useCallback((args) => {
    if (args.length === 0) return;
    setWsrtParams(args);
  }, []);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };

  const getSearchFields = () => {
    return [
      {
        label: t("WMS_WSR_PROJECT_NAME_LABEL"),
        name: "project_name",
        type:"text"
      },
      {
        label: t("WMS_WSR_WORK_NAME_LABEL"),
        name: "work_name",
        type:"text",
      },
      {
        label: t("WMS_WSR_ML_NAME_LABEL"),
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
  if(WSRApplications!=undefined)
  alert(JSON.stringify(WSRApplications))
} */
  if (WsrData?.length !== null) {
    if (isMobile) {
      return (
        <MobileList
          businessService={businessService}
          data={WsrData}
          isLoading={isLoading}
          defaultSearchParams={initialStates.searchParams}
          isSearch={isList}
          onFilterChange={handleFilterChange}
          searchFields={getSearchFields()}
          onSearch={handleFilterChange}
          onWsrt={handleWsrt}
          onNextPage={fetchNextPage}
          tableConfig={rest?.tableConfig}
          onPrevPage={fetchPrevPage}
          currentPage={Math.floor(pageOffset / pageSize)}
          pageSizeLimit={pageSize}
          disableWsrt={false}
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
            data={WsrData}
            isLoading={isLoading}
            defaultSearchParams={initialStates.searchParams}
            isSearch={isList}
            onFilterChange={handleFilterChange}
            searchFields={getSearchFields()}
            onSearch={handleFilterChange}
            onWsrt={handleWsrt}
            onNextPage={fetchNextPage}
            onPrevPage={fetchPrevPage}
            currentPage={Math.floor(pageOffset / pageSize)}
            pageSizeLimit={pageSize}
            disableWsrt={false}
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

export default WmsWsrList;
