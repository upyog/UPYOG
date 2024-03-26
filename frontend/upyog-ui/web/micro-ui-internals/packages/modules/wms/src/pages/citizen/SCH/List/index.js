import { Header, Loader } from "@egovernments/digit-ui-react-components";
import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DesktopList from "../../../../components/List/SCH/DesktopList";
import MobileList from  "../../../../components/List/SCH/MobileList";

const WmsSchList = ({ parentRoute, businessService = "WMS", initialStates = {}, filterComponent, isList }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { isLoading: isLoading, Errors, data: res } = Digit.Hooks.wms.sch.useWmsSchCount(tenantId);

  const { t } = useTranslation();
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [schtParams, setSchtParams] = useState(initialStates.schtParams || [{ id: "createdTime", desc: false }]);
  const [totalRecords, setTotalReacords] = useState(undefined);
  const [searchParams, setSearchParams] = useState(() => {
    return initialStates.searchParams || {};
  });

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: pageOffset, schtOrder: schtParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, schtOrder: schtParams?.[0]?.desc ? "DESC" : "ASC" };
  const isupdate = Digit.SessionStorage.get("isupdate");
  //const  SCHApplications  = async () => await WMSService.SCHApplications.search(tenantId, searchparams, filters);// Digit.Hooks.wms.sch.useWmsSchSearch(
  //  tenantId,
  //  searchParams,
  //  paginationParams,
  //  isupdate
  //);
  const  SCHApplications  = Digit.Hooks.wms.sch.useWmsSchSearch(
     tenantId,
     searchParams,
     paginationParams,
     isupdate
    );
  useEffect(() => {

     setTotalReacords(SCHApplications?.length-1);
  }, [SCHApplications]);

 // useEffect(() => {}, [isLoading, SCHApplications]);

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

  const handleScht = useCallback((args) => {
    if (args.length === 0) return;
    setSchtParams(args);
  }, []);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };

  const getSearchFields = () => {
    return [
      {
        label: t("WMS_SCH_NAME_EN_LABEL"),
        name: "scheme_name_en",
        type:"text"
      },
      {
        label: t("WMS_SCH_START_DATE_LABEL"),
        name: "start_date",
        type:"date",
      },
      {
        label: t("WMS_SCH_END_DATE_LABEL"),
        name: "end_date",
        type:"date"
      },
    ];
  };

  /* if (isLoading) {
    return <Loader />;
  }
else
{
  if(SCHApplications!=undefined)
  alert(JSON.stringify(SCHApplications))
} */
  if (SCHApplications?.length !== null) {
    if (isMobile) {
      return (
        <MobileList
          businessService={businessService}
          data={SCHApplications}
          isLoading={isLoading}
          defaultSearchParams={initialStates.searchParams}
          isSearch={isList}
          onFilterChange={handleFilterChange}
          searchFields={getSearchFields()}
          onSearch={handleFilterChange}
          onScht={handleScht}
          onNextPage={fetchNextPage}
          tableConfig={rest?.tableConfig}
          onPrevPage={fetchPrevPage}
          currentPage={Math.floor(pageOffset / pageSize)}
          pageSizeLimit={pageSize}
          disableScht={false}
          onPageSizeChange={handlePageSizeChange}
          parentRoute={parentRoute}
          searchParams={searchParams}
          schtParams={schtParams}
          totalRecords={totalRecords}
          linkPrefix={'/upyog-ui/citizen/wms/sch-details/'}
          filterComponent={filterComponent}
        />
      );
    } else {
      return (
        <div>
          {isList && <Header>{t("WMS_HOME_SEARCH_RESULTS_HEADING")}</Header>}
          <DesktopList
            businessService={businessService}
            data={SCHApplications}
            isLoading={isLoading}
            defaultSearchParams={initialStates.searchParams}
            isSearch={isList}
            onFilterChange={handleFilterChange}
            searchFields={getSearchFields()}
            onSearch={handleFilterChange}
            onScht={handleScht}
            onNextPage={fetchNextPage}
            onPrevPage={fetchPrevPage}
            currentPage={Math.floor(pageOffset / pageSize)}
            pageSizeLimit={pageSize}
            disableScht={false}
            onPageSizeChange={handlePageSizeChange}
            parentRoute={parentRoute}
            searchParams={searchParams}
            schtParams={schtParams}
            totalRecords={totalRecords}
            filterComponent={filterComponent}
          />
        </div>
      );
    }
  }
};

export default WmsSchList;
