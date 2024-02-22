import { Header, Loader } from "@egovernments/digit-ui-react-components";
import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DesktopList from "../../../../components/List/PR/DesktopList";
import MobileList from  "../../../../components/List/PR/MobileList";

const WmsPrList = ({ parentRoute, businessService = "WMS", initialStates = {}, filterComponent, isList }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { isLoading: isLoading, Errors, data: res } = Digit.Hooks.wms.pr.useWmsPrCount(tenantId);

  const { t } = useTranslation();
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [prtParams, setPrtParams] = useState(initialStates.prtParams || [{ id: "createdTime", desc: false }]);
  const [totalRecords, setTotalReacords] = useState(undefined);
  const [searchParams, setSearchParams] = useState(() => {
    return initialStates.searchParams || {};
  });

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: pageOffset, prtOrder: prtParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, prtOrder: prtParams?.[0]?.desc ? "DESC" : "ASC" };
  const isupdate = Digit.SessionStorage.get("isupdate");
  //const { PRApplications } = async () => await WMSService.PRApplications.search(tenantId, searchparams, filters);// Digit.Hooks.wms.pr.useWmsPrSearch(
  //  tenantId,
  //  searchParams,
  //  paginationParams,
  //  isupdate
  //);

  const { isLoading: hookLoading, isError, error, data:PrData, ...rest } = Digit.Hooks.wms.pr.useWmsPrSearch(
    searchParams,
    tenantId,
    paginationParams,
    isupdate
  );
  const { isLoading: getLoading, isError:getIsError, error:getError, data:GetData } = Digit.Hooks.wms.pr.useWmsPrGet();

  
  console.log(PrData,"Prrrr")

  useEffect(() => {

     setTotalReacords(PrData?.length-1);
  }, [PrData]);

 // useEffect(() => {}, [isLoading, PRApplications]);

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

  const handlePrt = useCallback((args) => {
    if (args.length === 0) return;
    setPrtParams(args);
  }, []);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };

  const getSearchFields = () => {
    return [
      {
        label: t("WMS_PR_PROJECT_NAME_LABEL"),
        name: "project_name",
        type:"text"
      },
      {
        label: t("WMS_PR_WORK_NAME_LABEL"),
        name: "work_name",
        type:"text",
      },
      {
        label: t("WMS_PR_WORK_TYPE_LABEL"),
        name: "Work_type",
        type:"text"
      },
    ];
  };

  /* if (isLoading) {
    return <Loader />;
  }
else
{
  if(PRApplications!=undefined)
  alert(JSON.stringify(PRApplications))
} */
  if (PrData?.WMSProjectRegisterApplications
 !== null) {
    if (isMobile) {
      return (
        <MobileList
          businessService={businessService}
          data={PrData?.WMSProjectRegisterApplications
}
          isLoading={isLoading}
          defaultSearchParams={initialStates.searchParams}
          isSearch={isList}
          onFilterChange={handleFilterChange}
          searchFields={getSearchFields()}
          onSearch={handleFilterChange}
          onPrt={handlePrt}
          onNextPage={fetchNextPage}
          tableConfig={rest?.tableConfig}
          onPrevPage={fetchPrevPage}
          currentPage={Math.floor(pageOffset / pageSize)}
          pageSizeLimit={pageSize}
          disablePrt={false}
          onPageSizeChange={handlePageSizeChange}
          parentRoute={parentRoute}
          searchParams={searchParams}
          prtParams={prtParams}
          totalRecords={totalRecords}
          linkPrefix={'/upyog-ui/citizen/wms/pr-details/'}
          filterComponent={filterComponent}
        />
      );
    } else {
      return (
        <div>
          {isList && <Header>{t("WMS_HOME_SEARCH_RESULTS_HEADING")}</Header>}
          <DesktopList
            businessService={businessService}
            data={PrData?.WMSProjectRegisterApplications
}
            isLoading={isLoading}
            defaultSearchParams={initialStates.searchParams}
            isSearch={isList}
            onFilterChange={handleFilterChange}
            searchFields={getSearchFields()}
            onSearch={handleFilterChange}
            onPrt={handlePrt}
            onNextPage={fetchNextPage}
            onPrevPage={fetchPrevPage}
            currentPage={Math.floor(pageOffset / pageSize)}
            pageSizeLimit={pageSize}
            disablePrt={false}
            onPageSizeChange={handlePageSizeChange}
            parentRoute={parentRoute}
            searchParams={searchParams}
            prtParams={prtParams}
            totalRecords={totalRecords}
            filterComponent={filterComponent}
          />
        </div>
      );
    }
  }
};

export default WmsPrList;
