
import { Link } from "react-router-dom";
import _ from "lodash";
import React from "react";
import { Amount, LinkLabel } from "@egovernments/digit-ui-react-components";
import { useHistory } from "react-router-dom";

//create functions here based on module name set in mdms(eg->SearchProjectConfig)
//how to call these -> Digit?.Customizations?.[masterName]?.[moduleName]
// these functions will act as middlewares
var Digit = window.Digit || {};

function extractArrayByKey(arrayOfObjects, key) {
  // Use the map() function to extract the values based on the key
  return arrayOfObjects.map(function (object) {
    return object[key];
  });
}

function cleanObject(obj) {
  for (const key in obj) {
    if (obj.hasOwnProperty(key)) {
      if (Array.isArray(obj[key])) {
        if (obj[key].length === 0) {
          delete obj[key];
        }
      } else if (obj[key] === undefined || obj[key] === null || obj[key] === false || obj[key] === '' || (typeof obj[key] === 'object' && Object.keys(obj[key]).length === 0)) {
        delete obj[key];
      }
    }
  }
  return obj;
}

//this is a recursive function , test it out before you use this
function cleanObjectNested(obj) {
  for (const key in obj) {
    if (obj.hasOwnProperty(key)) {
      if (typeof obj[key] === 'object' && !Array.isArray(obj[key])) {
        // Recursively clean nested objects
        cleanObjectNested(obj[key]);
        // If the nested object becomes empty, remove it
        if (Object.keys(obj[key]).length === 0) {
          delete obj[key];
        }
      } else if (Array.isArray(obj[key]) && obj[key].length === 0) {
        // Remove empty arrays
        delete obj[key];
      } else if (
        obj[key] === undefined ||
        obj[key] === null ||
        obj[key] === false ||
        obj[key] === '' ||
        (typeof obj[key] === 'object' && Object.keys(obj[key]).length === 0)
      ) {
        // Remove falsy values (except 0)
        delete obj[key];
      }
    }
  }
  return obj;
}

const businessServiceMap = {
  tqm: "PQM"
};

const workflowStatusMap = {
  pendingResults: "PENDINGRESULTS",
  submit: "SUBMITTED",
};

const workflowActionMap = {
  update: "UPDATE_RESULT",
  submit: "SUBMIT_SAMPLE",
  schedule: "SCHEDULE"
};

const tqmRoleMapping = {
  plant: ["PQM_TP_OPERATOR"],
  ulb: ["PQM_ADMIN"]
}

const urls = {
  search: "/pqm/v1/_search"
}

