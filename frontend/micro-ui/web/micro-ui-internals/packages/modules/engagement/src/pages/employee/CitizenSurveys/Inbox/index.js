import React, { Fragment, useCallback, useMemo, useReducer } from "react"
import { InboxComposer, DocumentIcon } from "@egovernments/digit-ui-react-components";
import { useTranslation } from "react-i18next";
import FilterFormFieldsComponent from "./FilterFieldsComponent";
import SearchFormFieldsComponents from "./SearchFieldsComponents";
import useInboxTableConfig from "./useInboxTableConfig";
import useInboxMobileCardsData from "./useInboxMobileDataCard";
// import { useHistory } from "react-router-dom";
const Inbox = ({ parentRoute }) => {

  const { t } = useTranslation()
  // const history = useHistory()
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const ulbs = Digit.SessionStorage.get("ENGAGEMENT_TENANTS");
  const userInfo = Digit.UserService.getUser().info;
  const userUlbs = ulbs
    .filter((ulb) => userInfo?.roles?.some((role) => role?.tenantId === ulb?.code))
    
  const statuses = [
    { code: "All", name: `${t("ES_COMMON_ALL")}` },
    { code: "Active", name: `${t("ES_COMMON_ACTIVE")}` },
    { code: "Inactive", name: `${t("ES_COMMON_INACTIVE")}` }
  ]

  const isActive = (startDate, endDate) => {
    const currentDate = new Date().getTime();
    if (startDate < currentDate && currentDate <= endDate) {
      return true;
    }
    return false;
  };

  const searchFormDefaultValues = {
    // tenantIds: tenantId,
    tenantIds:userUlbs[0],
    postedBy: "",
    title: ""
  }

  const filterFormDefaultValues = {
    status: statuses[0]
  }
  const tableOrderFormDefaultValues = {
    sortBy: "",
    limit: 10,
    offset: 0,
    sortOrder: "DESC"
  }

  const ServiceDefinitionCriteria =  {
    "tenantId": tenantId,
    "code": [],
    "module": ["engagement"],
    "postedBy":""
  }

  function formReducer(state, payload) {
    switch (payload.action) {
      case "mutateSearchForm":
        Digit.SessionStorage.set("CITIZENSURVEY.INBOX", { ...state, searchForm: payload.data , ServiceDefinitionCriteria:{
          "code": [],
          "postedBy": null,
          "module": ["engagement"],
        }})
        return { ...state, searchForm: payload.data,ServiceDefinitionCriteria: {"tenantId": tenantId,
        "code": [],
        "postedBy": null,
        "module": ["engagement"],
      }  };
      case "mutateFilterForm":
        // Digit.SessionStorage.set("CITIZENSURVEY.INBOX", { ...state, filterForm: payload.data })
        // return { ...state, filterForm: payload.data };
        Digit.SessionStorage.set("CITIZENSURVEY.INBOX", { ...state, filterForm: payload.data, ServiceDefinitionCriteria:{
          "code": [state.searchForm.title],
          "postedBy": state.searchForm.postedBy,
          "module": ["engagement"],
          "status": payload.data.status.code
        }})
        return { ...state, filterForm: payload.data, ServiceDefinitionCriteria: {"tenantId": tenantId,
          "code": [state.searchForm.title],
          "postedBy": state.searchForm.postedBy,
          "module": ["engagement"],
          "status": payload.data.status.code
        } };
      case "mutateTableForm":
        Digit.SessionStorage.set("CITIZENSURVEY.INBOX", { ...state, tableForm: payload.data })
        return { ...state, tableForm: payload.data };
      case "mutateSearchDefinationForm":
        Digit.SessionStorage.set("CITIZENSURVEY.INBOX", { ...state, searchForm: payload.data, ServiceDefinitionCriteria: {"tenantId": tenantId,
          "code": [payload.data.title],
          "postedBy": payload.data.postedBy,
          "module": ["engagement"]} })
        return { ...state, searchForm: payload.data, ServiceDefinitionCriteria: {"tenantId": tenantId,
          "code": [payload.data.title],
          "postedBy": payload.data.postedBy,
          "module": ["engagement"]} };
      default:
        break;
    }
  }
  const InboxObjectInSessionStorage = Digit.SessionStorage.get("CITIZENSURVEY.INBOX")
  const onSearchFormReset = (setSearchFormValue) => {
    setSearchFormValue("postedBy", "")
    setSearchFormValue("title", "")
    setSearchFormValue("tenantIds", tenantId)
    dispatch({ action: "mutateSearchForm", data: searchFormDefaultValues })
  }

  const onFilterFormReset = (setFilterFormValue) => {
    setFilterFormValue("status", statuses[0])
    dispatch({ action: "mutateFilterForm", data: filterFormDefaultValues })
  }

  const formInitValue = useMemo(() => {
    return InboxObjectInSessionStorage || {
      filterForm: filterFormDefaultValues,
      searchForm: searchFormDefaultValues,
      tableForm: tableOrderFormDefaultValues,
      ServiceDefinitionCriteria:ServiceDefinitionCriteria,
    }
  }
    , [Object.values(InboxObjectInSessionStorage?.ServiceDefinitionCriteria || {}),Object.values(InboxObjectInSessionStorage?.filterForm || {}), Object.values(InboxObjectInSessionStorage?.searchForm || {}), Object.values(InboxObjectInSessionStorage?.tableForm || {})])

  const [formState, dispatch] = useReducer(formReducer, formInitValue)

  const onPageSizeChange = (e) => {
    dispatch({ action: "mutateTableForm", data: { ...formState.tableForm, limit: e.target.value } })
  }

  let ServiceCriteria = {
    tenantId: userUlbs[0].code,
    ids: [],
    serviceDefIds: [],
    referenceIds: [],
  }
  const { data: selecedSurveyData } = Digit.Hooks.survey.useSelectedSurveySearch({ServiceCriteria},{})
  
  // const { data: { Surveys, TotalCount } = {}, isLoading: isInboxLoading, } = Digit.Hooks.survey.useSurveyInbox(formState)
  const { data: surveyList, isLoading: isSurveyListLoading } = Digit.Hooks.survey.useCfdefinitionsearch({ServiceDefinitionCriteria:formState.ServiceDefinitionCriteria, Pagination:formState.tableForm})
  
  if (surveyList?.ServiceDefinition){
    surveyList?.ServiceDefinition.map((element,index)=>{
      element.uuid = element.code;
      element.tenantIds = null;
      element.title = element.code;
      element.status = isActive(element.additionalDetails.startDate,element.additionalDetails.endDate)?"ACTIVE":"INACTIVE";;
      element.description = element.additionalDetails.description;
      element.startDate = element.additionalDetails.startDate || null;
      element.endDate = element.additionalDetails.endDate || null;
      element.active = true;
      element.insertQuestionsForUpdate = null;
      element.postedBy = element.postedBy || "";
      element.answersCount = 0;
      element.hasResponded = "";

    })
  }

  ( async() => {
    if (surveyList && selecedSurveyData){
      surveyList?.ServiceDefinition?.map((element)=>{
        selecedSurveyData?.Service?.map((ele)=>{
          if (element.code === ele.referenceId){
            element.answersCount ++;
          }
        })
      })
    }
    

  })();


  const PropsForInboxLinks = {
    logoIcon: <DocumentIcon />,
    headerText: "CS_COMMON_SURVEYS",
    links: [{
      text: t("CS_COMMON_NEW_SURVEY"),
      link: "/digit-ui/employee/engagement/surveys/inbox/create",
    }]
  }

  const SearchFormFields = useCallback(({ registerRef, searchFormState, controlSearchForm }) => <SearchFormFieldsComponents {...{ registerRef, searchFormState, controlSearchForm }} />, [])

  const FilterFormFields = useCallback(
    ({ registerRef, controlFilterForm, setFilterFormValue, getFilterFormValue }) => <FilterFormFieldsComponent
      {...{
        statuses,
        registerRef,
        controlFilterForm,
        setFilterFormValue,
        filterFormState: formState?.filterForm,
        getFilterFormValue,
      }} />
    , [statuses])

    
  const onSearchFormSubmit = (data) => {
    //setting the offset to 0(In case searched from page other than 1)
    // dispatch({ action: "mutateTableForm", data: { ...formState.tableForm, offset:0 } })

    // data.hasOwnProperty("") ? delete data?.[""] : null
    // dispatch({ action: "mutateSearchForm", data })
    dispatch({ action: "mutateSearchDefinationForm", data });


  }
  const onFilterFormSubmit = (data) => {
    // data.hasOwnProperty("") ? delete data?.[""] : null
    // dispatch({ action: "mutateFilterForm", data })
    dispatch({ action: "mutateFilterForm", data });
  }


  const propsForSearchForm = { SearchFormFields, onSearchFormSubmit, searchFormDefaultValues: formState?.ServiceDefinitionCriteria?.code[0], resetSearchFormDefaultValues: searchFormDefaultValues, onSearchFormReset }

  const propsForFilterForm = { FilterFormFields, onFilterFormSubmit, filterFormDefaultValues: formState?.filterForm, resetFilterFormDefaultValues: filterFormDefaultValues, onFilterFormReset }

  const propsForInboxTable = useInboxTableConfig({ ...{ parentRoute, onPageSizeChange, formState, totalCount: "", table: surveyList?.ServiceDefinition||[], noResultsMessage: "CS_SURVEYS_NOT_FOUND", dispatch, inboxStyles:{overflowX:"scroll", overflowY:"hidden"} } })

  const propsForInboxMobileCards = useInboxMobileCardsData({parentRoute, table:surveyList?.ServiceDefinition||[]})
  
  return <InboxComposer {...{ isSurveyListLoading, PropsForInboxLinks, ...propsForSearchForm, ...propsForFilterForm, propsForInboxMobileCards, propsForInboxTable, formState }}></InboxComposer>

}

export default Inbox