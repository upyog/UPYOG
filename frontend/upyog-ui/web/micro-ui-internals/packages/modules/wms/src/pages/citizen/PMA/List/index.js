import { Header, Loader } from "@egovernments/digit-ui-react-components";
import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import DesktopList from "../../../../components/List/PMA/DesktopList";
import MobileList from  "../../../../components/List/PMA/MobileList";

const WmsPmaList = ({ parentRoute, businessService = "WMS", initialStates = {}, filterComponent, isList }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { isLoading: isLoading, Errors, data: res } = Digit.Hooks.wms.pma.useWmsPmaCount(tenantId);

  const { t } = useTranslation();
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [pmatParams, setPmatParams] = useState(initialStates.pmatParams || [{ id: "createdTime", desc: false }]);
  const [totalRecords, setTotalReacords] = useState(undefined);
  const [searchParams, setSearchParams] = useState(() => {
    return initialStates.searchParams || {};
  });

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: pageOffset, pmatOrder: pmatParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, pmatOrder: pmatParams?.[0]?.desc ? "DESC" : "ASC" };
  const isupdate = Digit.SessionStorage.get("isupdate");
  //const { PMAApplications } = async () => await WMSService.PMAApplications.search(tenantId, searchparams, filters);// Digit.Hooks.wms.pma.useWmsPmaSearch(
    const { isLoading: hookLoading, isError, error, data:PmaData, ...rest } = Digit.Hooks.wms.phm.useWmsPmaSearch(
      searchParams,
      tenantId,
      paginationParams,
      isupdate
    );

const { isLoading: getLoading, isError:getIsError, error:getError, data:GetData } = Digit.Hooks.wms.pma.useWmsPmaGet();
console.log("GetData ddd ",GetData)
console.log("GetData ddd WMSPhysicalMileStoneActivityApplication  ",GetData?.WMSPhysicalMileStoneActivityApplication)

  useEffect(() => {
     setTotalReacords(res?.length-1);
  }, [res]);
//  useEffect(() => {setTotalReacords(PMAApplications?.length-1);}, [PMAApplications]);

  useEffect(() => {}, [hookLoading, rest]);

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

  const handlePmat = useCallback((args) => {
    if (args.length === 0) return;
    setPmatParams(args);
  }, []);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };

  const getSearchFields = () => {
    return [
      {
        label: t("WMS_PMA_DESC_OF_ITEM_LABEL"),
        name: "description_of_item",
        type:"text"
      },
      {
        label: t("WMS_PMA_START_DATE_LABEL"),
        name: "start_date",
        type:"date",
      },
      {
        label: t("WMS_PMA_END_DATE_LABEL"),
        name: "end_date",
        type:"date"
      },
    ];
  };

  if (isLoading) {
    return <Loader />;
  }
/*else
{
  if(PMAApplications!=undefined)
  alert(JSON.stringify(PMAApplications))
} */
  //if (PMAApplications?.length !== null) {
  if (PmaData?.WMSPhysicalMileStoneActivityApplication.length !== null) {
    if (isMobile) {
      return (
        <MobileList
          businessService={businessService}
          //data={PMAApplications}
          //isLoading={isLoading}
          
          data={PmaData?.WMSPhysicalMileStoneActivityApplication}
          isLoading={hookLoading}
          defaultSearchParams={initialStates.searchParams}
          isSearch={isList}
          onFilterChange={handleFilterChange}
          searchFields={getSearchFields()}
          onSearch={handleFilterChange}
          onPmat={handlePmat}
          onNextPage={fetchNextPage}
          tableConfig={rest?.tableConfig}
          onPrevPage={fetchPrevPage}
          currentPage={Math.floor(pageOffset / pageSize)}
          pageSizeLimit={pageSize}
          disablePmat={false}
          onPageSizeChange={handlePageSizeChange}
          parentRoute={parentRoute}
          searchParams={searchParams}
          pmatParams={pmatParams}
          totalRecords={totalRecords}
          linkPrefix={'/upyog-ui/citizen/wms/pma-details/'}
          filterComponent={filterComponent}
        />
      );
    } else {
      return (
        <div>
          {isList && <Header>{t("WMS_HOME_SEARCH_RESULTS_HEADING")}</Header>}
          <DesktopList
            businessService={businessService}
            //data={PMAApplications}
            //isLoading={isLoading}
            data={PmaData?.WMSPhysicalMileStoneActivityApplication}
            isLoading={hookLoading}
            defaultSearchParams={initialStates.searchParams}
            isSearch={isList}
            onFilterChange={handleFilterChange}
            searchFields={getSearchFields()}
            onSearch={handleFilterChange}
            onPmat={handlePmat}
            onNextPage={fetchNextPage}
            onPrevPage={fetchPrevPage}
            currentPage={Math.floor(pageOffset / pageSize)}
            pageSizeLimit={pageSize}
            disablePmat={false}
            onPageSizeChange={handlePageSizeChange}
            parentRoute={parentRoute}
            searchParams={searchParams}
            pmatParams={pmatParams}
            totalRecords={totalRecords}
            filterComponent={filterComponent}
          />
        </div>
      );
    }
  }
};

export default WmsPmaList;