export const UICustomizations = {
  urls,
  tqmRoleMapping,
  businessServiceMap,
  workflowStatusMap,
  workflowActionMap,
  TqmInboxConfig: {
    preProcess: (data, additionalDetails) => {
      //sample working code to stop calling inbox api if no plants are linked to this user
      if (Digit.SessionStorage.get("user_plants")?.filter(row => row.plantCode)?.length === 0 || !Digit.SessionStorage.get("user_plants")) {
        data.config.enabled = false
        return data
      }
      // const { processCodes, materialCodes, status, dateRange,sortOrder,limit,offset } = data.body.custom || {};

      //picking data from state always instead of config(issue -> first api call from state rather than config)
      const { processCodes, materialCodes, status, dateRange } = data?.state?.searchForm || {}
      const { sortOrder } = data?.state?.filterForm || {}
      const { limit, offset } = data?.state?.tableForm || {}
      //processcodes
      data.body.inbox.moduleSearchCriteria.processCodes = processCodes?.map(processCode => processCode.code)

      //materialcodes
      data.body.inbox.moduleSearchCriteria.materialCodes = materialCodes?.map(materialCode => materialCode.code)

      //status
      data.body.inbox.moduleSearchCriteria.wfStatus = status?.map(status => status.applicationStatus)

      //fromDate and toDate
      const { fromDate, toDate } = Digit.Utils.tqm.convertDateRangeToEpochObj(dateRange) || {}
      data.body.inbox.moduleSearchCriteria.fromDate = fromDate
      data.body.inbox.moduleSearchCriteria.toDate = toDate

      //sortOrder sortBy 

      data.body.inbox.moduleSearchCriteria.sortBy = "scheduledDate"
      data.body.inbox.moduleSearchCriteria.sortOrder = sortOrder?.value

      //limit offset

      cleanObject(data.body.inbox.processSearchCriteria)
      cleanObject(data.body.inbox.moduleSearchCriteria)

      const isMobile = window.Digit.Utils.browser.isMobile();
      if (isMobile) {
        data.body.inbox.limit = 100
        data.body.inbox.offset = 0
      } else {
        data.body.inbox.limit = limit
        data.body.inbox.offset = offset
      }

      //set tenantId
      data.body.inbox.tenantId = Digit.ULBService.getCurrentTenantId();
      data.body.inbox.processSearchCriteria.tenantId = Digit.ULBService.getCurrentTenantId();
      data.body.inbox.moduleSearchCriteria.tenantId = Digit.ULBService.getCurrentTenantId();

      // //testting
      // data.body.inbox.moduleSearchCriteria.ids = ["8"]

      //delete custom
      delete data.body.custom;

      const activePlantCode = Digit.SessionStorage.get("active_plant")?.plantCode ? [Digit.SessionStorage.get("active_plant")?.plantCode] : Digit.SessionStorage.get("user_plants")?.filter(row => row.plantCode)?.map(row => row.plantCode)
      if (activePlantCode?.length > 0) {
        data.body.inbox.moduleSearchCriteria.plantCodes = [...activePlantCode]
      }


      return data
    },
    populateStatusReqCriteria: () => {
      const tenantId = Digit.ULBService.getCurrentTenantId();

      return {
        url: "/egov-workflow-v2/egov-wf/businessservice/_search",
        params: { tenantId, businessServices: businessServiceMap?.tqm },
        body: {

        },
        changeQueryName: "setWorkflowStatus",
        config: {
          enabled: true,
          select: (data) => {
            const wfStates = data?.BusinessServices?.[0]?.states?.filter(state => state.applicationStatus && !state.isTerminateState
            )?.map(state => {
              return {
                i18nKey: `WF_STATUS_${businessServiceMap?.tqm}_${state?.applicationStatus}`,
                ...state
              }
            })
            return wfStates
          },
        },
      };
    },
    getCustomActionLabel: (obj, row) => {
      const type = obj.apiResponse?.ProcessInstance?.state?.applicationStatus
      switch (type) {
        case "SCHEDULED":
          return "TQM_INBOX_ACTION_UPDATE_STATUS"
        case "DRAFTED":
          return "TQM_INBOX_ACTION_UPDATE_DRAFT"
        case "PENDINGRESULTS":
          return "TQM_INBOX_ACTION_UPDATE_RESULTS"
        default:
          return "case_not_found"
      }

    },
    additionalCustomizations: (row, key, column, value, t, searchResult) => {
      switch (key) {
        case "TQM_INBOX_SLA":
          let sla = 0
          const currentDate = new Date();
          const targetTimestamp = row?.businessObject?.scheduledDate;
          const targetDate = new Date(targetTimestamp);
          const remainingSLA = targetDate - currentDate;
          sla = Math.floor(remainingSLA / (24 * 60 * 60 * 1000));
          if (!row?.businessObject?.scheduledDate) return t("ES_COMMON_NA")
          return Math.sign(sla) === -1 ? <span className="sla-cell-error">{Math.floor(sla)} {t("COMMON_DAYS_OVERDUE")}</span> : <span className="sla-cell-success">{sla} {t("COMMON_DAYS")}</span>;

        case "TQM_PENDING_DATE":
          return Digit.DateUtils.ConvertEpochToDate(value)

        case "TQM_TEST_ID":
          const isMobile = window.Digit.Utils.browser.isMobile();
          if (isMobile) {
            return <span>{value}</span>
          }
          return <span className="link">
            <Link
              to={`/${window.contextPath}/employee/tqm/test-details?id=${value}`}
            // to={`/${window.contextPath}/employee/tqm/inbox`}
            >
              {String(value ? (column.translate ? t(column.prefix ? `${column.prefix}${value}` : value) : value) : t("ES_COMMON_NA"))}
            </Link>
          </span>
        default:
          return "case_not_found"
      }
    },
    onCardActionClick: (obj) => {
      return `test-details?id=${obj?.apiResponse?.ProcessInstance?.businessId}`
    },
    onCardClick: (obj) => {
      return `test-details?id=${obj?.apiResponse?.ProcessInstance?.businessId}`
    },

  },
  TqmInboxConfigUlbAdmin: {
    populateMdmsv2SearchReqCriteria: ({ schemaCode }) => {
      const tenantId = Digit.ULBService.getCurrentTenantId();

      return {
        url: "/mdms-v2/v2/_search",
        params: {},
        body: {
          tenantId,
          MdmsCriteria: {
            tenantId: tenantId,
            schemaCode: schemaCode,
            isActive: true,
          },
        },
        changeQueryName: `mdms-v2-${schemaCode}`,
        config: {
          enabled: schemaCode ? true : false,
          select: (response) => {
            const { mdms } = response;
            //first filter with isActive
            //then make a data array with actual data
            //refer the "code" key in data(for now) and set options array , also set i18nKey in each object to show in UI
            const options = mdms?.map((row) => {
              return {
                i18nKey: Digit.Utils.locale.getTransformedLocale(`${row?.schemaCode}_${row?.data?.code}`),
                ...row.data,
              };
            });
            return options;
          },
        },
      };
    },
    preProcess: (data, additionalDetails) => {
      if (Digit.SessionStorage.get("user_plants")?.filter(row => row.plantCode)?.length === 0 || !Digit.SessionStorage.get("user_plants")) {
        data.config.enabled = false
        return data
      }
      // const { id,plantCodes:selectedPlantCodes,processCodes,stage, materialCodes, status } = data.body.custom || {};

      const { id, plantCodes: selectedPlantCodes } = data?.state?.searchForm || {}
      const { processCodes, stage, materialCodes, status } = data?.state?.filterForm || {}
      const { limit, offset } = data?.state?.tableForm || {}
      //ids
      data.body.inbox.moduleSearchCriteria.testIds = id ? [id] : null

      data.body.inbox.moduleSearchCriteria.plantCodes = Digit.SessionStorage.get("user_plants")?.filter(row => row?.plantCode)?.map(row => row?.plantCode)
      //plantCodes 
      if (selectedPlantCodes?.length > 0) {
        data.body.inbox.moduleSearchCriteria.plantCodes = selectedPlantCodes?.filter(row => row?.plantCode)?.map(row => row?.plantCode)
      }
      //stage
      data.body.inbox.moduleSearchCriteria.stageCodes = stage?.map(st => st.code)


      //materialcodes
      data.body.inbox.moduleSearchCriteria.materialCodes = materialCodes?.length > 0 ? materialCodes?.map(row => row?.code) : []

      //processcodes
      data.body.inbox.moduleSearchCriteria.processCodes = processCodes?.length > 0 ? processCodes?.map(row => row?.code) : []

      //status
      data.body.inbox.moduleSearchCriteria.status = status ? Object?.keys(status)?.filter(key => status[key]) : null

      const isMobile = window.Digit.Utils.browser.isMobile();
      if (isMobile) {
        data.body.inbox.limit = 100
        data.body.inbox.offset = 0
      } else {
        data.body.inbox.limit = limit
        data.body.inbox.offset = offset
      }

      cleanObject(data.body.inbox.processSearchCriteria)
      cleanObject(data.body.inbox.moduleSearchCriteria)

      //set tenantId
      data.body.inbox.tenantId = Digit.ULBService.getCurrentTenantId();
      data.body.inbox.processSearchCriteria.tenantId = Digit.ULBService.getCurrentTenantId();
      data.body.inbox.moduleSearchCriteria.tenantId = Digit.ULBService.getCurrentTenantId();

      //delete custom
      delete data.body.custom;

      return data
    },
    populateStatusReqCriteria: () => {
      const tenantId = Digit.ULBService.getCurrentTenantId();

      return {
        url: "/egov-workflow-v2/egov-wf/businessservice/_search",
        params: { tenantId, businessServices: businessServiceMap?.tqm },
        body: {

        },
        changeQueryName: "setWorkflowStatus",
        config: {
          enabled: true,
          select: (data) => {
            const wfStates = data?.BusinessServices?.[0]?.states?.filter(state => state.applicationStatus
            )?.map(state => {
              return {
                i18nKey: `WF_STATUS_${businessServiceMap?.tqm}_${state?.applicationStatus}`,
                ...state
              }
            })
            return wfStates
          },
        },
      };
    },
    getCustomActionLabel: (obj, row) => {
      return ""
    },
    additionalCustomizations: (row, key, column, value, t, searchResult) => {
      switch (key) {
        case "TQM_INBOX_SLA":
          let sla = 0
          const currentDate = new Date();
          const targetTimestamp = row?.businessObject?.scheduledDate;
          const targetDate = new Date(targetTimestamp);
          const remainingSLA = targetDate - currentDate;
          sla = Math.floor(remainingSLA / (24 * 60 * 60 * 1000));
          if (!row?.businessObject?.scheduledDate) return t("ES_COMMON_NA")
          return Math.sign(sla) === -1 ? <span className="sla-cell-error">{Math.floor(sla)} {t("COMMON_DAYS_OVERDUE")}</span> : <span className="sla-cell-success">{sla} {t("COMMON_DAYS")}</span>;

        case "TQM_PENDING_DATE":
          return Digit.DateUtils.ConvertEpochToDate(value)

        case "TQM_TEST_ID":
          return <span className="link">
            <Link
              to={`/${window.contextPath}/employee/tqm/view-test-results?tenantId=${Digit.ULBService.getCurrentTenantId()}&id=${value}&from=TQM_BREAD_INBOX`}
            >
              {String(value ? (column.translate ? t(column.prefix ? `${column.prefix}${value}` : value) : value) : t("ES_COMMON_NA"))}
            </Link>
          </span>

        default:
          return "case_not_found"
      }
    },
    populatePlantUsersReqCriteria: (props) => {
      const userInfo = Digit.UserService.getUser();
      const tenantId = Digit.ULBService.getCurrentTenantId();

      return {
        params: {},
        url: '/pqm-service/plant/user/v1/_search',
        body: {
          "plantUserSearchCriteria": {
            tenantId,
            // "plantCodes": [],
            "plantUserUuids": userInfo?.info?.uuid ? [userInfo?.info?.uuid] : [],
            "additionalDetails": {}
          },
          "pagination": {}
        },
        config: {
          select: (data) => {
            return Digit.SessionStorage.get("user_plants");
          }
        },
        changeQueryName: "setPlantUsersInboxDropdown"
      }
    },
    onCardClick: (obj) => {
      return `view-test-results?tenantId=${obj?.apiResponse?.businessObject?.tenantId}&id=${obj?.apiResponse?.businessObject?.testId}&from=TQM_BREAD_INBOX`
    },
    onCardActionClick: (obj) => {
      return `view-test-results?tenantId=${obj?.apiResponse?.businessObject?.tenantId}&id=${obj?.apiResponse?.businessObject?.testId}&from=TQM_BREAD_INBOX`
    },
    getCustomActionLabel: (obj, row) => {
      return "TQM_VIEW_TEST_DETAILS"
    },

  },
  SearchTestResults: {
    preProcess: (data, additionalDetails) => {

      // const { processCodes, materialCodes, testType, dateRange,sortOrder,limit,offset } = data.body.custom || {};

      const { processCodes, materialCodes, testType, dateRange } = data?.state?.searchForm || {}
      const { sortOrder } = data?.state?.filterForm || {}
      const { limit, offset } = data?.state?.tableForm || {}
      data.body.testSearchCriteria = {}
      data.body.pagination = {}

      //update testSearchCriteria

      //plantcodes
      // data.body.testSearchCriteria.plantCodes = plantCodes?.map(plantCode => plantCode.code)

      //processcodes
      data.body.testSearchCriteria.processCodes = processCodes?.map(processCode => processCode.code)

      //materialcodes
      data.body.testSearchCriteria.materialCodes = materialCodes?.map(materialCode => materialCode.code)
      //testType
      data.body.testSearchCriteria.testType = testType?.map(sourceType => sourceType.code)
      //dataRange //fromDate //toDate
      const { fromDate, toDate } = Digit.Utils.tqm.convertDateRangeToEpochObj(dateRange) || {}
      data.body.testSearchCriteria.fromDate = fromDate
      data.body.testSearchCriteria.toDate = toDate
      data.body.testSearchCriteria.wfStatus = ["SUBMITTED"];
      //sortOrder
      data.body.pagination.sortOrder = sortOrder?.value

      if (data.body.pagination.sortOrder) {
        data.body.pagination.sortBy = "scheduledDate"
      }

      if (data?.body?.testSearchCriteria?.testType?.length === 0 || !data?.body?.testSearchCriteria?.testType) {
        data.body.testSearchCriteria.testType = ["LAB_SCHEDULED", "IOT_SCHEDULED", "LAB_ADHOC"]
      }
      cleanObject(data.body.testSearchCriteria)
      cleanObject(data.body.pagination)

      //update pagination

      const isMobile = window.Digit.Utils.browser.isMobile();

      if (isMobile) {
        data.body.pagination.limit = 100
        data.body.pagination.offset = 0
      }
      else {
        data.body.pagination.limit = limit
        data.body.pagination.offset = offset
      }

      //delete custom
      delete data.body.custom;

      const activePlantCode = Digit.SessionStorage.get("active_plant")?.plantCode ? [Digit.SessionStorage.get("active_plant")?.plantCode] : Digit.SessionStorage.get("user_plants")?.filter(row => row.plantCode)?.map(row => row.plantCode)
      if (activePlantCode?.length > 0) {
        data.body.testSearchCriteria.plantCodes = [...activePlantCode]
      }

      return data
    },
    MobileDetailsOnClick: () => {
      return ""
    },
    onCardClick: (obj) => {
      return `summary?id=${obj?.apiResponse?.testId}&type=past`
    },
    onCardActionClick: (obj) => {
      return `summary?id=${obj?.apiResponse?.testId}&type=past`
    },
    getCustomActionLabel: (obj, row) => {
      return ""
    },
    additionalCustomizations: (row, key, column, value, t, searchResult) => {
      switch (key) {
        case "TQM_TEST_RESULTS":
          return value?.includes("PASS") ? <span className="sla-cell-success">{t(`TQM_TEST_RESULT_${value}`)}</span> : <span className="sla-cell-error">{t(`TQM_TEST_RESULT_${value}`)}</span>;

        case "TQM_TEST_SUBMITTED_DATE":
          return Digit.DateUtils.ConvertEpochToDate(value)

        case "TQM_TEST_ID":
          const isMobile = window.Digit.Utils.browser.isMobile();
          if (isMobile) {
            return <span>{value}</span>
          }
          return <span className="link">
            <Link
              to={`/${window.contextPath}/employee/tqm/summary?id=${value}&type=past`}
            >
              {String(value ? (column.translate ? t(column.prefix ? `${column.prefix}${value}` : value) : value) : t("ES_COMMON_NA"))}
            </Link>
          </span>
        default:
          return "case_not_found"
      }
    }
  },
  SearchTestResultsUlbAdmin: {
    preProcess: (data, additionalDetails) => {

      // const { id,plantCodes:selectedPlantCodes, processCodes, testType, dateRange } = data.body.custom || {};

      const { id, plantCodes: selectedPlantCodes, processCodes, testType, dateRange } = data?.state?.searchForm || {};
      const { limit, offset } = data?.state?.tableForm || {}
      data.body.testSearchCriteria = {}

      //update testSearchCriteria

      //test id
      // data.body.testSearchCriteria.testIds = id ? [id] : []
      //test id with part search enabled 
      data.body.testSearchCriteria.testId = id ? id : ""
      //plantcodes
      // data.body.testSearchCriteria.plantCodes = plantCodes?.map(plantCode => plantCode.code)

      data.body.testSearchCriteria.plantCodes = Digit.SessionStorage.get("user_plants")?.filter(row => row?.plantCode)?.map(row => row?.plantCode)
      //plantCodes 
      if (selectedPlantCodes?.length > 0) {
        data.body.testSearchCriteria.plantCodes = selectedPlantCodes?.filter(row => row?.plantCode)?.map(row => row?.plantCode)
      }
      data.body.testSearchCriteria.wfStatus = ["SUBMITTED"];
      //processcodes
      data.body.testSearchCriteria.processCodes = processCodes?.map(processCode => processCode.code)
      //testType
      data.body.testSearchCriteria.testType = testType?.map(sourceType => sourceType.code)
      //dataRange //fromDate //toDate
      const { fromDate, toDate } = Digit.Utils.tqm.convertDateRangeToEpochObj(dateRange) || {}
      data.body.testSearchCriteria.fromDate = fromDate
      data.body.testSearchCriteria.toDate = toDate

      //tenantId
      data.body.testSearchCriteria.tenantId = Digit.ULBService.getCurrentTenantId();

      //sorting by scheduled date
      //   if(data.body.pagination){
      //   data.body.pagination.sortBy = "scheduledDate"
      //   data.body.pagination.sortOrder = "DESC"
      // }

      const isMobile = window.Digit.Utils.browser.isMobile();
      if (isMobile) {
        data.body.pagination = {}
        data.body.pagination.limit = 100
        data.body.pagination.offset = 0
      } else {
        data.body.pagination = {}
        data.body.pagination.limit = limit
        data.body.pagination.offset = offset
      }

      cleanObject(data.body.testSearchCriteria)
      cleanObject(data.body.pagination)

      //delete custom
      delete data.body.custom;
      return data
    },
    MobileDetailsOnClick: () => {
      return ""
    },
    onCardClick: (obj, row) => {
      return `view-test-results?tenantId=${obj?.apiResponse?.tenantId}&id=${obj?.apiResponse?.testId}&from=TQM_BREAD_PAST_TESTS&type=${obj?.apiResponse?.testType === "LAB_ADHOC" ? "adhoc" : ""}`

    },
    onCardActionClick: (obj, row) => {
      return `view-test-results?tenantId=${obj?.apiResponse?.tenantId}&id=${obj?.apiResponse?.testId}&from=TQM_BREAD_PAST_TESTS&type=${obj?.apiResponse?.testType === "LAB_ADHOC" ? "adhoc" : ""}`
    },
    getCustomActionLabel: (obj, row) => {
      return ""
    },
    additionalCustomizations: (row, key, column, value, t, searchResult) => {
      switch (key) {
        case "TQM_TEST_RESULTS":
          return value?.includes("PASS") ? <span className="sla-cell-success">{t(`TQM_TEST_RESULT_${value}`)}</span> : <span className="sla-cell-error">{t(`TQM_TEST_RESULT_${value}`)}</span>;

        case "ES_TQM_TEST_DATE":
          return Digit.DateUtils.ConvertEpochToDate(value)

        case "TQM_TEST_ID":
          return <span className="link">
            <Link
              to={`/${window.contextPath}/employee/tqm/view-test-results?tenantId=${Digit.ULBService.getCurrentTenantId()}&id=${value}&from=TQM_BREAD_PAST_TESTS&type=${row?.testType === "LAB_ADHOC" ? "adhoc" : ""}`}
            >
              {String(value ? (column.translate ? t(column.prefix ? `${column.prefix}${value}` : value) : value) : t("ES_COMMON_NA"))}
            </Link>
          </span>

        default:
          return "case_not_found"
      }
    }
  },
  SensorConfig: {
    preProcess: (data, additionalDetails) => {

      const { processCodes, materialCodes, status, dateRange, sortOrder, limit, offset } = data.body.custom || {};

      //processcodes
      data.body.inbox.moduleSearchCriteria.processCodes = processCodes?.map(processCode => processCode.code)

      //materialcodes
      data.body.inbox.moduleSearchCriteria.materialCodes = materialCodes?.map(materialCode => materialCode.code)

      //status
      data.body.inbox.moduleSearchCriteria.status = status?.map(status => status.applicationStatus)

      //fromDate and toDate
      const { fromDate, toDate } = Digit.Utils.tqm.convertDateRangeToEpochObj(dateRange) || {}
      data.body.inbox.moduleSearchCriteria.fromDate = fromDate
      data.body.inbox.moduleSearchCriteria.toDate = toDate

      //sortOrder sortBy 

      data.body.inbox.moduleSearchCriteria.sortBy = "createdTime"
      data.body.inbox.moduleSearchCriteria.sortOrder = sortOrder?.value

      //limit offset

      cleanObject(data.body.inbox.processSearchCriteria)
      cleanObject(data.body.inbox.moduleSearchCriteria)


      data.body.inbox.limit = 100
      data.body.inbox.offset = 0


      //set tenantId
      data.body.inbox.tenantId = Digit.ULBService.getCurrentTenantId();
      data.body.inbox.processSearchCriteria.tenantId = Digit.ULBService.getCurrentTenantId();


      // //testting
      // data.body.inbox.moduleSearchCriteria.ids = ["8"]

      //delete custom
      delete data.body.custom;

      return data
    },
    populateStatusReqCriteria: () => {
      const tenantId = Digit.ULBService.getCurrentTenantId();

      return {
        url: "/egov-workflow-v2/egov-wf/businessservice/_search",
        params: { tenantId, businessServices: businessServiceMap?.tqm },
        body: {

        },
        changeQueryName: "setWorkflowStatus",
        config: {
          enabled: true,
          select: (data) => {
            const wfStates = data?.BusinessServices?.[0]?.states?.filter(state => state.applicationStatus
            )?.map(state => {
              return {
                i18nKey: `WF_STATUS_${businessServiceMap?.tqm}_${state?.applicationStatus}`,
                ...state
              }
            })
            return wfStates
          },
        },
      };
    },
    getCustomActionLabel: (obj, row) => {
      return ""
    },
    additionalCustomizations: (row, key, column, value, t, searchResult) => {
      switch (key) {
        case "TQM_INBOX_SLA":
          return value > 0 ? <span className="sla-cell-success">{value}</span> : <span className="sla-cell-error">{value}</span>;

        case "TQM_PENDING_DATE":
          return Digit.DateUtils.ConvertEpochToDate(value)
        default:
          return "case_not_found"
      }
    }

  }

}