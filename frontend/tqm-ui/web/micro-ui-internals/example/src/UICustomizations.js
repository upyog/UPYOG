import React from "react";
import { Link } from "react-router-dom";
import _ from "lodash";
import { useTranslation } from "react-i18next";

//create functions here based on module name set in mdms(eg->SearchProjectConfig)
//how to call these -> Digit?.Customizations?.[masterName]?.[moduleName]
// these functions will act as middlewares
var Digit = window.Digit || {};

// const businessServiceMap = {
//   estimate: "ESTIMATE",
//   contracts: "CONTRACT",
//   attendencemgmt: "MR",
//   expenditure:{
//     WAGE_BILL:"EXPENSE.WAGE",
//     PURCHASE_BILL:"EXPENSE.PURCHASE",
//     SUPERVISION_BILL:"EXPENSE.SUPERVISION"
//   }
// };

const GetSlaCell = (value) => {
  if (value === "-") return <span className="sla-cell-success">-</span>;
  if (isNaN(value)) return <span className="sla-cell-success">0</span>;
  return value < 0 ? <span className="sla-cell-error">{value}</span> : <span className="sla-cell-success">{value}</span>;
};

export const UICustomizations = {
  FSMInboxConfig: {
    preProcess: (data) => {
      //fetch everything from data.state instead of data.body
      
      const {locality:localityFromState,state:stateFromState} = data?.state?.filterForm || {}
      const {applicationNos,mobileNumber} = data?.state?.searchForm || {}
      const userInfo = Digit.UserService.getUser();
      const userRoles = userInfo.info.roles.map((roleData) => roleData.code);
      //set tenantId
      const tenantId = Digit.ULBService.getCurrentTenantId();
      data.body.inbox.tenantId = tenantId;
      data.body.inbox.processSearchCriteria.tenantId = tenantId;
      data.body.inbox.moduleSearchCriteria.tenantId = tenantId;
      // // deleting them for now(assignee-> need clarity from pintu,ward-> static for now,not implemented BE side)
      // const assignee = _.clone(data.body.inbox.moduleSearchCriteria.assignee);
      // delete data.body.inbox.moduleSearchCriteria.assignee;
      // if (assignee?.code === "ASSIGNED_TO_ME") {
      if (userRoles.includes("FSM_DSO")) {
        data.body.inbox.moduleSearchCriteria.assignee = JSON.parse(localStorage.getItem("user-info")).uuid;
      }
      // } else {
      //   delete data.body.inbox.moduleSearchCriteria.assignedToMe;
      // }

      // cloning locality and workflow states to format them
      let locality = _.clone(localityFromState?.length>0 ? localityFromState : []);
      let states = _.clone(stateFromState ? stateFromState : []);
      delete data.body.inbox.moduleSearchCriteria.state;

      states = Object.keys(states)
        ?.filter((key) => states[key])
        .flatMap((i) => i.split(", "));
      locality = locality?.map((row) => row?.code);
      states.length > 0 ? (data.body.inbox.moduleSearchCriteria.status = states) : delete data.body.inbox.moduleSearchCriteria.status;
      locality.length > 0 ? (data.body.inbox.moduleSearchCriteria.locality = locality) : delete data.body.inbox.moduleSearchCriteria.locality;

      if(applicationNos) {
        data.body.inbox.moduleSearchCriteria.applicationNos = applicationNos
      }
      if(mobileNumber) {
        data.body.inbox.moduleSearchCriteria.mobileNumber = mobileNumber
      }
      return data;
    },
    additionalCustomizations: (row, key, column, value, t, searchResult) => {
      const tenantId = Digit.ULBService.getCurrentTenantId();
      switch (key) {
        case "CS_FILE_DESLUDGING_APPLICATION_NO":
          return (
            <span className="link">
              <Link to={`/${window.contextPath}/employee/fsm/application-details/${row?.businessObject?.applicationNo}`}>{value}</Link>
            </span>
          );
        case "ES_INBOX_APPLICATION_DATE":
          value = new Date(value);
          return <span>{`${value.getDate()}/${value.getMonth() + 1}/${value.getFullYear()}`}</span>;
        case "ES_INBOX_LOCALITY":
          return value ? <span>{t(Digit.Utils.locale.getRevenueLocalityCode(value, tenantId))}</span> : <span>{t("NA")}</span>;
        case "ES_INBOX_STATUS":
          return <span>{t(value)}</span>;
        case "ES_INBOX_SLA_DAYS_REMAINING":
          return GetSlaCell(value);
        default:
          return t("ES_COMMON_NA");
      }
    },
    localitydropdown: () => {
      const tenantId = Digit.ULBService.getCurrentTenantId();

      return {
        url: "/egov-location/location/v11/boundarys/_search",
        params: { hierarchyTypeCode: "ADMIN", boundaryType: "Locality", tenantId: tenantId },
        body: {},
        config: {
          enabled: true,
          select: (data) => {
            const localities = [];
            data?.TenantBoundary[0]?.boundary.forEach((item) => {
              localities.push({ code: item.code, name: item.name, i18nKey: `${tenantId.replace(".", "_").toUpperCase()}_REVENUE_${item?.code}` });
            });
            return localities;
          },
        },
      };
    },
  },
  FSMSearchConfig: {
    preProcess: (data) => {
      //set tenantId
      const tenantId = Digit.ULBService.getCurrentTenantId();
      data.params.tenantId = tenantId;
      data.params.applicationStatus = data.params.applicationStatus?.[0].name;
      if (data?.params?.fromDate || data?.params?.toDate) {
        const createdFrom = Digit.Utils.pt.convertDateToEpoch(data.params?.fromDate);
        const createdTo = Digit.Utils.pt.convertDateToEpoch(data.params?.toDate);
        data.params.fromDate = createdFrom;
        data.params.toDate = createdTo;
      }
      return data;
    },
    additionalCustomizations: (row, key, column, value, t, searchResult) => {
      const tenantId = Digit.ULBService.getCurrentTenantId();

      switch (key) {
        case "ES_INBOX_APPLICATION_NO":
          return (
            <span className="link">
              <Link to={`/${window.contextPath}/employee/fsm/application-details/${value}`}>{value}</Link>
            </span>
          );
        case "ES_APPLICATION_DETAILS_PROPERTY_TYPE":
          return <span>{t(`PROPERTYTYPE_MASTERS_${value.split(".")[0]}`)}</span>;
        case "ES_APPLICATION_DETAILS_PROPERTY_SUB-TYPE":
          return <span>{t(`PROPERTYTYPE_MASTERS_${row.propertyUsage}`)}</span>;
        case "ES_INBOX_STATUS":
          return <span>{t(value)}</span>;
        default:
          return t("ES_COMMON_NA");
      }
    },
    getApplicationStatus: () => {
      const tenantId = Digit.ULBService.getCurrentTenantId();
      return {
        url: "/egov-workflow-v2/egov-wf/businessservice/_search",
        params: { tenantId: tenantId, businessServices: "FSM,FSM_POST_PAY_SERVICE,PAY_LATER_SERVICE,FSM_ADVANCE_PAY_SERVICE,FSM_ZERO_PAY_SERVICE" },
        body: {},
        config: {
          enabled: true,
          select: (data) => {
            const DSO = Digit.UserService.hasAccess(["FSM_DSO"]);
            const workflowOrder = [
              { name: "CREATED" },
              { name: "PENDING_APPL_FEE_PAYMENT" },
              { name: "ASSING_DSO" },
              { name: "PENDING_DSO_APPROVAL" },
              { name: "DSO_REJECTED" },
              { name: "DSO_INPROGRESS" },
              { name: "REJECTED" },
              { name: "CANCELED" },
              { name: "COMPLETED" },
              { name: "CITIZEN_FEEDBACK_PENDING" },
              { name: "DISPOSAL_IN_PROGRESS" },
            ];
            const allowedStatusForDSO = [{ name: "PENDING_DSO_APPROVAL" }, { name: "DSO_INPROGRESS" }, { name: "COMPLETED" }, { name: "DSO_REJECTED" }];

            return DSO ? allowedStatusForDSO : workflowOrder;
          },
        },
      };
    },
    additionalValidations: (type, data, keys) => {
      if (type === "date") {
        return data.fromDate && data.toDate ? () => new Date(data.fromDate).getTime() < new Date(data.toDate).getTime() : true;
      }
    },
    customValidationCheck: (data) => {
      //checking both to and from date are present
      const { fromDate, toDate } = data;
      if ((fromDate === "" && toDate !== "") || (fromDate !== "" && toDate === "")) return { warning: true, label: "ES_COMMON_ENTER_DATE_RANGE" };

      return false;
    },
  },
  SearchVendor: {
    populateReqCriteria: () => {
      const tenantId = Digit.ULBService.getCurrentTenantId();

      return {
        url: "/vendor/v1/_search",
        params: { tenantId, sortBy: "name", status: "ACTIVE" },
        body: {},
        config: {
          enabled: true,
          select: (data) => {
            return data?.vendor;
          },
        },
      };
    },
  },
  VehicleAlertsConfig: {
    vehicleListDropdown: () => {
      const tenantId = Digit.ULBService.getCurrentTenantId();

      return {
        url: "/vehicle/v1/_search",
        params: { tenantId: tenantId, sortBy: "registrationNumber", sortOrder: "ASC", status: "ACTIVE" },
        body: {},
        config: {
          enabled: true,
          select: (data) => {
            const vehicleLists = [...data?.vehicle];
            return vehicleLists;
          },
        },
      };
    },
  },
};
