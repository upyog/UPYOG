import { Link } from "react-router-dom";
import _ from "lodash";

//create functions here based on module name set in mdms(eg->SearchProjectConfig)
//how to call these -> Digit?.Customizations?.[masterName]?.[moduleName]
// these functions will act as middlewares
const Digit = window.Digit || {};



const businessServiceMap = {

  "muster roll": "MR"
};

const inboxModuleNameMap = {
  "muster-roll-approval": "muster-roll-service",
};

const customizeMusterRollId = (value, column, t) => {
  let displayValue = t("ES_COMMON_NA");
  if (value) {
    if (column.translate) {
      const translationKey = column.prefix ? `${column.prefix}${value}` : value;
      displayValue = t(translationKey);
    } else {
      displayValue = value;
    }
  }

  return (
    <span className="link">
      <Link
        to={`/${window.contextPath}/employee/attendencemgmt/view-attendance?tenantId=${Digit.ULBService.getCurrentTenantId()}&musterRollNumber=${value}`}
      >
        {String(displayValue)}
      </Link>
    </span>
  );
};

const customizeWageSeekerId = (row, column, value, t) => {
  let displayValue = t("ES_COMMON_NA");
  if (value) {
    if (column.translate) {
      const translationKey = column.prefix ? `${column.prefix}${value}` : value;
      displayValue = t(translationKey);
    } else {
      displayValue = value;
    }
  }

  return (
    <span className="link">
      <Link to={`/${window.contextPath}/employee/masters/view-wageseeker?tenantId=${row?.tenantId}&individualId=${value}`}>
        {String(displayValue)}
      </Link>
    </span>
  );
};

const customizeSocialCategory = (value, t) => {
  return value ? <span style={{ whiteSpace: "nowrap" }}>{String(t(`MASTERS_${value}`))}</span> : t("ES_COMMON_NA");
};

const customizeProfileCity = (value, t) => {
  return value ? <span style={{ whiteSpace: "nowrap" }}>{String(t(Digit.Utils.locale.getCityLocale(value)))}</span> : t("ES_COMMON_NA");
};

const customizeWard = (value, tenantId, t) => {
  return value ? (
    <span style={{ whiteSpace: "nowrap" }}>{String(t(Digit.Utils.locale.getMohallaLocale(value, tenantId)))}</span>
  ) : (
    t("ES_COMMON_NA")
  );
};

const customizeLocality = (value, tenantId, t) => {
  return value ? (
    <span style={{ whiteSpace: "break-spaces" }}>{String(t(Digit.Utils.locale.getMohallaLocale(value, tenantId)))}</span>
  ) : (
    t("ES_COMMON_NA")
  );
};

const wageSeekerCustomizers = {
  MASTERS_WAGESEEKER_ID: (row, column, value, t) => customizeWageSeekerId(row, column, value, t),
  MASTERS_SOCIAL_CATEGORY: (row, column, value, t) => customizeSocialCategory(value, t),
  CORE_COMMON_PROFILE_CITY: (row, column, value, t) => customizeProfileCity(value, t),
  MASTERS_WARD: (row, column, value, t) => customizeWard(value, row?.tenantId, t),
  MASTERS_LOCALITY: (row, column, value, t) => customizeLocality(value, row?.tenantId, t),
};

const customizeAttendanceWeek = (value) => {
  const week = `${Digit.DateUtils.ConvertTimestampToDate(value?.startDate, "dd/MM/yyyy")}-${Digit.DateUtils.ConvertTimestampToDate(
    value?.endDate,
    "dd/MM/yyyy"
  )}`;
  return <div>{week}</div>;
};

const customizeNoOfIndividuals = (value) => {
  return <div>{value?.length}</div>;
};

const customizeAmountInRs = (value, t) => {
  return <span>{value ? Digit.Utils.dss.formatterWithoutRound(value, "number") : t("ES_COMMON_NA")}</span>;
};

const customizeSla = (value, t) => {
  return Number.parseInt(value, 10) > 0 ? (
    <span className="sla-cell-success">{t(value) || ""}</span>
  ) : (
    <span className="sla-cell-error">{t(value) || ""}</span>
  );
};

const customizeWorkflowStates = (value, t) => {
  return <span>{t(`WF_MUSTOR_${value}`)}</span>;
};

