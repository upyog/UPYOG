import React, { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Dropdown, Header, Toast } from "@upyog/digit-ui-react-components";
import { Switch, useLocation, Link } from "react-router-dom";
// import NoticeForAssesment from "./NoticeTemplates/NoticeForAssesment";
// import NoticeForRectification from "./NoticeTemplates/NoticeForRectification";
// import NoticeForReassessment from "./NoticeTemplates/NoticeForReassessment";
// import NoticeToEnterPremises from "./NoticeTemplates/NoticeToEnterPremises";
// import NoticeToFileReturn from "./NoticeTemplates/NoticeToFileReturn";
// import NoticeForHearing from "./NoticeTemplates/NoticeForHearing";
// import NoticeForImpositionOfPenalty from "./NoticeTemplates/NoticeForImpositionOfPenalty";
import NoticeForHearing from "../employee/NoticeTemplates/NoticeForHearing";
import NoticeForRectification from "../employee/NoticeTemplates/NoticeForRectification";
import NoticeForAssesment from "../employee/NoticeTemplates/NoticeForAssesment";
import NoticeForReassessment from "../employee/NoticeTemplates/NoticeForReassessment";
import NoticeToEnterPremises from "../employee/NoticeTemplates/NoticeToEnterPremises";
import NoticeForImpositionOfPenalty from "../employee/NoticeTemplates/NoticeForImpositionOfPenalty";
import NoticeToFileReturn from "../employee/NoticeTemplates/NoticeToFileReturn";

const CitizenNotice = ({
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
  const [showToast, setShowToast] = useState(null);
  const location = useLocation();

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
  const [noticeData, setNoticeData] = useState();
  const handleChangeNotice=(value)=>{
    setNotice(value)
  }
  const [isLocation, setIsLocation] = useState(false)
  if(location && location?.state && !isLocation){
    noticeList.forEach(element => {
        if(element.name== location?.state?.noticeType) {
            setNotice(element);
        }
    });
    // setNotice(noticeList[0]);
    
    setNoticeData(location?.state);
    setIsLocation(true);
  }

  const submit = async (e)=>{
    try {
      // TODO: change module in file storage
      const response = await Digit.PTService.noticeCreate({notice:e});
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

      return (
        <div>
          <div>
              {notice && notice.code=='1' && (
                <NoticeForRectification notice={notice} submit={submit} noticeData={noticeData} isCitizen={true}></NoticeForRectification>
              )}
              {notice && notice.code=='2' && (
                <NoticeForAssesment notice={notice} submit={submit} noticeData={noticeData} isCitizen={true}></NoticeForAssesment>
              )}
              {notice && notice.code=='3' && (
                <NoticeForReassessment notice={notice} submit={submit} noticeData={noticeData} isCitizen={true}></NoticeForReassessment>
              )}
              {notice && notice.code=='4' && (
                <NoticeToEnterPremises notice={notice} submit={submit} noticeData={noticeData} isCitizen={true}></NoticeToEnterPremises>
              )}
              {notice && notice.code=='5' && (
                <NoticeToFileReturn notice={notice} submit={submit} noticeData={noticeData} isCitizen={true}></NoticeToFileReturn>
              )}
              {notice && notice.code=='6' && (
                <NoticeForHearing notice={notice} submit={submit} noticeData={noticeData} isCitizen={true}></NoticeForHearing>
              )}
              {notice && notice.code=='7' && (
                <NoticeForImpositionOfPenalty notice={notice} submit={submit} isCitizen={true}></NoticeForImpositionOfPenalty>
              )}
            </div>
            {showToast && <Toast isDleteBtn={true} error={showToast?.key} label={showToast?.label} onClose={() => setShowToast(null)} />}
        </div>
      );
};

export default CitizenNotice;
