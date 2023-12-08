import { Header, Loader } from "@egovernments/digit-ui-react-components";
import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DesktopList from "../../../../components/List/MB/DesktopList";
import MobileList from  "../../../../components/List/MB/MobileList";

const WmsMbList = ({ parentRoute, businessService = "WMS", initialStates = {}, filterComponent, isList }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { isLoading: isLoading, Errors, data: res } = Digit.Hooks.wms.mb.useWmsMbCount(tenantId);


  const { t } = useTranslation();
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [mbtParams, setMbtParams] = useState(initialStates.mbtParams || [{ id: "createdTime", desc: false }]);
  const [totalRecords, setTotalReacords] = useState(undefined);
  const [searchParams, setSearchParams] = useState(() => {
    return initialStates.searchParams || {};
  });

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: pageOffset, mbtOrder: mbtParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, mbtOrder: mbtParams?.[0]?.desc ? "DESC" : "ASC" };
  const isupdate = Digit.SessionStorage.get("isupdate");
  //const { MBApplications } = async () => await WMSService.MBApplications.search(tenantId, searchparams, filters);// Digit.Hooks.wms.mb.useWmsMbSearch(
  //  tenantId,
  //  searchParams,
  //  paginationParams,
  //  isupdate
  //);

  const { isLoading: hookLoading, isError, error, data:MbData, ...rest } = Digit.Hooks.wms.mb.useWmsMbSearch(
    searchParams,
    tenantId,
    paginationParams,
    isupdate
  );


  useEffect(() => {

     setTotalReacords(MbData?.length-1);
  }, [MbData]);

 // useEffect(() => {}, [isLoading, MBApplications]);

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

  const handleMbt = useCallback((args) => {
    if (args.length === 0) return;
    setMbtParams(args);
  }, []);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };

  const getSearchFields = () => {
    return [
      {
        label: t("WMS_MB_NAME_LABEL"),
        name: "mb_name",
        type:"name"
      },
      {
        label: t("WMS_MB_DATE_LABEL"),
        name: "mb_date",
        type:"date",
      },
    ];
  };

  /* if (isLoading) {
    return <Loader />;
  }
else
{
  if(MBApplications!=undefined)
  alert(JSON.stringify(MBApplications))
} */
  if (MbData?.length !== null) {
    if (isMobile) {
      return (
        <MobileList
          businessService={businessService}
          data={MbData}
          isLoading={isLoading}
          defaultSearchParams={initialStates.searchParams}
          isSearch={isList}
          onFilterChange={handleFilterChange}
          searchFields={getSearchFields()}
          onSearch={handleFilterChange}
          onMbt={handleMbt}
          onNextPage={fetchNextPage}
          tableConfig={rest?.tableConfig}
          onPrevPage={fetchPrevPage}
          currentPage={Math.floor(pageOffset / pageSize)}
          pageSizeLimit={pageSize}
          disableMbt={false}
          onPageSizeChange={handlePageSizeChange}
          parentRoute={parentRoute}
          searchParams={searchParams}
          mbtParams={mbtParams}
          totalRecords={totalRecords}
          linkPrefix={'/upyog-ui/citizen/wms/mb-details/'}
          filterComponent={filterComponent}
        />
      );
    } else {
      return (
        <div>
          {isList && <Header>{t("WMS_HOME_SEARCH_RESULTS_HEADING")}</Header>}
          <DesktopList
            businessService={businessService}
            data={MbData}
            isLoading={isLoading}
            defaultSearchParams={initialStates.searchParams}
            isSearch={isList}
            onFilterChange={handleFilterChange}
            searchFields={getSearchFields()}
            onSearch={handleFilterChange}
            onMbt={handleMbt}
            onNextPage={fetchNextPage}
            onPrevPage={fetchPrevPage}
            currentPage={Math.floor(pageOffset / pageSize)}
            pageSizeLimit={pageSize}
            disableMbt={false}
            onPageSizeChange={handlePageSizeChange}
            parentRoute={parentRoute}
            searchParams={searchParams}
            mbtParams={mbtParams}
            totalRecords={totalRecords}
            filterComponent={filterComponent}
          />
        </div>
      );
    }
  }
};

export default WmsMbList;