const attendanceCustomizers = {
  ATM_MUSTER_ROLL_ID: (row, column, value, t) => customizeMusterRollId(value, column, t),
  ATM_ATTENDANCE_WEEK: (row, column, value, t) => customizeAttendanceWeek(value),
  ATM_NO_OF_INDIVIDUALS: (row, column, value, t) => customizeNoOfIndividuals(value),
  ATM_AMOUNT_IN_RS: (row, column, value, t) => customizeAmountInRs(value, t),
  ATM_SLA: (row, column, value, t) => customizeSla(value, t),
  COMMON_WORKFLOW_STATES: (row, column, value, t) => customizeWorkflowStates(value, t),
};

export const UICustomizations = {
  businessServiceMap,
  updatePayload: (applicationDetails, data, action, businessService) => {

    if (businessService === businessServiceMap.estimate) {
      const workflow = {
        comment: data.comments,
        documents: data?.documents?.map((document) => {
          return {
            documentType: action?.action + " DOC",
            fileName: document?.[1]?.file?.name,
            fileStoreId: document?.[1]?.fileStoreId?.fileStoreId,
            documentUid: document?.[1]?.fileStoreId?.fileStoreId,
            tenantId: document?.[1]?.fileStoreId?.tenantId,
          };
        }),
        assignees: data?.assignees?.uuid ? [data?.assignees?.uuid] : null,
        action: action.action,
      };
      //filtering out the data
      Object.keys(workflow).forEach((key, index) => {
        if (!workflow[key] || workflow[key]?.length === 0) delete workflow[key];
      });

      return {
        estimate: applicationDetails,
        workflow,
      };
    }
    if (businessService === businessServiceMap.contract) {
      const workflow = {
        comment: data?.comments,
        documents: data?.documents?.map((document) => {
          return {
            documentType: action?.action + " DOC",
            fileName: document?.[1]?.file?.name,
            fileStoreId: document?.[1]?.fileStoreId?.fileStoreId,
            documentUid: document?.[1]?.fileStoreId?.fileStoreId,
            tenantId: document?.[1]?.fileStoreId?.tenantId,
          };
        }),
        assignees: data?.assignees?.uuid ? [data?.assignees?.uuid] : null,
        action: action.action,
      };
      //filtering out the data
      Object.keys(workflow).forEach((key, index) => {
        if (!workflow[key] || workflow[key]?.length === 0) delete workflow[key];
      });

      return {
        contract: applicationDetails,
        workflow,
      };
    }
    if (businessService === businessServiceMap?.["muster roll"]) {
      const workflow = {
        comment: data?.comments,
        documents: data?.documents?.map((document) => {
          return {
            documentType: action?.action + " DOC",
            fileName: document?.[1]?.file?.name,
            fileStoreId: document?.[1]?.fileStoreId?.fileStoreId,
            documentUid: document?.[1]?.fileStoreId?.fileStoreId,
            tenantId: document?.[1]?.fileStoreId?.tenantId,
          };
        }),
        assignees: data?.assignees?.uuid ? [data?.assignees?.uuid] : null,
        action: action.action,
      };
      //filtering out the data
      Object.keys(workflow).forEach((key, index) => {
        if (!workflow[key] || workflow[key]?.length === 0) delete workflow[key];
      });

      return {
        musterRoll: applicationDetails,
        workflow,
      };
    }
    if (businessService === businessServiceMap?.["works.purchase"]) {
      const workflow = {
        comment: data.comments,
        documents: data?.documents?.map((document) => {
          return {
            documentType: action?.action + " DOC",
            fileName: document?.[1]?.file?.name,
            fileStoreId: document?.[1]?.fileStoreId?.fileStoreId,
            documentUid: document?.[1]?.fileStoreId?.fileStoreId,
            tenantId: document?.[1]?.fileStoreId?.tenantId,
          };
        }),
        assignees: data?.assignees?.uuid ? [data?.assignees?.uuid] : null,
        action: action.action,
      };
      //filtering out the data
      Object.keys(workflow).forEach((key, index) => {
        if (!workflow[key] || workflow[key]?.length === 0) delete workflow[key];
      });

      const additionalFieldsToSet = {
        projectId: applicationDetails.additionalDetails.projectId,
        invoiceDate: applicationDetails.billDate,
        invoiceNumber: applicationDetails.referenceId.split('_')?.[1],
        contractNumber: applicationDetails.referenceId.split('_')?.[0],
        documents: applicationDetails.additionalDetails.documents
      }
      return {
        bill: { ...applicationDetails, ...additionalFieldsToSet },
        workflow,
      };
    }
  },
  enableModalSubmit: (businessService, action, setModalSubmit, data) => {
    if (businessService === businessServiceMap?.["muster roll"] && action.action === "APPROVE") {
      setModalSubmit(data?.acceptTerms)
    }
  },
  enableHrmsSearch: (businessService, action) => {
    if (businessService === businessServiceMap.estimate) {
      return action.action.includes("TECHNICALSANCTION") || action.action.includes("VERIFYANDFORWARD");
    }
    if (businessService === businessServiceMap.contract) {
      return action.action.includes("VERIFY_AND_FORWARD");
    }
    if (businessService === businessServiceMap?.["muster roll"]) {
      return action.action.includes("VERIFY");
    }
    if (businessService === businessServiceMap?.["works.purchase"]) {
      return action.action.includes("VERIFY_AND_FORWARD")
    }
    return false;
  },
  getBusinessService: (moduleCode) => {
    if (moduleCode?.includes("estimate")) {
      return businessServiceMap?.estimate;
    } else if (moduleCode?.includes("contract")) {
      return businessServiceMap?.contract;
    } else if (moduleCode?.includes("muster roll")) {
      return businessServiceMap?.["muster roll"];
    }
    else if (moduleCode?.includes("works.purchase")) {
      return businessServiceMap?.["works.purchase"];
    }
    else if (moduleCode?.includes("works.wages")) {
      return businessServiceMap?.["works.wages"];
    }
    else if (moduleCode?.includes("works.supervision")) {
      return businessServiceMap?.["works.supervision"];
    }
    else {
      return businessServiceMap;
    }
  },
  getInboxModuleName: (moduleCode) => {
    if (moduleCode?.includes("estimate")) {
      return inboxModuleNameMap?.estimate;
    } else if (moduleCode?.includes("contract")) {
      return inboxModuleNameMap?.contracts;
    } else if (moduleCode?.includes("attendence")) {
      return inboxModuleNameMap?.attendencemgmt;
    } else {
      return inboxModuleNameMap;
    }
  },

  AttendanceInboxConfig: {
    preProcess: (data) => {

      //set tenantId
      data.body.inbox.tenantId = Digit.ULBService.getCurrentTenantId();
      data.body.inbox.processSearchCriteria.tenantId = Digit.ULBService.getCurrentTenantId();

      const musterRollNumber = data?.body?.inbox?.moduleSearchCriteria?.musterRollNumber?.trim();
      if (musterRollNumber) data.body.inbox.moduleSearchCriteria.musterRollNumber = musterRollNumber

      const attendanceRegisterName = data?.body?.inbox?.moduleSearchCriteria?.attendanceRegisterName?.trim();
      if (attendanceRegisterName) data.body.inbox.moduleSearchCriteria.attendanceRegisterName = attendanceRegisterName

      // deleting them for now(assignee-> need clarity from pintu,ward-> static for now,not implemented BE side)
      const assignee = _.clone(data.body.inbox.moduleSearchCriteria.assignee);
      delete data.body.inbox.moduleSearchCriteria.assignee;
      if (assignee?.code === "ASSIGNED_TO_ME") {
        data.body.inbox.moduleSearchCriteria.assignee = Digit.UserService.getUser().info.uuid;
      }

      //cloning locality and workflow states to format them

      let selectedOrg = _.clone(data.body.inbox.moduleSearchCriteria.orgId ? data.body.inbox.moduleSearchCriteria.orgId : null);
      delete data.body.inbox.moduleSearchCriteria.orgId;
      if (selectedOrg) {
        data.body.inbox.moduleSearchCriteria.orgId = selectedOrg?.[0]?.applicationNumber;
      }


      let states = _.clone(data.body.inbox.moduleSearchCriteria.state ? data.body.inbox.moduleSearchCriteria.state : []);
      let ward = _.clone(data.body.inbox.moduleSearchCriteria.ward ? data.body.inbox.moduleSearchCriteria.ward : []);
      delete data.body.inbox.moduleSearchCriteria.state;
      delete data.body.inbox.moduleSearchCriteria.ward;

      states = Object.keys(states)?.filter((key) => states[key]);
      ward = ward?.map((row) => row?.code);


      // //adding formatted data to these keys
      if (states.length > 0) data.body.inbox.moduleSearchCriteria.status = states;
      if (ward.length > 0) data.body.inbox.moduleSearchCriteria.ward = ward;
      const projectType = _.clone(data.body.inbox.moduleSearchCriteria.projectType ? data.body.inbox.moduleSearchCriteria.projectType : {});
      if (projectType?.code) data.body.inbox.moduleSearchCriteria.projectType = projectType.code;

      //adding tenantId to moduleSearchCriteria
      data.body.inbox.moduleSearchCriteria.tenantId = Digit.ULBService.getCurrentTenantId();

      //setting limit and offset becoz somehow they are not getting set in muster inbox 
      data.body.inbox.limit = data.state.tableForm.limit
      data.body.inbox.offset = data.state.tableForm.offset
      delete data.state
      return data;
    },
    postProcess: (responseArray, uiConfig) => {
      const statusOptions = responseArray?.statusMap
        ?.filter((item) => item.applicationstatus)
        ?.map((item) => ({ code: item.applicationstatus, i18nKey: `COMMON_MASTERS_${item.applicationstatus}` }));
      if (uiConfig?.type === "filter") {
        let fieldConfig = uiConfig?.fields?.filter((item) => item.type === "dropdown" && item.populators.name === "musterRollStatus");
        if (fieldConfig.length) {
          fieldConfig[0].populators.options = statusOptions;
        }
      }
    },
    additionalCustomizations: (row, key, column, value, t, searchResult) => {
      const customizer = attendanceCustomizers[key];
      if (customizer) {
        return customizer(row, column, value, t);
      }
      //added this in case we change the key and not updated here , it'll throw that nothing was returned from cell error if that case is not handled here. To prevent that error putting this default
      return <span>{t(`CASE_NOT_HANDLED`)}</span>
    },
    MobileDetailsOnClick: (row, tenantId) => {
      let link;
      Object.keys(row).map((key) => {
        if (key === "ATM_MUSTER_ROLL_ID")
          link = `/${window.contextPath}/employee/attendencemgmt/view-attendance?tenantId=${tenantId}&musterRollNumber=${row[key]}`;
      });
      return link;
    },
    populateReqCriteria: () => {
      const tenantId = Digit.ULBService.getCurrentTenantId();
      return {
        url: "/org-services/organisation/v1/_search",
        params: { limit: 50, offset: 0 },
        body: {
          SearchCriteria: {
            tenantId: tenantId,
            functions: {
              type: "CBO"
            }
          },
        },
        config: {
          enabled: true,
          select: (data) => {
            return data?.organisations;
          },
        },
      };
    },
  },
  SearchWageSeekerConfig: {
    customValidationCheck: (data) => {
      //checking both to and from date are present
      const { createdFrom, createdTo } = data;
      if ((createdFrom === "" && createdTo !== "") || (createdFrom !== "" && createdTo === ""))
        return { warning: true, label: "ES_COMMON_ENTER_DATE_RANGE" };

      return null;
    },
    preProcess: (data) => {
      data.params = { ...data.params, tenantId: Digit.ULBService.getCurrentTenantId() };

      let requestBody = { ...data.body.Individual };
      const pathConfig = {
        name: "name.givenName",
      };
      const dateConfig = {
        createdFrom: "daystart",
        createdTo: "dayend",
      };
      const selectConfig = {
        wardCode: "wardCode[0].code",
        socialCategory: "socialCategory.code",
      };
      const textConfig = ["name", "individualId"]
      let Individual = Object.keys(requestBody)
        .map((key) => {
          if (selectConfig[key]) {
            requestBody[key] = _.get(requestBody, selectConfig[key], null);
          } else if (typeof requestBody[key] == "object") {
            requestBody[key] = requestBody[key]?.code;
          } else if (textConfig?.includes(key)) {
            requestBody[key] = requestBody[key]?.trim()
          }
          return key;
        })
        .filter((key) => requestBody[key])
        .reduce((acc, curr) => {
          if (pathConfig[curr]) {
            _.set(acc, pathConfig[curr], requestBody[curr]);
          } else if (dateConfig[curr]?.includes("day")) {
            _.set(acc, curr, Digit.Utils.date.convertDateToEpoch(requestBody[curr], dateConfig[curr]));
          } else {
            _.set(acc, curr, requestBody[curr]);
          }
          return acc;
        }, {});

      data.body.Individual = { ...Individual };
      return data;
    },
    additionalCustomizations: (row, key, column, value, t, searchResult) => {
      const customizer = wageSeekerCustomizers[key];
      if (customizer) {
        return customizer(row, column, value, t);
      }
      return t("ES_COMMON_NA");
    },
    MobileDetailsOnClick: (row, tenantId) => {
      let link;
      Object.keys(row).map((key) => {
        if (key === "MASTERS_WAGESEEKER_ID")
          link = `/${window.contextPath}/employee/masters/view-wageseeker?tenantId=${tenantId}&wageseekerId=${row[key]}`;
      });
      return link;
    },
    additionalValidations: (type, data, keys) => {
      if (type === "date") {
        return data[keys.start] && data[keys.end] ? () => new Date(data[keys.start]).getTime() <= new Date(data[keys.end]).getTime() : true;
      }
    }
  },
};
