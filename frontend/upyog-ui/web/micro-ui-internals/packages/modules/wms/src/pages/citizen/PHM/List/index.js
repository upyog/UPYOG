import { Header, Loader } from "@egovernments/digit-ui-react-components";
import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DesktopList from "../../../../components/List/PHM/DesktopList";
import MobileList from  "../../../../components/List/PHM/MobileList";

const WmsPhmList = ({ parentRoute, businessService = "WMS", initialStates = {}, filterComponent, isList }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { isLoading: isLoading, Errors, data: res } = Digit.Hooks.wms.phm.useWmsPhmCount(tenantId);

  const { t } = useTranslation();
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [phmtParams, setPhmtParams] = useState(initialStates.phmtParams || [{ id: "createdTime", desc: false }]);
  const [totalRecords, setTotalReacords] = useState(undefined);
  const [searchParams, setSearchParams] = useState(() => {
    return initialStates.searchParams || {};
  });

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: pageOffset, phmtOrder: phmtParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, phmtOrder: phmtParams?.[0]?.desc ? "DESC" : "ASC" };
  const isupdate = Digit.SessionStorage.get("isupdate");
  //const { PHMApplications } = async () => await WMSService.PHMApplications.search(tenantId, searchparams, filters);// Digit.Hooks.wms.phm.useWmsPhmSearch(
  //  tenantId,
  //  searchParams,
  //  paginationParams,
  //  isupdate
  //);

  const { isLoading: hookLoading, isError, error, data:PhmData, ...rest } = Digit.Hooks.wms.phm.useWmsPhmSearch(
    searchParams,
    tenantId,
    paginationParams,
    isupdate
  );
  const { isLoading: getLoading, isError:getIsError, error:getError, data:GetData } = Digit.Hooks.wms.phm.useWmsPhmGet();
console.log("GetData ddd ",GetData)
console.log("GetData ddd WMSPhysicalFinancialMilestoneApplications  ",GetData?.WMSPhysicalFinancialMilestoneApplications)

  useEffect(() => {

     setTotalReacords(PhmData?.length-1);
  }, [PhmData]);

 // useEffect(() => {}, [isLoading, PHMApplications]);

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

  const handlePhmt = useCallback((args) => {
    if (args.length === 0) return;
    setPhmtParams(args);
  }, []);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };

  const getSearchFields = () => {
    return [
      {
        label: t("WMS_PHM_PROJECT_NAME_LABEL"),
        name: "project_name",
        type:"text"
      },
      {
        label: t("WMS_PHM_WORK_NAME_LABEL"),
        name: "work_name",
        type:"text",
      },
      {
        label: t("WMS_PHM_ML_NAME_LABEL"),
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
  if(PHMApplications!=undefined)
  alert(JSON.stringify(PHMApplications))
} */

// console.log(phmData , "phm data hai ye");
  if (PhmData?.WMSPhysicalFinancialMilestoneApplications.length !== null) {
    if (isMobile) {
      return (
        <MobileList
          businessService={businessService}
          data={PhmData?.WMSPhysicalFinancialMilestoneApplications}
          isLoading={isLoading}
          defaultSearchParams={initialStates.searchParams}
          isSearch={isList}
          onFilterChange={handleFilterChange}
          searchFields={getSearchFields()}
          onSearch={handleFilterChange}
          onPhmt={handlePhmt}
          onNextPage={fetchNextPage}
          tableConfig={rest?.tableConfig}
          onPrevPage={fetchPrevPage}
          currentPage={Math.floor(pageOffset / pageSize)}
          pageSizeLimit={pageSize}
          disablePhmt={false}
          onPageSizeChange={handlePageSizeChange}
          parentRoute={parentRoute}
          searchParams={searchParams}
          phmtParams={phmtParams}
          totalRecords={totalRecords}
          linkPrefix={'/upyog-ui/citizen/wms/phm-details/'}
          filterComponent={filterComponent}
        />
      );
    } else {
      return (
        <div>
          {isList && <Header>{t("WMS_HOME_SEARCH_RESULTS_HEADING")}</Header>}
          <DesktopList
            businessService={businessService}
            data={PhmData?.WMSPhysicalFinancialMilestoneApplications}
            isLoading={isLoading}
            defaultSearchParams={initialStates.searchParams}
            isSearch={isList}
            onFilterChange={handleFilterChange}
            searchFields={getSearchFields()}
            onSearch={handleFilterChange}
            onPhmt={handlePhmt}
            onNextPage={fetchNextPage}
            onPrevPage={fetchPrevPage}
            currentPage={Math.floor(pageOffset / pageSize)}
            pageSizeLimit={pageSize}
            disablePhmt={false}
            onPageSizeChange={handlePageSizeChange}
            parentRoute={parentRoute}
            searchParams={searchParams}
            phmtParams={phmtParams}
            totalRecords={totalRecords}
            filterComponent={filterComponent}
          />
        </div>
      );
    }
  }
};

export default WmsPhmList;
