import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Dropdown, Header, Toast } from "@egovernments/digit-ui-react-components";

import DesktopInbox from "../../components/DesktopInbox";
import MobileInbox from "../../components/MobileInbox";
import NoticeForAssesment from "./NoticeTemplates/NoticeForAssesment";
import NoticeForRectification from "./NoticeTemplates/NoticeForRectification";
import NoticeForReassessment from "./NoticeTemplates/NoticeForReassessment";
import NoticeToEnterPremises from "./NoticeTemplates/NoticeToEnterPremises";
import NoticeToFileReturn from "./NoticeTemplates/NoticeToFileReturn";
import NoticeForHearing from "./NoticeTemplates/NoticeForHearing";
import NoticeForImpositionOfPenalty from "./NoticeTemplates/NoticeForImpositionOfPenalty";

const Notices = ({
  useNewInboxAPI,
  parentRoute,
  moduleCode = "PT",
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
  const [TableConfig, setTableConfig] = useState(() => Digit.ComponentRegistryService?.getComponent("PTInboxTableConfig"));
  // const [getSearchFi]
  const [pageOffset, setPageOffset] = useState(initialStates.pageOffset || 0);
  const [pageSize, setPageSize] = useState(initialStates.pageSize || 10);
  const [sortParams, setSortParams] = useState(initialStates.sortParams || [{ id: "createdTime", desc: false }]);
  const [searchParams, setSearchParams] = useState(initialStates.searchParams || {});
  const [showToast, setShowToast] = useState(null)

  let isMobile = window.Digit.Utils.browser.isMobile();
  let paginationParams = isMobile
    ? { limit: 100, offset: 0, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" }
    : { limit: pageSize, offset: pageOffset, sortBy: sortParams?.[0]?.id, sortOrder: sortParams?.[0]?.desc ? "DESC" : "ASC" };

  const { isFetching, isLoading: hookLoading, searchResponseKey, data, searchFields, ...rest } = useNewInboxAPI
    ? Digit.Hooks.useNewInboxGeneral({
        tenantId,
        ModuleCode: moduleCode,
        filters: { ...searchParams, ...paginationParams, sortParams },
      })
    : Digit.Hooks.useInboxGeneral({
        tenantId,
        businessService: moduleCode,
        isInbox,
        filters: { ...searchParams, ...paginationParams, sortParams },
        rawWfHandler,
        rawSearchHandler,
        combineResponse,
        wfConfig,
        searchConfig: { ...enableSarch, ...searchConfig },
        middlewaresWf,
        middlewareSearch,
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

  const handleSort = useCallback((args) => {
    if (args.length === 0) return;
    setSortParams(args);
  }, []);

  const handlePageSizeChange = (e) => {
    setPageSize(Number(e.target.value));
  };
  const noticeList = [
    {code: '1', name: 'Notice for rectification of mistakes in a Defective Return'},
    {code: '2', name: 'Notice for Assessment'},
    {code: '3', name: 'Notice for Re-Assessment'},
    {code: '4', name: 'Notice to enter Premises'},
    {code: '5', name: 'Notice to file Return'},
    {code: '6', name: 'Notice for Hearing under Rule 39 / 40'},
    {code: '7', name: 'Notice for Imposition of Penalty'}
  ]
  const [notice, setNotice] = useState();
  const handleChangeNotice=(value)=>{
    setNotice(value)
  }

  const submit = async (e)=>{
    console.log("submit===",e)
    try {
      // TODO: change module in file storage
      const response = await Digit.PTService.noticeCreate({notice:e});
      console.log("response==",response)
      if (response) {
        setShowToast({
          key: false,
          label: `${t("Notice Generated Successfully.")}`
        })
      } else {
          setShowToast({
            key: true,
            label: `${t("Something wrong!!!")}`
          })
      }
    } catch (err) {
      setShowToast({
        key: true,
        label: `${t("Something wrong!!!")}`
      })
    }
  }

  if (rest?.data?.length !== null) {
    if (isMobile) {
      return (
        <MobileInbox
          data={data}
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
          tableConfig={rest?.tableConfig?res?.tableConfig:TableConfig(t)["PT"]}
          filterComponent={filterComponent}
          EmptyResultInboxComp={EmptyResultInboxComp}
          useNewInboxAPI={useNewInboxAPI}
        />
        // <div></div>
      );
    } else {
      return (
        <div>
          {isInbox && <Header>{t("NOTICES")}</Header>}
          <div className="card" style={{maxWidth: "100%"}}>
            <div>
              <Dropdown
                option={noticeList}
                selected={notice}
                optionKey={"name"}
                select={handleChangeNotice}
                freeze={true}
              />
            </div>
            
          </div>
          <div>
              {notice && notice.code=='1' && (
                <NoticeForRectification notice={notice} submit={submit}></NoticeForRectification>
              )}
              {notice && notice.code=='2' && (
                <NoticeForAssesment notice={notice} submit={submit}></NoticeForAssesment>
              )}
              {notice && notice.code=='3' && (
                <NoticeForReassessment notice={notice} submit={submit}></NoticeForReassessment>
              )}
              {notice && notice.code=='4' && (
                <NoticeToEnterPremises notice={notice} submit={submit}></NoticeToEnterPremises>
              )}
              {notice && notice.code=='5' && (
                <NoticeToFileReturn notice={notice} submit={submit}></NoticeToFileReturn>
              )}
              {notice && notice.code=='6' && (
                <NoticeForHearing notice={notice} submit={submit}></NoticeForHearing>
              )}
              {notice && notice.code=='7' && (
                <NoticeForImpositionOfPenalty notice={notice} submit={submit}></NoticeForImpositionOfPenalty>
              )}
            </div>
            {showToast && <Toast isDleteBtn={true} error={showToast?.key} label={showToast?.label} onClose={() => setShowToast(null)} />}
        </div>
      );
    }
  }
};

export default Notices;
